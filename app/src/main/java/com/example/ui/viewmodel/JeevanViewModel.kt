package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.JeevanDatabase
import com.example.data.entity.*
import com.example.data.repository.JeevanRepository
import com.example.network.GeminiNetworkClient
import com.example.service.TimerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class JeevanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JeevanRepository
    
    // --- Room flow states ---
    val transactions: StateFlow<List<Transaction>>
    val careerProgress: StateFlow<List<CareerProgress>>
    val healthLogs: StateFlow<List<HealthLog>>
    val userProfile: StateFlow<UserProfile>
    val subtopicsProgress: StateFlow<List<SubtopicProgress>>
    val newsBookmarks: StateFlow<List<NewsBookmark>>
    val portfolioHoldings: StateFlow<List<PortfolioHolding>>
    val careerGoalFunds: StateFlow<List<CareerGoalFund>>

    // --- UI Interactive States ---
    private val _isBrainThinking = MutableStateFlow(false)
    val isBrainThinking: StateFlow<Boolean> = _isBrainThinking

    // AI Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    // Focus Timer States (Synced with TimerService)
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private val _customDurationMinutes = MutableStateFlow(25)
    val customDurationMinutes: StateFlow<Int> = _customDurationMinutes

    private val _timerSecondsRemaining = MutableStateFlow(1500) // Default 25 min
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining

    private var localTimerJob: kotlinx.coroutines.Job? = null

    private fun startLocalTimer(durationSeconds: Int) {
        localTimerJob?.cancel()
        _isTimerRunning.value = true
        _timerSecondsRemaining.value = durationSeconds
        localTimerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                kotlinx.coroutines.delay(1000)
                if (TimerService.isRunning) {
                    localTimerJob?.cancel()
                    break
                }
                _timerSecondsRemaining.value = _timerSecondsRemaining.value - 1
            }
            if (_timerSecondsRemaining.value <= 0) {
                _isTimerRunning.value = false
                _timerSecondsRemaining.value = _customDurationMinutes.value * 60
                
                val context = getApplication<Application>().applicationContext
                val intent = Intent(context, TimerService::class.java).apply {
                    action = "START"
                    putExtra("duration_seconds", 1)
                }
                try {
                    androidx.core.content.ContextCompat.startForegroundService(context, intent)
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Failed to start completion timer", e)
                }
            }
        }
    }

    // Career Mini Labs
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex

    private val _quizFeedback = MutableStateFlow<String?>(null)
    val quizFeedback: StateFlow<String?> = _quizFeedback

    // YAML Playground state
    private val _yamlCodeInput = MutableStateFlow(
        "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: jeevan-pod-deployment\nspec:\n  replicas: 3\n  selector:\n    matchLabels:\n      app: api-server"
    )
    val yamlCodeInput: StateFlow<String> = _yamlCodeInput

    private val _yamlValidationResult = MutableStateFlow<String?>(null)
    val yamlValidationResult: StateFlow<String?> = _yamlValidationResult

    // Current screen navigation tab
    private val _activeTab = MutableStateFlow("DASHBOARD") 
    val activeTab: StateFlow<String> = _activeTab

    // Active AI Advisor summary across modules
    private val _syntheticInsights = MutableStateFlow<List<String>>(emptyList())
    val syntheticInsights: StateFlow<List<String>> = _syntheticInsights

    // --- Brainstorming Cognitive Puzzles States ---
    private val _brainstormScore = MutableStateFlow(0)
    val brainstormScore: StateFlow<Int> = _brainstormScore

    private val _puzzlesSolved = MutableStateFlow(0)
    val puzzlesSolved: StateFlow<Int> = _puzzlesSolved

    private val _selectedPuzzleIndex = MutableStateFlow(0)
    val selectedPuzzleIndex: StateFlow<Int> = _selectedPuzzleIndex

    private val _puzzleResultFeedback = MutableStateFlow<String?>(null)
    val puzzleResultFeedback: StateFlow<String?> = _puzzleResultFeedback

    private val _puzzleIsAnswered = MutableStateFlow(false)
    val puzzleIsAnswered: StateFlow<Boolean> = _puzzleIsAnswered

    private val _aiInvestmentInsights = MutableStateFlow<List<String>>(emptyList())
    val aiInvestmentInsights: StateFlow<List<String>> = _aiInvestmentInsights

    init {
        val database = JeevanDatabase.getDatabase(application)
        repository = JeevanRepository(database.jeevanDao())

        // Setup pipeline flows from repository
        transactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        careerProgress = repository.allCareerProgress
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        healthLogs = repository.allHealthLogs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        userProfile = repository.userProfile
            .filterNotNull()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

        subtopicsProgress = repository.allSubtopicProgress
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        newsBookmarks = repository.allNewsBookmarks
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        portfolioHoldings = repository.allPortfolioHoldings
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        careerGoalFunds = repository.allCareerGoalFunds
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Sync with TimerService background state
        val sharedPrefs = application.getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
        val savedDuration = sharedPrefs.getInt("custom_duration_minutes", 25)
        _customDurationMinutes.value = savedDuration

        val endTime = sharedPrefs.getLong("end_time", 0)
        val isActive = sharedPrefs.getBoolean("is_active", false)
        val now = System.currentTimeMillis()

        if (TimerService.isRunning) {
            _timerSecondsRemaining.value = TimerService.secondsRemaining
            _isTimerRunning.value = true
        } else if (isActive && endTime > now) {
            val remainSeconds = ((endTime - now) / 1000).toInt()
            startLocalTimer(remainSeconds)
        } else {
            _timerSecondsRemaining.value = savedDuration * 60
            _isTimerRunning.value = false
        }

        TimerService.setCallbacks(
            onTick = { seconds ->
                _timerSecondsRemaining.value = seconds
                _isTimerRunning.value = true
            },
            onFinished = {
                _isTimerRunning.value = false
                _timerSecondsRemaining.value = _customDurationMinutes.value * 60
                // Award XP on successful completed timing
                viewModelScope.launch {
                    repository.addXpToTopic("linux", 50)
                    generateDynamicAIEcosystemInsights()
                }
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "Jeevan Coach",
                    text = "Acknowledge: Strategic DevOps focus session completed successfully when device was idle. +50 XP rewarded.",
                    timestamp = System.currentTimeMillis()
                )
            }
        )

        viewModelScope.launch {
            try {
                repository.seedDemoDataIfEmpty()
                generateDynamicAIEcosystemInsights()
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Initialization seeding failed", e)
            }
        }

        // Auto compile AI investment insights dynamically on portfolio/career goals updates
        viewModelScope.launch {
            portfolioHoldings.collect {
                updateAIInvestmentInsights()
            }
        }
        viewModelScope.launch {
            careerGoalFunds.collect {
                updateAIInvestmentInsights()
            }
        }

        // Live market price fluctuation ticker
        viewModelScope.launch {
            while (true) {
                delay(4000)
                try {
                    if (com.example.ui.screen.IndianMarketScheduleManager.getMarketStatus().isOpen) {
                        repository.fluctuateHoldingPrices()
                    }
                } catch (e: Exception) {
                    // Fail gracefully
                }
            }
        }

        // Welcome Greeting
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "Jeevan",
                text = "Welcome, Commander. System check: Wallet capital registers stable, study tracks are updated, and health variables compiled. I am ready to route your query.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    // --- FINANCIAL TRANSACTION OPERATIONS ---
    fun addExpense(title: String, amount: Double, category: String, isSub: Boolean) {
        viewModelScope.launch {
            repository.addTransaction(title, amount, "EXPENSE", category.uppercase(), isSub)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun addIncome(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.addTransaction(title, amount, "INCOME", category.uppercase(), false)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun updateMonthlyLimit(limit: Double) {
        viewModelScope.launch {
            val prof = userProfile.value
            repository.updateUserProfile(prof.copy(monthlyBudgetLimit = limit))
            generateDynamicAIEcosystemInsights()
        }
    }

    // --- PORTFOLIO OPERATIONS ---
    fun addPortfolioAsset(
        name: String,
        quantity: Double,
        price: Double,
        type: String,
        purchaseDate: Long = System.currentTimeMillis(),
        notes: String = "",
        symbol: String = "",
        exchange: String = "NSE",
        sector: String = "Other"
    ) {
        viewModelScope.launch {
            repository.addPortfolioHolding(
                name,
                quantity,
                price,
                type.uppercase(),
                purchaseDate,
                notes,
                symbol,
                exchange,
                sector
            )
        }
    }

    fun removePortfolioAsset(holding: PortfolioHolding) {
        viewModelScope.launch {
            repository.deletePortfolioHolding(holding)
        }
    }

    // --- CAREER GOAL FUNDS OPERATIONS ---
    fun addCareerGoalFund(name: String, target: Double, current: Double = 0.0) {
        viewModelScope.launch {
            repository.addCareerGoalFund(name, target, current)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun updateCareerGoalFund(fund: CareerGoalFund) {
        viewModelScope.launch {
            repository.updateCareerGoalFund(fund)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun deleteCareerGoalFund(fund: CareerGoalFund) {
        viewModelScope.launch {
            repository.deleteCareerGoalFund(fund)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun contributeToCareerGoal(fund: CareerGoalFund, amount: Double) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile.balanceAmount >= amount) {
                // Deduct from profile
                repository.updateUserProfile(currentProfile.copy(balanceAmount = currentProfile.balanceAmount - amount))
                // Add to fund
                repository.updateCareerGoalFund(fund.copy(currentAmount = fund.currentAmount + amount))
                // Add transactional log
                repository.addTransaction(
                    title = "Funded goal: ${fund.name}",
                    amount = amount,
                    type = "EXPENSE",
                    category = "ADDITIONAL",
                    isSubscription = false
                )
                generateDynamicAIEcosystemInsights()
            }
        }
    }

    // --- HEALTH / NUTRITION OPERATIONS ---
    fun addWater(ml: Int) {
        viewModelScope.launch {
            repository.updateWaterIntake(ml)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun addSteps(steps: Int) {
        viewModelScope.launch {
            repository.updateSteps(steps)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun saveMoodAndJournal(score: Int, entry: String) {
        viewModelScope.launch {
            repository.updateMoodAndJournal(score, entry)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun logVitals(sleepMin: Int, calConsumed: Int, calBurned: Int) {
        viewModelScope.launch {
            repository.updateSleepAndCal(sleepMin, calConsumed, calBurned)
            generateDynamicAIEcosystemInsights()
        }
    }

    // --- NEWS HUB BOOKMARKS ---
    fun bookmarkNews(title: String, category: String, url: String, desc: String) {
        viewModelScope.launch {
            repository.addNewsBookmark(title, category, url, desc)
        }
    }

    fun removeNewsBookmark(bookmark: NewsBookmark) {
        viewModelScope.launch {
            repository.deleteNewsBookmark(bookmark)
        }
    }

    // --- DEVOPS CAREER SUBTOPIC PROGRESS ---
    fun toggleSubtopic(subtopicId: String, parentTopicId: String, isCompleted: Boolean, reason: String? = null, score: Int = 0) {
        viewModelScope.launch {
            repository.saveSubtopicProgress(subtopicId, parentTopicId, isCompleted, reason, score)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun awardXp(topicId: String, amount: Int) {
        viewModelScope.launch {
            repository.addXpToTopic(topicId, amount)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun selectTopicLesson(topicId: String, lessonId: String) {
        viewModelScope.launch {
            repository.markLessonCompleted(topicId, lessonId)
        }
    }

    fun toggleTopicDeployment(topicId: String) {
        viewModelScope.launch {
            repository.toggleTopicDeployment(topicId)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun processQuizAnswer(topicId: String, selectedOptionIndex: Int, correctIndex: Int) {
        if (selectedOptionIndex == correctIndex) {
            _quizFeedback.value = "🟢 Correct! Scenario resolved accurately. Got +25 XP."
            viewModelScope.launch {
                repository.addXpToTopic(topicId, 25)
            }
        } else {
            _quizFeedback.value = "🔴 Incorrect override. Review AWS/K8s architectural constraints."
        }
    }

    fun setQuizIndex(index: Int) {
        _currentQuizIndex.value = index
        _quizFeedback.value = null
    }

    // YAML syntax checks
    fun updateYamlCode(code: String) {
        _yamlCodeInput.value = code
    }

    fun validateYamlCode() {
        val input = _yamlCodeInput.value
        val hasContainersStr = input.contains("containers:")
        val hasSpecStr = input.contains("spec:")
        val hasImageStr = input.contains("image:")

        when {
            !input.contains("apiVersion:") -> {
                _yamlValidationResult.value = "⚠️ YAML schema validation failed: Missing 'apiVersion' root field."
            }
            !input.contains("kind:") -> {
                _yamlValidationResult.value = "⚠️ YAML schema validation failed: Missing element 'kind'."
            }
            !hasSpecStr -> {
                _yamlValidationResult.value = "⚠️ Spec structure failed: Definition requires a 'spec:' root block."
            }
            !hasContainersStr && input.lowercase().contains("deployment") -> {
                _yamlValidationResult.value = "⚠️ Compliance failure: Container parameters 'containers:' block is missing inside your manifest spec."
            }
            else -> {
                _yamlValidationResult.value = "🚀 YAML deployment compliance checked: Outstanding syntactical precision! +15 XP rewarded!"
                viewModelScope.launch {
                    repository.addXpToTopic("kubernetes", 15)
                }
            }
        }
    }

    // --- TIMERS / DEEP FOCUS WORK SERVICE CONTROLLER ---
    fun setCustomTimerMinutes(minutes: Int) {
        if (!_isTimerRunning.value) {
            val clamped = minutes.coerceIn(1, 180)
            _customDurationMinutes.value = clamped
            _timerSecondsRemaining.value = clamped * 60
            
            val context = getApplication<Application>().applicationContext
            val prefs = context.getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
            prefs.edit().putInt("custom_duration_minutes", clamped).apply()
        }
    }

    fun toggleTimer(customMinutes: Int = _customDurationMinutes.value) {
        val context = getApplication<Application>().applicationContext
        val startServiceIntent = Intent(context, TimerService::class.java)

        localTimerJob?.cancel()

        if (_isTimerRunning.value) {
            startServiceIntent.action = "PAUSE"
            try {
                context.startService(startServiceIntent)
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Failed to pause TimerService", e)
            }
            _isTimerRunning.value = false
        } else {
            val secondsToRun = if (TimerService.isRunning) TimerService.secondsRemaining else customMinutes * 60
            startServiceIntent.action = "START"
            startServiceIntent.putExtra("duration_seconds", secondsToRun)
            try {
                androidx.core.content.ContextCompat.startForegroundService(context, startServiceIntent)
                _isTimerRunning.value = true
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Failed to start TimerService", e)
                _isTimerRunning.value = false
            }
        }
    }

    fun resetTimer() {
        val context = getApplication<Application>().applicationContext
        localTimerJob?.cancel()
        val startServiceIntent = Intent(context, TimerService::class.java).apply {
            action = "STOP"
        }
        try {
            context.startService(startServiceIntent)
        } catch (e: Exception) {
            android.util.Log.e("JeevanViewModel", "Failed to stop TimerService", e)
        }
        _isTimerRunning.value = false
        _timerSecondsRemaining.value = _customDurationMinutes.value * 60
    }

    fun getTodayDateString(): String {
        return repository.getTodayDateString()
    }

    // --- QUARTERLY REPORT GENERATOR ---
    fun generateCSVReport(context: Context) {
        viewModelScope.launch {
            try {
                val budget = userProfile.value.monthlyBudgetLimit
                val balance = userProfile.value.balanceAmount
                val txsList = transactions.value
                val spent = txsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                val saved = budget - spent
                val holdings = portfolioHoldings.value

                val csvBuilder = StringBuilder()
                csvBuilder.append("Jeevan Personal OS Financial Report\n")
                csvBuilder.append("Generated on,,${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n")
                csvBuilder.append("\nSummary,,\n")
                csvBuilder.append("Total Current Capital,,₹$balance\n")
                csvBuilder.append("Monthly Capital Threshold,,₹$budget\n")
                csvBuilder.append("Total Recorded Expenses,,₹$spent\n")
                csvBuilder.append("Implied Active Savings,,₹$saved\n")
                
                csvBuilder.append("\nRecent Ledgers,,\n")
                csvBuilder.append("Title,Amount,Type,Category,Is Subscription,DateString\n")
                txsList.forEach { tx ->
                    val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(tx.date))
                    csvBuilder.append("\"${tx.title}\",${tx.amount},${tx.type},${tx.category},${tx.isSubscription},$dateFormatted\n")
                }

                csvBuilder.append("\nCore Portfolio & Assets,,\n")
                csvBuilder.append("Asset Name,Symbol,Exchange,Sector,Quantity,Purchase Price,Current Price,Current Calculated Value,Purchase Date,Notes,Asset Type\n")
                holdings.forEach { hold ->
                    val totalHoldVal = hold.quantity * hold.currentPrice
                    val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(hold.purchaseDate ?: System.currentTimeMillis()))
                    val escapedNotes = (hold.notes ?: "").replace("\"", "\"\"")
                    csvBuilder.append("\"${hold.assetName}\",\"${hold.symbol.orEmpty()}\",\"${hold.exchange.orEmpty()}\",\"${hold.sector.orEmpty()}\",${hold.quantity},₹${hold.purchasePrice},₹${hold.currentPrice},₹$totalHoldVal,$dateFormatted,\"$escapedNotes\",${hold.assetType}\n")
                }

                val fileName = "jeevan_quarterly_report_${System.currentTimeMillis()}.csv"
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                    output.write(csvBuilder.toString().toByteArray())
                }

                Toast.makeText(context, "Quarterly Report compilation successful: $fileName archived.", Toast.LENGTH_LONG).show()
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "Jeevan Finance Advisor",
                    text = "Acknowledge: I have compiled your quarterly financial reports into: '$fileName' inside local app directories. Growth charts verified.",
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Failed to write CSV file", e)
                Toast.makeText(context, "Failed to compile: limits or storage error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- CENTRAL BRAIN CHAT ROUTER ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(
            sender = "You",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isBrainThinking.value = true

        viewModelScope.launch {
            // Build Context Memory from Database entries
            val profile = userProfile.value
            val txsList = transactions.value
            val activeSpent = txsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            val health = repository.getTodayHealthLog()
            val caps = careerProgress.value
            val subLog = subtopicsProgress.value
            val holdingSummaryList = portfolioHoldings.value.map { "${it.assetName} (${it.quantity} units @ ₹${it.purchasePrice})" }.joinToString(", ")

            val completedSubtopicsCount = subLog.count { it.isCompleted }
            val totalSubtopicsCount = subLog.size

            val userMemoryContext = """
                [Jeevan DB Context Memory Injection]
                User Name: ${profile.name}
                Target Profession: ${profile.jobTarget}
                Current Streak: ${profile.careerStreak} Days
                Financial Capital Limit: ₹${profile.monthlyBudgetLimit}
                Expenses recorded: ₹$activeSpent
                Available Purchasing Power: ₹${profile.monthlyBudgetLimit - activeSpent}
                Balance amount in infrastructure: ₹${profile.balanceAmount}
                Holdings list: $holdingSummaryList
                Today's Water Intake: ${health.waterIntakeMl} ml (Goal: ${profile.dailyWaterGoalMl} ml)
                Today's Steps Count: ${health.stepsCount} (Goal: ${profile.dailyStepGoal})
                Today's Sleep: ${health.sleepMinutes} min
                Today's Calorie Consumed: ${health.caloriesConsumed} kcal
                Completed DevOps Subtopics: $completedSubtopicsCount of $totalSubtopicsCount
            """.trimIndent()

            // Run Gemini REST query
            val responseText = GeminiNetworkClient.queryJeevanEngine(text, userMemoryContext)
            
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "Jeevan",
                text = responseText,
                timestamp = System.currentTimeMillis()
            )
            _isBrainThinking.value = false
        }
    }

    // --- ECOSYSTEM REASONING ENGINE ---
    private suspend fun generateDynamicAIEcosystemInsights() {
        try {
            val txs = transactions.value
            val health = repository.getTodayHealthLog()
            val prof = repository.getOrInitUserProfile()
            val subs = subtopicsProgress.value

            val insights = mutableListOf<String>()

            // 1. Finance Checks
            val totalExpenses = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            if (totalExpenses > prof.monthlyBudgetLimit * 0.8) {
                insights.add("⚠️ Budget Overload Alert: You have expended 80% of your ₹${prof.monthlyBudgetLimit} budget allocation rate. Immediate capital freeze suggested.")
            } else if (totalExpenses > 0) {
                val rem = prof.monthlyBudgetLimit - totalExpenses
                insights.add("📈 Capital Analysis: Allocation stable. Remaining purchasing power is ₹$rem. Unused savings rate: ${((rem / prof.monthlyBudgetLimit) * 100).toInt()}%")
            } else {
                insights.add("✔ Zero financial outflow today. Capital preservation coefficient is 100%.")
            }

            // 2. Hydration checking
            if (health.waterIntakeMl < prof.dailyWaterGoalMl / 2) {
                insights.add("💧 Hydration Warning: Fluid index registered at low supply (${health.waterIntakeMl}ml / ${prof.dailyWaterGoalMl}ml). Supplement with 500ml water to secure cognitive efficiency.")
            } else {
                insights.add("💧 Hydration Stabilized: Fluid density matches standard guidelines. High focus capacity enabled.")
            }

            // 3. Health & Learning synergy
            if (health.sleepMinutes < 360) {
                insights.add("😴 Fatigue Warning: Sleep registered below 6 hours (${health.sleepMinutes} mins). I recommend holding off difficult AWS Multi-Region cluster updates today.")
            } else {
                val completionRatio = if (subs.isNotEmpty()) (subs.count { it.isCompleted }.toDouble() / subs.size * 100).toInt() else 0
                insights.add("🔥 DevOps Readiness: Career subtopic validation is at $completionRatio%. Track your progress continuously to unlock high-grade offers.")
            }

            _syntheticInsights.value = insights
        } catch (e: Exception) {
            android.util.Log.e("JeevanViewModel", "Failed to generate dynamic ecosystem insights", e)
        }
    }

    // --- BRAINSTORMING PUZZLES FUNCTIONS ---
    fun submitPuzzleAnswer(selectedIndex: Int, correctIndex: Int) {
        if (_puzzleIsAnswered.value) return
        _puzzleIsAnswered.value = true
        if (selectedIndex == correctIndex) {
            _brainstormScore.value += 20
            _puzzlesSolved.value += 1
            _puzzleResultFeedback.value = "🟢 SUCCESS! Scenario resolved accurately with strategic mastery! +25 DevOps XP awarded and Brain power coefficient boosted."
            viewModelScope.launch {
                repository.addXpToTopic("kubernetes", 25)
                generateDynamicAIEcosystemInsights()
            }
        } else {
            _puzzleResultFeedback.value = "🔴 COMPROMISE! The option selected failed to address baseline architectural rules. System state degraded. Let's study and try again."
        }
    }

    fun nextPuzzle(totalPuzzles: Int) {
        _puzzleIsAnswered.value = false
        _puzzleResultFeedback.value = null
        val nextIdx = (_selectedPuzzleIndex.value + 1) % totalPuzzles
        _selectedPuzzleIndex.value = nextIdx
    }

    // --- LOCATION TEMPERATURE WIDGET SUPPORT ---
    private val _weatherState = MutableStateFlow("UNKNOWN") // UNKNOWN, LOADING, SUCCESS, ERROR, PERMISSION_REQUIRED
    val weatherState: StateFlow<String> = _weatherState

    private val _weatherTemp = MutableStateFlow<Double?>(null)
    val weatherTemp: StateFlow<Double?> = _weatherTemp

    private val _weatherLocationName = MutableStateFlow("Locating...")
    val weatherLocationName: StateFlow<String> = _weatherLocationName

    fun refreshWeather() {
        val context = getApplication<Application>()
        _weatherState.value = "LOADING"
        viewModelScope.launch {
            try {
                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            fetchWeatherForCoordinates(loc.latitude, loc.longitude)
                        } else {
                            _weatherLocationName.value = "Bengaluru (GPS)"
                            fetchWeatherForCoordinates(12.9716, 77.5946)
                        }
                    }.addOnFailureListener {
                        _weatherLocationName.value = "Bengaluru (GPS)"
                        fetchWeatherForCoordinates(12.9716, 77.5946)
                    }
                } else {
                    _weatherLocationName.value = "Loc Off"
                    _weatherState.value = "PERMISSION_REQUIRED"
                }
            } catch (e: Exception) {
                _weatherLocationName.value = "Bengaluru (GPS)"
                fetchWeatherForCoordinates(12.9716, 77.5946)
            }
        }
    }

    private fun fetchWeatherForCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val city = addresses[0].locality ?: addresses[0].subAdminArea ?: "Remote Node"
                        _weatherLocationName.value = city
                    } else {
                        _weatherLocationName.value = "Grid ${String.format("%.2f", lat)},${String.format("%.2f", lon)}"
                    }
                } catch (e: Exception) {
                    _weatherLocationName.value = "Grid ${String.format("%.2f", lat)},${String.format("%.2f", lon)}"
                }

                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
                val conn = java.net.URL(urlString).openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val responseStr = conn.inputStream.bufferedReader().use { it.readText() }

                val tempRegex = """(?i)"temperature"\s*:\s*([0-9.-]+)""".toRegex()
                val match = tempRegex.find(responseStr)
                if (match != null) {
                    val tempVal = match.groupValues[1].toDoubleOrNull()
                    if (tempVal != null) {
                        _weatherTemp.value = tempVal
                        _weatherState.value = "SUCCESS"
                    } else {
                        _weatherState.value = "ERROR"
                    }
                } else {
                    _weatherState.value = "ERROR"
                }
            } catch (e: Exception) {
                _weatherState.value = "ERROR"
            }
        }
    }

    fun updateAIInvestmentInsights() {
        val holdings = portfolioHoldings.value
        val fundsList = careerGoalFunds.value
        val insights = mutableListOf<String>()

        if (holdings.isEmpty()) {
            insights.add("Observation: No portfolio asset records stored in the OS database yet.")
            insights.add("Tip: Record mutual funds or stock units under secondary ledger to start auto-analysis.")
        } else {
            val totalVal = holdings.sumOf { it.quantity * it.currentPrice }
            val stocksVal = holdings.filter { it.assetType == "STOCK" }.sumOf { it.quantity * it.currentPrice }
            val mfVal = holdings.filter { it.assetType == "MF" }.sumOf { it.quantity * it.currentPrice }
            val sipVal = holdings.filter { it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }

            val totalCompVal = totalVal.coerceAtLeast(1.0)
            val stockPercent = ((stocksVal / totalCompVal) * 100).toInt()
            val mfPercent = (((mfVal + sipVal) / totalCompVal) * 100).toInt()

            if (stockPercent > 60) {
                insights.add("Observation: Your portfolio has a heavy stock exposure index ($stockPercent%), meaning higher volatility during earnings season.")
            } else if (mfPercent > 60) {
                insights.add("Observation: Most of your assets ($mfPercent%) are locked in index / equity-based mutual funds which are excellent for long-term compound growth.")
            } else {
                insights.add("Observation: Asset allocation is balanced, split between mutual funds ($mfPercent%) and individual stocks ($stockPercent%).")
            }
        }

        val emergency = fundsList.firstOrNull { it.name.contains("Emergency", ignoreCase = true) }
        if (emergency != null) {
            val pct = ((emergency.currentAmount / emergency.targetAmount.coerceAtLeast(1.0)) * 100).toInt()
            insights.add("Observation: Emergency Reserve progress stands at $pct% of the ₹60,000 threshold.")
        }

        val totalSipMf = holdings.filter { it.assetType == "MF" || it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }
        if (totalSipMf >= 4000.0) {
            insights.add("Observation: Consistent savings habit confirmed! Auto-SIP contributions show 80%+ consistency over standard limits.")
        } else {
            insights.add("Observation: Monthly investment contributions are slightly lower than optimal threshold. Target is ₹5,000.")
        }

        _aiInvestmentInsights.value = insights
    }

    fun calculateFinancialHealthScore(): FinancialHealthReport {
        val txsList = transactions.value
        val budget = userProfile.value.monthlyBudgetLimit.coerceAtLeast(1.0)
        val spent = txsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val remaining = (budget - spent).coerceAtLeast(0.0)

        // 1. Expense Ratio (20 pts)
        val expenseRatio = (spent / budget).coerceAtLeast(0.0)
        val expensePts = if (expenseRatio <= 0.5) 20.0 else ((1.0 - expenseRatio) * 40.0).coerceIn(0.0, 20.0)

        // 2. Savings Rate (20 pts)
        val savingsRate = remaining / budget
        val savingsPts = (savingsRate * 50.0).coerceIn(0.0, 20.0)

        // 3. Goal Progress (20 pts)
        val fundsList = careerGoalFunds.value
        val goalPts = if (fundsList.isEmpty()) 15.0 else {
            val totalTarget = fundsList.sumOf { it.targetAmount }.coerceAtLeast(1.0)
            val totalCurrent = fundsList.sumOf { it.currentAmount }
            ((totalCurrent / totalTarget) * 20.0).coerceIn(0.0, 20.0)
        }

        // 4. Investment Consistency (20 pts) (Target: ₹5,000 Mutual Funds/SIP per month)
        val holdings = portfolioHoldings.value
        val totalSipMf = holdings.filter { it.assetType == "MF" || it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }
        val sipTarget = 5000.0
        val consistencyPercent = ((totalSipMf / sipTarget) * 100.0).coerceIn(0.0, 100.0)
        val consistencyPts = ((totalSipMf / sipTarget) * 20.0).coerceIn(0.0, 20.0)

        // 5. Emergency Fund Status (20 pts)
        val emergencyFund = fundsList.firstOrNull { it.name.contains("Emergency", ignoreCase = true) }
        val emergencyPts = if (emergencyFund != null) {
            ((emergencyFund.currentAmount / emergencyFund.targetAmount.coerceAtLeast(1.0)) * 20.0).coerceIn(0.0, 20.0)
        } else {
            10.0
        }

        val totalScore = (expensePts + savingsPts + goalPts + consistencyPts + emergencyPts).toInt().coerceIn(10, 100)

        val grade = when {
            totalScore >= 90 -> "S+ Excellent"
            totalScore >= 80 -> "A Grade (Strong Financial Health)"
            totalScore >= 70 -> "B Grade (Moderate)"
            totalScore >= 50 -> "C Grade (Needs Fine-Tuning)"
            else -> "D Grade (High Risk Alert)"
        }

        val recs = mutableListOf<String>()
        if (expenseRatio > 0.7) {
            recs.add("Your monthly spending exceeds 70% of threshold. Freeze non-essential purchase items.")
        } else {
            recs.add("Awesome capital threshold overhead of ${(100 - expenseRatio * 100).toInt()}%. Maintain this margin.")
        }

        if (emergencyFund == null || (emergencyFund.currentAmount / emergencyFund.targetAmount) < 0.5) {
            recs.add("Emergency reserve is under 50% target. Recommend funding this goal to cushion career changes.")
        } else {
            recs.add("Your emergency fund safety nest is fully operational on safe ground.")
        }

        if (totalSipMf < sipTarget) {
            recs.add("Sip contributions are ₹${(sipTarget - totalSipMf).toInt()} below monthly targets. Adjust recurring plans.")
        } else {
            recs.add("Compounding investments are highly consistent. Wealth engine running at maximum throughput.")
        }

        return FinancialHealthReport(
            score = totalScore,
            grade = grade,
            expenseRatio = expenseRatio,
            savingsRate = savingsRate,
            consistencyProgress = consistencyPercent,
            emergencyProgress = if (emergencyFund != null) (emergencyFund.currentAmount / emergencyFund.targetAmount * 100.0) else 0.0,
            recommendations = recs
        )
    }

    override fun onCleared() {
        TimerService.clearCallbacks()
        super.onCleared()
    }
}

data class FinancialHealthReport(
    val score: Int,
    val grade: String,
    val expenseRatio: Double,
    val savingsRate: Double,
    val consistencyProgress: Double,
    val emergencyProgress: Double,
    val recommendations: List<String>
)

data class ChatMessage(
    val sender: String,
    val text: String,
    val timestamp: Long
)

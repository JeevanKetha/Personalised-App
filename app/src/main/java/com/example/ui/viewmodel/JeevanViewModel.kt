package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.JeevanDatabase
import com.example.data.entity.Transaction
import com.example.data.entity.CareerProgress
import com.example.data.entity.HealthLog
import com.example.data.entity.UserProfile
import com.example.data.repository.JeevanRepository
import com.example.network.GeminiNetworkClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class JeevanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JeevanRepository
    
    // --- Room flows ---
    val transactions: StateFlow<List<Transaction>>
    val careerProgress: StateFlow<List<CareerProgress>>
    val healthLogs: StateFlow<List<HealthLog>>
    val userProfile: StateFlow<UserProfile>

    // --- UI Interactive States ---
    private val _isBrainThinking = MutableStateFlow(false)
    val isBrainThinking: StateFlow<Boolean> = _isBrainThinking

    // AI Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    // Focus / Deep Work Timer States
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private val _timerSecondsRemaining = MutableStateFlow(1500) // 25 Min Default
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining
    private var timerJob: Job? = null

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

    // Current screen tab
    private val _activeTab = MutableStateFlow("DASHBOARD") // "DASHBOARD", "FINANCE", "CAREER", "HEALTH", "BRAIN"
    val activeTab: StateFlow<String> = _activeTab

    // Active AI Advisor summary across modules
    private val _syntheticInsights = MutableStateFlow<List<String>>(emptyList())
    val syntheticInsights: StateFlow<List<String>> = _syntheticInsights

    init {
        val database = JeevanDatabase.getDatabase(application)
        repository = JeevanRepository(database.jeevanDao())

        // Setup states from repository flows
        transactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        careerProgress = repository.allCareerProgress
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        healthLogs = repository.allHealthLogs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Fallback-backed User Profile loading
        userProfile = repository.userProfile
            .filterNotNull()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

        viewModelScope.launch {
            try {
                repository.seedDemoDataIfEmpty()
                generateDynamicAIEcosystemInsights()
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Initialization seeding failed", e)
            }
        }

        // Setup Initial greeting message
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "Jeevan",
                text = "Welcome, Explorer. I have synthesized your personal operating system indicators for today. " +
                        "How can I assist your engineering roadmap or capital allocation right now?",
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
            repository.addTransaction(title, amount, "EXPENSE", category, isSub)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun addIncome(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.addTransaction(title, amount, "INCOME", category, false)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            generateDynamicAIEcosystemInsights()
        }
    }

    // --- HEALTH / HYDRATION OPERATIONS ---
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

    // --- DEVOPS CAREER COMPANION OPERATIONS ---
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
            _quizFeedback.value = "🟢 Correct! Outstanding precision. Got +25 XP."
            viewModelScope.launch {
                repository.addXpToTopic(topicId, 25)
            }
        } else {
            _quizFeedback.value = "🔴 Incorrect. Jeevan suggests reviewing standard Kubernetes configuration details."
        }
    }

    fun setQuizIndex(index: Int) {
        _currentQuizIndex.value = index
        _quizFeedback.value = null
    }

    // YAML syntax playground checker
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
                _yamlValidationResult.value = "⚠️ YAML Validation failed: Missing 'apiVersion' schema definition."
            }
            !input.contains("kind:") -> {
                _yamlValidationResult.value = "⚠️ YAML Validation failed: Missing Kubernetes object element 'kind'."
            }
            !hasSpecStr -> {
                _yamlValidationResult.value = "⚠️ Error: Kubernetes pod specification template requires a 'spec:' root block."
            }
            !hasContainersStr && input.lowercase().contains("deployment") -> {
                _yamlValidationResult.value = "⚠️ Warning: Cluster container specification definition 'containers:' block is missing inside your deployment template structure."
            }
            else -> {
                _yamlValidationResult.value = "🚀 YAML deployment templates checked successfully! High syntactic compliance detected. +15 XP rewarded!"
                viewModelScope.launch {
                    repository.addXpToTopic("kubernetes", 15)
                }
            }
        }
    }

    // --- TIMERS / DEEP FOCUS WORK ---
    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = 1500
    }

    private fun startTimer() {
        timerJob?.cancel() // Cancel any previously active timer loop to prevent concurrent decr races
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                delay(1000)
                _timerSecondsRemaining.value -= 1
            }
            _isTimerRunning.value = false
            // Complete session bonus
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "Jeevan Coach",
                text = "Excellent focus completed! Deep work timer reached 0. +50 Career focus XP unlocked.",
                timestamp = System.currentTimeMillis()
            )
            repository.addXpToTopic("linux", 50)
        }
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun getTodayDateString(): String {
        return repository.getTodayDateString()
    }

    // --- CENTRAL BRAIN CHAT INTERACTION ---
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
            val responseText = GeminiNetworkClient.queryJeevanBrain(text)
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "Jeevan",
                text = responseText,
                timestamp = System.currentTimeMillis()
            )
            _isBrainThinking.value = false
        }
    }

    // --- ECOSYSTEM REASONING SYSTEM ---
    /**
     * Synthesizes cross-module variables to generate unique, highly-personalized strategic advice.
     */
    private suspend fun generateDynamicAIEcosystemInsights() {
        try {
            val txs = transactions.value
            val health = repository.getTodayHealthLog()
            val prof = repository.getOrInitUserProfile()

            val insights = mutableListOf<String>()

            // 1. Finance logic checks
            val totalExpenses = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            if (totalExpenses > prof.monthlyBudgetLimit * 0.8) {
                insights.add("⚠️ Budget Warning: You have reached 80% of your current allocated monthly limit. Suspend SaaS renewals.")
            } else if (totalExpenses > 0) {
                insights.add("📈 Capital Analysis: Capital allocation is currently optimal. Unused savings: \$${prof.balanceAmount - totalExpenses}")
            } else {
                insights.add("✔ No recorded expenditures today. Capital preservation rate: 100%.")
            }

            // 2. Hydration checking
            if (health.waterIntakeMl < prof.dailyWaterGoalMl / 2) {
                insights.add("💧 Hydration Index: Low fluid supply (${health.waterIntakeMl}ml / ${prof.dailyWaterGoalMl}ml). Drink 2 glasses of water to maintain high cognitive focus metrics.")
            } else {
                insights.add("💧 Hydration Index: Cellular hydration is premium! Excellent focus retention.")
            }

            // 3. Health & Learning synergy
            if (health.sleepMinutes < 360) {
                insights.add("😴 Fatigue Indicator: Sleep registered below 6 hours last night. Jeevan suggests limiting intensive CKA Kubernetes network design labs today.")
            } else if (prof.careerStreak > 2) {
                insights.add("🔥 Performance Index: You are on a ${prof.careerStreak}-day study streak! Keep tracking Linux mastery topics.")
            }

            _syntheticInsights.value = insights
        } catch (e: Exception) {
            android.util.Log.e("JeevanViewModel", "Failed to generate dynamic ecosystem insights", e)
        }
    }
}

data class ChatMessage(
    val sender: String,
    val text: String,
    val timestamp: Long
)

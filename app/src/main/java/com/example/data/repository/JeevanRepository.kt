package com.example.data.repository

import com.example.data.dao.JeevanDao
import com.example.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JeevanRepository(private val jeevanDao: JeevanDao) {

    // --- Flows ---
    val allTransactions: Flow<List<Transaction>> = jeevanDao.getAllTransactionsFlow()
    val allCareerProgress: Flow<List<CareerProgress>> = jeevanDao.getAllCareerProgressFlow()
    val allHealthLogs: Flow<List<HealthLog>> = jeevanDao.getAllHealthLogsFlow()
    val userProfile: Flow<UserProfile?> = jeevanDao.getUserProfileFlow()
    val allSubtopicProgress: Flow<List<SubtopicProgress>> = jeevanDao.getAllSubtopicProgressFlow()
    val allNewsBookmarks: Flow<List<NewsBookmark>> = jeevanDao.getAllNewsBookmarksFlow()
    val allPortfolioHoldings: Flow<List<PortfolioHolding>> = jeevanDao.getAllPortfolioHoldingsFlow()
    val allCareerGoalFunds: Flow<List<CareerGoalFund>> = jeevanDao.getAllCareerGoalFundsFlow()
    val allSavedResources: Flow<List<SavedResource>> = jeevanDao.getAllSavedResourcesFlow()

    // --- Profile Management ---
    suspend fun getOrInitUserProfile(): UserProfile = withContext(Dispatchers.IO) {
        val existing = jeevanDao.getUserProfileDirect()
        if (existing != null) {
            if (existing.balanceAmount > 100000.0) {
                val diff = 125000.0 - existing.balanceAmount
                val corrected = existing.copy(balanceAmount = 20000.0 - diff)
                jeevanDao.insertUserProfile(corrected)
                corrected
            } else {
                existing
            }
        } else {
            val initial = UserProfile(
                id = 1,
                name = "Jeevan Explorer",
                jobTarget = "DevOps Engineer",
                monthlyBudgetLimit = 20000.0, // Default requested capital ₹20,000
                dailyWaterGoalMl = 3000,
                dailyStepGoal = 8000,
                careerStreak = 3,
                balanceAmount = 20000.0,
                weightKg = 70.0,
                heightCm = 175.0,
                computedBmi = 22.8
            )
            jeevanDao.insertUserProfile(initial)
            initial
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Unit = withContext(Dispatchers.IO) {
        jeevanDao.insertUserProfile(profile)
    }

    // --- Financial operations ---
    suspend fun addTransaction(title: String, amount: Double, type: String, category: String, isSubscription: Boolean): Unit = withContext(Dispatchers.IO) {
        val transaction = Transaction(
            title = title,
            amount = amount,
            type = type,
            category = category,
            isSubscription = isSubscription,
            date = System.currentTimeMillis()
        )
        jeevanDao.insertTransaction(transaction)

        // Adjust balance
        val profile = getOrInitUserProfile()
        val newBalance = if (type == "INCOME") {
            profile.balanceAmount + amount
        } else {
            profile.balanceAmount - amount
        }
        jeevanDao.insertUserProfile(profile.copy(balanceAmount = newBalance))
    }

    suspend fun deleteTransaction(transaction: Transaction): Unit = withContext(Dispatchers.IO) {
        jeevanDao.deleteTransaction(transaction)

        // Readjust balance
        val profile = getOrInitUserProfile()
        val newBalance = if (transaction.type == "INCOME") {
            profile.balanceAmount - transaction.amount
        } else {
            profile.balanceAmount + transaction.amount
        }
        jeevanDao.insertUserProfile(profile.copy(balanceAmount = newBalance))
    }

    // --- Career Progression Operations ---
    suspend fun addXpToTopic(topicId: String, xpToAdd: Int): Unit = withContext(Dispatchers.IO) {
        val existing = jeevanDao.getCareerProgressByTopic(topicId) 
            ?: CareerProgress(topicId = topicId, level = 1, xp = 0)
        
        val newXp = existing.xp + xpToAdd
        // Standard level progression: 100 XP per level
        val newLevel = (newXp / 100) + 1
        
        val updated = existing.copy(
            xp = newXp,
            level = if (newLevel > existing.level) newLevel else existing.level,
            lastActiveTime = System.currentTimeMillis()
        )
        jeevanDao.insertCareerProgress(updated)

        // Increment career streak in user profile if studied today
        val profile = getOrInitUserProfile()
        jeevanDao.insertUserProfile(profile.copy(careerStreak = profile.careerStreak + 1))
    }

    suspend fun markLessonCompleted(topicId: String, lessonId: String): Unit = withContext(Dispatchers.IO) {
        val existing = jeevanDao.getCareerProgressByTopic(topicId)
            ?: CareerProgress(topicId = topicId, level = 1, xp = 0)
        
        val list = existing.completedQuizIds.split(",").filter { it.isNotEmpty() }.toMutableList()
        if (!list.contains(lessonId)) {
            list.add(lessonId)
            val updated = existing.copy(
                completedQuizIds = list.joinToString(","),
                lastActiveTime = System.currentTimeMillis()
            )
            jeevanDao.insertCareerProgress(updated)
            addXpToTopic(topicId, 30) // Unlock 30 XP for lesson complete
        }
    }

    suspend fun toggleTopicDeployment(topicId: String): Unit = withContext(Dispatchers.IO) {
        val existing = jeevanDao.getCareerProgressByTopic(topicId)
            ?: CareerProgress(topicId = topicId, level = 1, xp = 0)
        
        val isDeployed = existing.level >= 2
        val updated = if (isDeployed) {
            existing.copy(level = 1, xp = 0, lastActiveTime = System.currentTimeMillis())
        } else {
            existing.copy(level = 2, xp = 120, lastActiveTime = System.currentTimeMillis())
        }
        jeevanDao.insertCareerProgress(updated)
    }

    // --- Subtopic Custom Relations ---
    suspend fun saveSubtopicProgress(subtopicId: String, parentTopicId: String, isCompleted: Boolean, reason: String?, score: Int): Unit = withContext(Dispatchers.IO) {
        val subObj = SubtopicProgress(
            subtopicId = subtopicId,
            parentTopicId = parentTopicId,
            isCompleted = isCompleted,
            reasonNotCompleted = if (isCompleted) null else reason,
            completionDate = if (isCompleted) System.currentTimeMillis() else null,
            assessmentScore = score
        )
        jeevanDao.insertSubtopicProgress(subObj)

        if (isCompleted) {
            addXpToTopic(parentTopicId, 40) // Award 40 XP for subtopic validation
        }
    }

    suspend fun getAllSubtopicProgressDirect(): List<SubtopicProgress> = withContext(Dispatchers.IO) {
        jeevanDao.getAllSubtopicProgressDirect()
    }

    // --- Saved Resources ---
    suspend fun saveResource(resource: SavedResource): Unit = withContext(Dispatchers.IO) {
        jeevanDao.insertSavedResource(resource)
    }

    suspend fun deleteResource(resource: SavedResource): Unit = withContext(Dispatchers.IO) {
        jeevanDao.deleteSavedResource(resource)
    }

    suspend fun getAllSavedResourcesDirect(): List<SavedResource> = withContext(Dispatchers.IO) {
        jeevanDao.getAllSavedResourcesDirect()
    }

    // --- News Bookmarks ---
    suspend fun addNewsBookmark(title: String, category: String, url: String, description: String): Unit = withContext(Dispatchers.IO) {
        val news = NewsBookmark(
            title = title,
            category = category,
            url = url,
            description = description,
            savedAt = System.currentTimeMillis()
        )
        jeevanDao.insertNewsBookmark(news)
    }

    suspend fun deleteNewsBookmark(bookmark: NewsBookmark): Unit = withContext(Dispatchers.IO) {
        jeevanDao.deleteNewsBookmark(bookmark)
    }

    // --- Portfolio Holdings ---
    suspend fun addPortfolioHolding(
        assetName: String,
        quantity: Double,
        purchasePrice: Double,
        assetType: String,
        purchaseDate: Long = System.currentTimeMillis(),
        notes: String = "",
        symbol: String = "",
        exchange: String = "NSE",
        sector: String = "Other"
    ): Unit = withContext(Dispatchers.IO) {
        val holding = PortfolioHolding(
            assetName = assetName,
            quantity = quantity,
            purchasePrice = purchasePrice,
            assetType = assetType,
            currentPrice = purchasePrice,
            purchaseDate = purchaseDate,
            notes = notes,
            symbol = symbol,
            exchange = exchange,
            sector = sector
        )
        jeevanDao.insertPortfolioHolding(holding)
    }

    suspend fun liquidatePortfolioValueToBalance(): Unit = withContext(Dispatchers.IO) {
        // Option to liquidate or manage, or optionally use standard wallet updates if needed
    }

    suspend fun fluctuateHoldingPrices(): Unit = withContext(Dispatchers.IO) {
        val holdings = jeevanDao.getAllPortfolioHoldingsDirect()
        val random = java.util.Random()
        for (holding in holdings) {
            val pct = (random.nextDouble() * 3.3) - 1.5 // range -1.5% to +1.8%
            val updatedPrice = (holding.currentPrice * (1.0 + pct / 100.0)).coerceAtLeast(0.1)
            val roundedPrice = Math.round(updatedPrice * 100.0) / 100.0
            val updated = holding.copy(currentPrice = roundedPrice)
            jeevanDao.insertPortfolioHolding(updated)
        }
    }

    suspend fun deletePortfolioHolding(holding: PortfolioHolding): Unit = withContext(Dispatchers.IO) {
        jeevanDao.deletePortfolioHolding(holding)
    }

    // --- Career Goal Funds Operations ---
    suspend fun addCareerGoalFund(name: String, targetAmount: Double, currentAmount: Double): Unit = withContext(Dispatchers.IO) {
        jeevanDao.insertCareerGoalFund(CareerGoalFund(name = name, targetAmount = targetAmount, currentAmount = currentAmount))
    }

    suspend fun updateCareerGoalFund(fund: CareerGoalFund): Unit = withContext(Dispatchers.IO) {
        jeevanDao.updateCareerGoalFund(fund)
    }

    suspend fun deleteCareerGoalFund(fund: CareerGoalFund): Unit = withContext(Dispatchers.IO) {
        jeevanDao.deleteCareerGoalFund(fund)
    }

    // --- Health Operations ---
    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    suspend fun getTodayHealthLog(): HealthLog = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        jeevanDao.getHealthLogByDate(today) ?: HealthLog(dateString = today)
    }

    suspend fun updateWaterIntake(addMl: Int): Unit = withContext(Dispatchers.IO) {
        val log = getTodayHealthLog()
        val updated = log.copy(waterIntakeMl = (log.waterIntakeMl + addMl).coerceAtLeast(0))
        jeevanDao.insertHealthLog(updated)
    }

    suspend fun updateSteps(steps: Int): Unit = withContext(Dispatchers.IO) {
        val log = getTodayHealthLog()
        val updated = log.copy(stepsCount = (log.stepsCount + steps).coerceAtLeast(0))
        jeevanDao.insertHealthLog(updated)
    }

    suspend fun updateSleepAndCal(sleepMin: Int, calConsumed: Int, calBurned: Int): Unit = withContext(Dispatchers.IO) {
        val log = getTodayHealthLog()
        val updated = log.copy(
            sleepMinutes = sleepMin,
            caloriesConsumed = calConsumed,
            caloriesBurned = calBurned
        )
        jeevanDao.insertHealthLog(updated)
    }

    suspend fun updateMoodAndJournal(score: Int, entry: String): Unit = withContext(Dispatchers.IO) {
        val log = getTodayHealthLog()
        val updated = log.copy(
            moodScore = score,
            journalEntry = entry
        )
        jeevanDao.insertHealthLog(updated)
    }

    // --- Seed Demo Data helper if empty ---
    suspend fun seedDemoDataIfEmpty(): Unit = withContext(Dispatchers.IO) {
        // If "week_1" subtopic is already seeded, the database has been initialized previously.
        // We skip seeding to prevent deleted portfolio assets or goal funds from reappearing!
        if (jeevanDao.getSubtopicProgressById("week_1") != null) {
            return@withContext
        }

        // Initial setup
        getOrInitUserProfile()

        val todayStr = getTodayDateString()
        if (jeevanDao.getHealthLogByDate(todayStr) == null) {
            jeevanDao.insertHealthLog(
                HealthLog(
                    dateString = todayStr,
                    waterIntakeMl = 1500,
                    caloriesBurned = 340,
                    caloriesConsumed = 1800,
                    sleepMinutes = 445, 
                    moodScore = 4,
                    journalEntry = "Constructed custom infrastructure deployment pipeline templates. Cognitive focus optimal.",
                    stepsCount = 4250
                )
            )
        }

        // Add Career core entries
        val defaultTopics = listOf("linux", "kubernetes", "python", "aws", "docker")
        for (topic in defaultTopics) {
            if (jeevanDao.getCareerProgressByTopic(topic) == null) {
                jeevanDao.insertCareerProgress(
                    CareerProgress(
                        topicId = topic,
                        level = 2,
                        xp = 150,
                        completedQuizIds = "intro",
                        lastActiveTime = System.currentTimeMillis()
                    )
                )
            }
        }

        // Seed subtopics with expanded 28-week Ultimate SRE & DevOps roadmap
        val defaultSubtopics = listOf(
            SubtopicProgress("week_1", "linux", true, System.currentTimeMillis(), null, 90),
            SubtopicProgress("week_2", "linux", false, null, "Lack of Time", 0),
            SubtopicProgress("week_3", "linux", true, System.currentTimeMillis(), null, 85),
            SubtopicProgress("week_4", "python", false, null, "Busy SRE Workday", 0),
            SubtopicProgress("week_5", "aws", false, null, "Needs Prep", 0),
            SubtopicProgress("week_6", "aws", false, null, null, 0),
            SubtopicProgress("week_7", "aws", false, null, null, 0),
            SubtopicProgress("week_8", "aws", false, null, null, 0),
            SubtopicProgress("week_9", "aws", false, null, null, 0),
            SubtopicProgress("week_10", "aws", false, null, null, 0),
            SubtopicProgress("week_11", "aws", false, null, null, 0),
            SubtopicProgress("week_12", "aws", false, null, null, 0),
            SubtopicProgress("week_13", "aws", false, null, null, 0),
            SubtopicProgress("week_14", "aws", false, null, null, 0),
            SubtopicProgress("week_15", "docker", false, null, null, 0),
            SubtopicProgress("week_16", "docker", false, null, null, 0),
            SubtopicProgress("week_17", "docker", false, null, null, 0),
            SubtopicProgress("week_18", "docker", false, null, null, 0),
            SubtopicProgress("week_19", "docker", false, null, null, 0),
            SubtopicProgress("week_20", "docker", false, null, null, 0),
            SubtopicProgress("week_21", "docker", false, null, null, 0),
            SubtopicProgress("week_22", "kubernetes", false, null, null, 0),
            SubtopicProgress("week_23", "kubernetes", false, null, null, 0),
            SubtopicProgress("week_24", "kubernetes", false, null, null, 0),
            SubtopicProgress("week_25", "kubernetes", false, null, null, 0),
            SubtopicProgress("week_26", "kubernetes", false, null, null, 0),
            SubtopicProgress("week_27", "kubernetes", false, null, null, 0),
            SubtopicProgress("week_28", "kubernetes", false, null, null, 0)
        )

        for (sub in defaultSubtopics) {
            if (jeevanDao.getSubtopicProgressById(sub.subtopicId) == null) {
                jeevanDao.insertSubtopicProgress(sub)
            }
        }

        // Seed initial portfolio investments from the Google Sheet
        val defaultHoldings = listOf(
            PortfolioHolding(0, "ICICI Prudential Silver ETF", 2.0, 85.0, "ETF", 92.5, symbol = "SILVERBEES", exchange = "NSE", sector = "Commodities"),
            PortfolioHolding(0, "IOC", 3.0, 165.0, "STOCK", 172.4, symbol = "IOC", exchange = "NSE", sector = "Energy"),
            PortfolioHolding(0, "ITC", 2.0, 410.0, "STOCK", 438.5, symbol = "ITC", exchange = "NSE", sector = "Consumer Goods"),
            PortfolioHolding(0, "JSWENERGY", 1.0, 575.0, "STOCK", 622.5, symbol = "JSWENERGY", exchange = "NSE", sector = "Power"),
            PortfolioHolding(0, "NTPC", 2.0, 340.0, "STOCK", 365.2, symbol = "NTPC", exchange = "NSE", sector = "Power"),
            PortfolioHolding(0, "POWERGRID", 2.0, 280.0, "STOCK", 298.8, symbol = "POWERGRID", exchange = "NSE", sector = "Power"),
            PortfolioHolding(0, "UNION BANK", 1.0, 135.0, "STOCK", 142.1, symbol = "UNIONBANK", exchange = "BSE", sector = "Banking"),
            PortfolioHolding(0, "ICICI Prudential Gold ETF", 5.0, 60.0, "ETF", 68.4, symbol = "GOLDBEES", exchange = "NSE", sector = "Commodities"),
            PortfolioHolding(0, "ICICI Prudential NIFTY Next 50 Index Fund", 4.0, 125.0, "MF", 135.5, symbol = "ICICINIFT", exchange = "Mutual Fund", sector = "Index Funds"),
            PortfolioHolding(0, "ONGC", 2.0, 282.5, "STOCK", 295.4, symbol = "ONGC", exchange = "NSE", sector = "Energy")
        )

        // Seed default Career Investment Goals
        val defaultCareerGoalFunds = listOf(
            CareerGoalFund(0, "AWS Certification Fund", 15000.0, 9500.0),
            CareerGoalFund(0, "Emergency Fund", 60000.0, 18000.0),
            CareerGoalFund(0, "Laptop Upgrade Fund", 80000.0, 26000.0),
            CareerGoalFund(0, "DevOps Learning Fund", 20000.0, 12000.0)
        )

        if (jeevanDao.getAllPortfolioHoldingsDirect().isEmpty()) {
            defaultHoldings.forEach {
                jeevanDao.insertPortfolioHolding(it)
            }
        }

        if (jeevanDao.getAllCareerGoalFundsDirect().isEmpty()) {
            defaultCareerGoalFunds.forEach {
                jeevanDao.insertCareerGoalFund(it)
            }
        }

        // Seed initial news articles only if empty
        if (jeevanDao.getAllNewsBookmarksDirect().isEmpty()) {
            val defaultNews = listOf(
                NewsBookmark(
                    title = "Kubernetes 1.30: Pod Security Standards Graduation",
                    category = "DevOps",
                    url = "https://kubernetes.io/blog/",
                    description = "Details the transition of pod admission systems directly into high-fidelity graduation matrices. Critical migration mandatory for container sandboxes.",
                    savedAt = System.currentTimeMillis() - 86400000
                ),
                NewsBookmark(
                    title = "AWS IAM Identity Center Multi-Region Authentication Release",
                    category = "DevOps",
                    url = "https://aws.amazon.com/blogs/",
                    description = "Unveils accelerated directory federation channels across secondary fallback clusters. Zero structural changes required for AWS DevOps pipelines.",
                    savedAt = System.currentTimeMillis() - 172800000
                ),
                NewsBookmark(
                    title = "RBI Updates Digital Rupee Wallet Safeguards",
                    category = "Finance",
                    url = "https://rbi.org.in/",
                    description = "Introduces offline localized escrow contracts for purchasing power tracking in compact remote sandboxes.",
                    savedAt = System.currentTimeMillis() - 259200000
                ),
                NewsBookmark(
                    title = "Nifty 50 Hits Raw Records Driven by Steady Retail SIP Inflow Boost",
                    category = "Finance",
                    url = "https://moneycontrol.com/",
                    description = "Nifty 50 surges above critical consolidation marks, demonstrating high market integrity. Expert advisors suggest expanding index mutual fund allocations.",
                    savedAt = System.currentTimeMillis() - 364000000
                ),
                NewsBookmark(
                    title = "JOB OPENING: TCS Recruiting DevOps SRE Engineers (Chennai/Hyd)",
                    category = "Job Openings",
                    url = "https://tcs.com/careers",
                    description = "TCS is scouting professionals with 1-4 years expertise in Jenkins, Terraform, and Docker. Package: ₹6 - 11 LPA.",
                    savedAt = System.currentTimeMillis() - 43200000
                ),
                NewsBookmark(
                    title = "JOB OPENING: CRED Seeking Remote Junior Infrastructure Engineer",
                    category = "Job Openings",
                    url = "https://careers.cred.club/",
                    description = "Scale production systems on AWS, manage Kubernetes clusters, and automate with Python. Exceptional compensation perks.",
                    savedAt = System.currentTimeMillis() - 120000000
                )
            )
            defaultNews.forEach {
                jeevanDao.insertNewsBookmark(it)
            }
        }
    }
}

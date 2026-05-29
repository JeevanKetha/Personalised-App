package com.example.data.repository

import com.example.data.dao.JeevanDao
import com.example.data.entity.Transaction
import com.example.data.entity.CareerProgress
import com.example.data.entity.HealthLog
import com.example.data.entity.UserProfile
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

    // --- Profile Management ---
    suspend fun getOrInitUserProfile(): UserProfile = withContext(Dispatchers.IO) {
        val existing = jeevanDao.getUserProfileDirect()
        if (existing != null) {
            existing
        } else {
            val initial = UserProfile(
                id = 1,
                name = "Jeevan Explorer",
                jobTarget = "Cloud Platform Engineer",
                monthlyBudgetLimit = 45000.0,
                dailyWaterGoalMl = 3000,
                dailyStepGoal = 9000,
                careerStreak = 3,
                balanceAmount = 84200.0
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
        // Standard level progression algorithm: 100 XP per level
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
            addXpToTopic(topicId, 30) // Unlock 30 XP for completing a topic
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
        // Initial setup
        getOrInitUserProfile()

        val todayStr = getTodayDateString()
        if (jeevanDao.getHealthLogByDate(todayStr) == null) {
            jeevanDao.insertHealthLog(
                HealthLog(
                    dateString = todayStr,
                    waterIntakeMl = 1200,
                    caloriesBurned = 340,
                    caloriesConsumed = 1800,
                    sleepMinutes = 445, // 7 hr 25 min
                    moodScore = 4,
                    journalEntry = "Productive morning, did cloud labs and feeling confident.",
                    stepsCount = 4250
                )
            )
        }

        // Add Career presets including the 28-Week Ultimate DevOps Roadmap
        val defaultTopics = listOf(
            "linux", "kubernetes", "python", "aws", "docker",
            "week_1_2_linux",
            "week_3_4_networking",
            "week_5_6_python",
            "week_7_8_git",
            "week_9_10_cicd",
            "week_11_12_terraform",
            "week_13_14_ansible",
            "week_15_16_docker",
            "week_17_18_k8s",
            "week_19_20_aws",
            "week_21_22_security",
            "week_23_24_monitoring",
            "week_25_26_logging",
            "week_27_28_capstone"
        )
        for (topic in defaultTopics) {
            if (jeevanDao.getCareerProgressByTopic(topic) == null) {
                jeevanDao.insertCareerProgress(
                    CareerProgress(
                        topicId = topic,
                        level = 1,
                        xp = 30,
                        completedQuizIds = "intro",
                        lastActiveTime = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}

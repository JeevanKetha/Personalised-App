package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String, // e.g., "Food", "Cloud Bills", "Salary", "Gym", "Entertainment"
    val isSubscription: Boolean = false,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "career_progress")
data class CareerProgress(
    @PrimaryKey val topicId: String, // e.g., "linux", "kubernetes", "python", "aws", "docker"
    val level: Int = 1,
    val xp: Int = 0,
    val completedQuizIds: String = "", // Comma-separated list
    val completedLabIds: String = "", // Comma-separated list
    val lastActiveTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_logs")
data class HealthLog(
    @PrimaryKey val dateString: String, // "YYYY-MM-DD"
    val waterIntakeMl: Int = 0,
    val caloriesBurned: Int = 0,
    val caloriesConsumed: Int = 0,
    val sleepMinutes: Int = 0,
    val moodScore: Int = 3, // 1 (Awful) to 5 (Radiant)
    val journalEntry: String = "",
    val stepsCount: Int = 0
)

@Entity(tableName = "user_settings")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Jeevan Explorer",
    val jobTarget: String = "DevOps Engineer",
    val monthlyBudgetLimit: Double = 50000.0,
    val dailyWaterGoalMl: Int = 3000,
    val dailyStepGoal: Int = 8000,
    val careerStreak: Int = 1,
    val balanceAmount: Double = 125000.0
)

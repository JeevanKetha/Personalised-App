package com.example.data.engine

import kotlin.math.max
import kotlin.math.min

object RecoveryScoreEngine {
    /**
     * Calculates current physical readiness/recovery score (0 to 100) based on:
     * - Sleep Duration (Optimal: 7-9 hours)
     * - Average Heart Rate (Normal resting range: 60-70 bpm)
     * - Step Load (Physical strain indicator)
     * - Previous Day parameters
     */
    fun calculateRecoveryScore(
        sleepMinutes: Int,
        averageHeartRate: Int,
        stepsCount: Int,
        previousDaySleepMinutes: Int = 480,
        previousDayStepsCount: Int = 5000
    ): Int {
        // 1. Sleep score component (Max: 40 points)
        val sleepScore = when {
            sleepMinutes <= 0 -> 18.0 // Default baseline
            sleepMinutes < 300 -> 15.0 + (sleepMinutes / 300.0) * 10.0 // Under 5h
            sleepMinutes < 420 -> 25.0 + ((sleepMinutes - 300) / 120.0) * 10.0 // 5-7h
            sleepMinutes <= 540 -> 35.0 + ((sleepMinutes - 420) / 120.0) * 5.0 // 7-9h (Optimal)
            else -> max(20.0, 40.0 - ((sleepMinutes - 540) / 60.0) * 5.0) // Elevated sleep duration
        }

        // 2. Cardiopulmonary Recovery / Heart Rate indicator (Max: 30 points)
        val targetHR = if (averageHeartRate <= 0) 72 else averageHeartRate
        val hrScore = when {
            targetHR < 50 -> 20.0 // Bradycardia or highly trained runner
            targetHR in 50..62 -> 30.0 // Elite athlete resting state
            targetHR in 63..72 -> 27.0 // Healthy baseline profile
            targetHR in 73..82 -> 22.0 // Normal elevated
            targetHR in 83..92 -> 16.0 // High systemic stress
            else -> 10.0 // Insufficient active recovery
        }

        // 3. Step strain / Overexertion component (Max: 20 points)
        // Too many steps means elevated physical fatigue, lower immediate recovery score
        val stepScore = when {
            stepsCount <= 0 -> 10.0
            stepsCount < 3000 -> 15.0 // Idle
            stepsCount in 3000..8000 -> 20.0 // Ideal target zone for blood flow
            stepsCount in 8001..13000 -> 15.0 // Moderate exhaustion
            else -> max(5.0, 20.0 - ((stepsCount - 13000) / 1000.0) * 2.0) // Systemic fatigue
        }

        // 4. Previous day accumulation buffer (Max: 10 points)
        val prevSleepBonus = if (previousDaySleepMinutes >= 420) 5.0 else 2.0
        val prevStepBonus = if (previousDayStepsCount in 4000..12000) 5.0 else 3.0
        val cumulativeBonus = prevSleepBonus + prevStepBonus

        val finalScore = sleepScore + hrScore + stepScore + cumulativeBonus
        return min(100, max(0, finalScore.toInt()))
    }
}

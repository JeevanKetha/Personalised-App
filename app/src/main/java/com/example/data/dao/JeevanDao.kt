package com.example.data.dao

import androidx.room.*
import com.example.data.entity.Transaction
import com.example.data.entity.CareerProgress
import com.example.data.entity.HealthLog
import com.example.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface JeevanDao {

    // --- Transactions Queries ---
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    // --- Career Progress Queries ---
    @Query("SELECT * FROM career_progress")
    fun getAllCareerProgressFlow(): Flow<List<CareerProgress>>

    @Query("SELECT * FROM career_progress WHERE topicId = :topicId")
    suspend fun getCareerProgressByTopic(topicId: String): CareerProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerProgress(progress: CareerProgress)

    // --- Health Log Queries ---
    @Query("SELECT * FROM health_logs ORDER BY dateString DESC")
    fun getAllHealthLogsFlow(): Flow<List<HealthLog>>

    @Query("SELECT * FROM health_logs WHERE dateString = :dateString")
    suspend fun getHealthLogByDate(dateString: String): HealthLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthLog(healthLog: HealthLog)

    // --- User Profile Queries ---
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)
}

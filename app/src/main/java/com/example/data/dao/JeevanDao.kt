package com.example.data.dao

import androidx.room.*
import com.example.data.entity.Transaction
import com.example.data.entity.CareerProgress
import com.example.data.entity.HealthLog
import com.example.data.entity.UserProfile
import com.example.data.entity.SubtopicProgress
import com.example.data.entity.NewsBookmark
import com.example.data.entity.PortfolioHolding
import com.example.data.entity.CareerGoalFund
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

    // --- Subtopic Progress Queries ---
    @Query("SELECT * FROM subtopic_progress")
    fun getAllSubtopicProgressFlow(): Flow<List<SubtopicProgress>>

    @Query("SELECT * FROM subtopic_progress WHERE parentTopicId = :parentId")
    suspend fun getSubtopicProgressByParent(parentId: String): List<SubtopicProgress>

    @Query("SELECT * FROM subtopic_progress WHERE subtopicId = :subtopicId")
    suspend fun getSubtopicProgressById(subtopicId: String): SubtopicProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtopicProgress(subtopic: SubtopicProgress)

    // --- News Bookmarks Queries ---
    @Query("SELECT * FROM news_bookmarks ORDER BY savedAt DESC")
    fun getAllNewsBookmarksFlow(): Flow<List<NewsBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsBookmark(bookmark: NewsBookmark)

    @Delete
    suspend fun deleteNewsBookmark(bookmark: NewsBookmark)

    // --- Portfolio Holdings Queries ---
    @Query("SELECT * FROM portfolio_holdings")
    fun getAllPortfolioHoldingsFlow(): Flow<List<PortfolioHolding>>

    @Query("SELECT * FROM portfolio_holdings")
    suspend fun getAllPortfolioHoldingsDirect(): List<PortfolioHolding>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioHolding(holding: PortfolioHolding)

    @Delete
    suspend fun deletePortfolioHolding(holding: PortfolioHolding)

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

    // --- Career Goal Funds Queries ---
    @Query("SELECT * FROM career_goal_funds")
    fun getAllCareerGoalFundsFlow(): Flow<List<CareerGoalFund>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerGoalFund(fund: CareerGoalFund)

    @Update
    suspend fun updateCareerGoalFund(fund: CareerGoalFund)

    @Delete
    suspend fun deleteCareerGoalFund(fund: CareerGoalFund)
}

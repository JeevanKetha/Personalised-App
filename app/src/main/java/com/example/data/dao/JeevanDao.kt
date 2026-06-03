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
import com.example.data.entity.SavedResource
import com.example.data.entity.RoadmapTopic
import com.example.data.entity.RoadmapSubtopic
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

    @Query("SELECT * FROM subtopic_progress")
    suspend fun getAllSubtopicProgressDirect(): List<SubtopicProgress>

    @Query("SELECT * FROM subtopic_progress WHERE parentTopicId = :parentId")
    suspend fun getSubtopicProgressByParent(parentId: String): List<SubtopicProgress>

    @Query("SELECT * FROM subtopic_progress WHERE subtopicId = :subtopicId")
    suspend fun getSubtopicProgressById(subtopicId: String): SubtopicProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtopicProgress(subtopic: SubtopicProgress)

    @Query("DELETE FROM subtopic_progress")
    suspend fun deleteAllSubtopicProgress()

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

    @Query("SELECT * FROM career_goal_funds")
    suspend fun getAllCareerGoalFundsDirect(): List<CareerGoalFund>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerGoalFund(fund: CareerGoalFund)

    @Update
    suspend fun updateCareerGoalFund(fund: CareerGoalFund)

    @Delete
    suspend fun deleteCareerGoalFund(fund: CareerGoalFund)

    // --- News Bookmarks Direct ---
    @Query("SELECT * FROM news_bookmarks")
    suspend fun getAllNewsBookmarksDirect(): List<NewsBookmark>

    // --- Saved Resources Queries ---
    @Query("SELECT * FROM saved_resources ORDER BY id DESC")
    fun getAllSavedResourcesFlow(): Flow<List<SavedResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedResource(resource: SavedResource)

    @Delete
    suspend fun deleteSavedResource(resource: SavedResource)

    @Query("SELECT * FROM saved_resources")
    suspend fun getAllSavedResourcesDirect(): List<SavedResource>

    // --- AI Conversations Queries ---
    @Query("SELECT * FROM ai_conversations WHERE agentType = :agentType ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentConversations(agentType: String, limit: Int = 10): List<com.example.data.entity.AiConversation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(conversation: com.example.data.entity.AiConversation)

    @Query("DELETE FROM ai_conversations WHERE agentType = :agentType")
    suspend fun clearConversations(agentType: String)

    // --- Roadmap Topic Queries ---
    @Query("SELECT * FROM roadmap_topics ORDER BY orderIndex ASC")
    fun getAllTopicsFlow(): Flow<List<RoadmapTopic>>

    @Query("SELECT * FROM roadmap_topics ORDER BY orderIndex ASC")
    suspend fun getAllTopicsDirect(): List<RoadmapTopic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: RoadmapTopic): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<RoadmapTopic>)

    @Update
    suspend fun updateTopic(topic: RoadmapTopic)

    @Delete
    suspend fun deleteTopic(topic: RoadmapTopic)

    @Query("DELETE FROM roadmap_topics")
    suspend fun deleteAllTopics()

    // --- Roadmap Subtopic Queries ---
    @Query("SELECT * FROM roadmap_subtopics ORDER BY orderIndex ASC")
    fun getAllSubtopicsFlow(): Flow<List<RoadmapSubtopic>>

    @Query("SELECT * FROM roadmap_subtopics WHERE parentTopicId = :parentTopicId ORDER BY orderIndex ASC")
    fun getSubtopicsByTopicFlow(parentTopicId: Int): Flow<List<RoadmapSubtopic>>

    @Query("SELECT * FROM roadmap_subtopics WHERE parentTopicId = :parentTopicId ORDER BY orderIndex ASC")
    suspend fun getSubtopicsByTopicDirect(parentTopicId: Int): List<RoadmapSubtopic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtopic(subtopic: RoadmapSubtopic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtopics(subtopics: List<RoadmapSubtopic>)

    @Update
    suspend fun updateSubtopic(subtopic: RoadmapSubtopic)

    @Delete
    suspend fun deleteSubtopic(subtopic: RoadmapSubtopic)

    @Query("DELETE FROM roadmap_subtopics WHERE parentTopicId = :parentTopicId")
    suspend fun deleteSubtopicsByTopic(parentTopicId: Int)

    @Query("DELETE FROM roadmap_subtopics")
    suspend fun deleteAllSubtopics()
}

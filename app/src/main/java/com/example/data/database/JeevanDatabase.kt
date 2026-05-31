package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.JeevanDao
import com.example.data.entity.Transaction
import com.example.data.entity.CareerProgress
import com.example.data.entity.HealthLog
import com.example.data.entity.UserProfile
import com.example.data.entity.SubtopicProgress
import com.example.data.entity.NewsBookmark
import com.example.data.entity.PortfolioHolding
import com.example.data.entity.CareerGoalFund
import com.example.data.entity.SavedResource

@Database(
    entities = [
        Transaction::class,
        CareerProgress::class,
        HealthLog::class,
        UserProfile::class,
        SubtopicProgress::class,
        NewsBookmark::class,
        PortfolioHolding::class,
        CareerGoalFund::class,
        SavedResource::class
    ],
    version = 9,
    exportSchema = false
)
abstract class JeevanDatabase : RoomDatabase() {
    
    abstract fun jeevanDao(): JeevanDao

    companion object {
        @Volatile
        private var INSTANCE: JeevanDatabase? = null

        fun getDatabase(context: Context): JeevanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JeevanDatabase::class.java,
                    "jeevan_life_os_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

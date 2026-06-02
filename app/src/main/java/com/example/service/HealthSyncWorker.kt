package com.example.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.HealthConnectManager
import com.example.data.database.JeevanDatabase
import com.example.data.repository.JeevanRepository

class HealthSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("HealthSyncWorker", "Executing background periodic health synchronization...")
        return try {
            val database = JeevanDatabase.getDatabase(applicationContext)
            val repository = JeevanRepository(database.jeevanDao())
            val healthConnectManager = HealthConnectManager(applicationContext)
            
            repository.syncHealthFromConnect(healthConnectManager)
            Result.success()
        } catch (e: Exception) {
            Log.e("HealthSyncWorker", "Error during periodic health sync", e)
            Result.retry()
        }
    }
}

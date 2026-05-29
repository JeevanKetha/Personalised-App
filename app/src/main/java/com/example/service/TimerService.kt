package com.example.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.Locale

class TimerService : Service() {

    private val binder = LocalBinder()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var timerJob: CoroutineJob? = null
    private var ringtonePlayer: Ringtone? = null

    private fun stopAlarmSound() {
        try {
            ringtonePlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
            ringtonePlayer = null
        } catch (e: Exception) {
            Log.e("TimerService", "Failed to stop alarm", e)
        }
    }

    companion object {
        const val CHANNEL_ID = "JeevanFocusChannel"
        const val NOTIFICATION_ID = 2026
        
        var isRunning = false
            private set
        var secondsRemaining = 1500
            private set
        
        // Callback interface for UI updates
        private var onTickCallback: ((Int) -> Unit)? = null
        private var onFinishedCallback: (() -> Unit)? = null

        fun setCallbacks(onTick: (Int) -> Unit, onFinished: () -> Unit) {
            onTickCallback = onTick
            onFinishedCallback = onFinished
        }

        fun clearCallbacks() {
            onTickCallback = null
            onFinishedCallback = null
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val durationSeconds = intent?.getIntExtra("duration_seconds", 1500) ?: 1500

        when (action) {
            "START" -> startFocusTimer(durationSeconds)
            "PAUSE" -> pauseFocusTimer()
            "STOP" -> stopFocusTimer()
        }

        return START_NOT_STICKY
    }

    private fun startFocusTimer(duration: Int) {
        if (isRunning) return
        isRunning = true
        secondsRemaining = duration

        val prefs = getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
        val endTime = System.currentTimeMillis() + (duration * 1000L)
        prefs.edit()
            .putBoolean("is_active", true)
            .putLong("end_time", endTime)
            .apply()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                buildNotification("Focus session initialized. Remain in flow.", secondsRemaining),
                1073741824 // ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, buildNotification("Focus session initialized. Remain in flow.", secondsRemaining))
        }

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
                onTickCallback?.invoke(secondsRemaining)
                updateNotificationContent()
            }
            // Finished!
            triggerCompletion()
        }
    }

    private fun pauseFocusTimer() {
        isRunning = false
        timerJob?.cancel()
        val prefs = getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_active", false).putLong("end_time", 0).apply()
        stopAlarmSound()
        val formatted = formatTime(secondsRemaining)
        updateNotification("Focus paused at $formatted", false)
    }

    private fun stopFocusTimer() {
        isRunning = false
        timerJob?.cancel()
        val prefs = getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_active", false).putLong("end_time", 0).apply()
        stopAlarmSound()
        val savedDuration = prefs.getInt("custom_duration_minutes", 25)
        secondsRemaining = savedDuration * 60
        stopForeground(true)
        stopSelf()
    }

    private fun updateNotificationContent() {
        val formatted = formatTime(secondsRemaining)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification("Focusing: $formatted remaining", secondsRemaining))
    }

    private fun updateNotification(text: String, ongoing: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Jeevan Deep Focus")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(ongoing)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun triggerCompletion() {
        isRunning = false
        val prefs = getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_active", false).putLong("end_time", 0).apply()

        val vibe = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(1200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibe.vibrate(1200)
        }

        // Play alarm ringtone sound
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtonePlayer?.stop()
            ringtonePlayer = RingtoneManager.getRingtone(applicationContext, alarmUri)?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                }
                play()
            }
            // Auto stop alarm after 30 seconds to be safe
            serviceScope.launch {
                delay(30000)
                stopAlarmSound()
            }
        } catch (e: Exception) {
            Log.e("TimerService", "Failed to play alarm sound", e)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus Session Complete!")
            .setContentText("Acknowledge: Strategic DevOps focus session compiled successfully. +50 XP rewarded.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        notificationManager.notify(NOTIFICATION_ID, builder.build())
        onFinishedCallback?.invoke()
        stopForeground(false)
    }

    private fun buildNotification(text: String, seconds: Int): Notification {
        val formatted = formatTime(seconds)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Jeevan Flow Controller")
            .setContentText(text)
            .setSubText("Time Remaining: $formatted")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Jeevan OS Focus Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks deep learning sessions with background thread protections."
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun formatTime(secs: Int): String {
        val min = secs / 60
        val sec = secs % 60
        return String.format(Locale.US, "%02d:%02d", min, sec)
    }

    override fun onDestroy() {
        isRunning = false
        timerJob?.cancel()
        serviceJob.cancel()
        stopAlarmSound()
        super.onDestroy()
    }
}
typealias CoroutineJob = Job

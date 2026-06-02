package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefsManager {
    private const val PREFS_FILE_NAME = "secure_jeevan_prefs"
    private const val KEY_GEMINI_API_KEY = "gemini_api_key_encrypted"
    private const val KEY_OPENWEATHER_API_KEY = "openweather_api_key_encrypted"
    private const val TAG = "SecurePrefsManager"
    
    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (sharedPreferences != null) return
        try {
            val masterKey = MasterKey.Builder(context.applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context.applicationContext,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            Log.d(TAG, "EncryptedSharedPreferences initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing EncryptedSharedPreferences, falling back to basic SharedPreferences for safety", e)
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveGeminiApiKey(apiKey: String) {
        sharedPreferences?.edit()?.putString(KEY_GEMINI_API_KEY, apiKey)?.apply()
    }

    fun getGeminiApiKey(): String? {
        val saved = sharedPreferences?.getString(KEY_GEMINI_API_KEY, null)
        if (!saved.isNullOrBlank()) {
            return saved
        }
        val buildConfigKey = com.example.BuildConfig.GEMINI_API_KEY
        if (buildConfigKey.isNotBlank() && buildConfigKey != "MY_GEMINI_API_KEY" && buildConfigKey != "API_KEY") {
            return buildConfigKey
        }
        return null
    }

    fun saveOpenWeatherApiKey(apiKey: String) {
        sharedPreferences?.edit()?.putString(KEY_OPENWEATHER_API_KEY, apiKey)?.apply()
    }

    fun getOpenWeatherApiKey(): String? {
        return sharedPreferences?.getString(KEY_OPENWEATHER_API_KEY, null)
    }

    fun hasKey(): Boolean {
        return !getGeminiApiKey().isNullOrBlank()
    }
}

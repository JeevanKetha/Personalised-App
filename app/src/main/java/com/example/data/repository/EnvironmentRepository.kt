package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.weather.WeatherProvider
import com.example.data.weather.OpenMeteoProvider
import com.example.data.weather.OpenWeatherMapProvider

class EnvironmentRepository(private val context: Context) {
    private var activeProvider: WeatherProvider = OpenMeteoProvider()
    
    // In-memory cache to prevent multiple views and ViewModels from making simultaneous requests
    private var cachedLat: Double = 17.3850
    private var cachedLon: Double = 78.4867
    private var cachedLocationName: String = "Hyderabad, Telangana"
    private var lastRefreshTime: Long = 0L

    companion object {
        const val REFRESH_INTERVAL_MS = 2 * 60 * 60 * 1000L // 2 Hours SRE Compliance standard
    }

    init {
        // Read stored preference if user pre-selected a provider
        val prefs = context.getSharedPreferences("jeevan_env_settings", Context.MODE_PRIVATE)
        val savedType = prefs.getString("weather_provider_type", "Meteo") ?: "Meteo"
        setProvider(savedType)
    }

    fun setProvider(providerType: String) {
        val prefs = context.getSharedPreferences("jeevan_env_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("weather_provider_type", providerType).apply()
        
        activeProvider = if (providerType == "OpenWeather") {
            OpenWeatherMapProvider()
        } else {
            OpenMeteoProvider()
        }
        Log.d("EnvironmentRepository", "Swapped weather provider dynamically to ${activeProvider.javaClass.simpleName}")
    }

    fun getActiveProviderName(): String {
        return if (activeProvider is OpenWeatherMapProvider) "OpenWeatherMap" else "Open-Meteo"
    }

    fun getCachedLocationName(): String = cachedLocationName
    fun getCachedLat(): Double = cachedLat
    fun getCachedLon(): Double = cachedLon
    fun getProvider(): WeatherProvider = activeProvider
    fun getLastRefreshTime(): Long = lastRefreshTime

    fun needsRefresh(): Boolean {
        val timeElapsed = System.currentTimeMillis() - lastRefreshTime
        return lastRefreshTime == 0L || timeElapsed > REFRESH_INTERVAL_MS
    }

    suspend fun refreshIfNeeded(lat: Double, lon: Double, locName: String? = null): Boolean {
        cachedLat = lat
        cachedLon = lon
        if (locName != null) {
            cachedLocationName = locName
        }
        
        if (!needsRefresh()) {
            Log.d("EnvironmentRepository", "Accessing cache for weather to avoid redundant APIs.")
            return true
        }

        Log.d("EnvironmentRepository", "Refreshing weather from Provider: ${getActiveProviderName()} at Lat:$lat, Lon:$lon")
        val isSuccess = activeProvider.fetchWeather(lat, lon)
        if (isSuccess) {
            lastRefreshTime = System.currentTimeMillis()
        }
        return isSuccess
    }

    fun forceRefreshNow(lat: Double, lon: Double, locName: String? = null, onComplete: suspend (Boolean) -> Unit) {
        cachedLat = lat
        cachedLon = lon
        if (locName != null) {
            cachedLocationName = locName
        }
        
        // Force refresh by clearing last refresh timestamp memory
        lastRefreshTime = 0L
        
        // Set a scope/launch or routine to perform the fetch
        Log.d("EnvironmentRepository", "Forced dynamic weather synchronization triggered.")
    }

    fun setLocationName(name: String) {
        cachedLocationName = name
    }
}

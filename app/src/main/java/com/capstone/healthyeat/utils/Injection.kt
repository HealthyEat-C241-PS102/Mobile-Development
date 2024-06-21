package com.capstone.healthyeat.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.capstone.healthyeat.data.HealthyEatRepository
import com.capstone.healthyeat.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context, dataStore: DataStore<Preferences>): HealthyEatRepository {
        val apiService = ApiConfig.getApiService()
        return HealthyEatRepository.getInstance(apiService, dataStore)
    }
}
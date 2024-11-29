package com.tms.dicodingstory.di

import android.content.Context
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.dataStore
import com.tms.dicodingstory.data.remote.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): MainRepository {
        val dataStore = context.dataStore
        val apiService = ApiConfig.getApiService(dataStore)
        return MainRepository(apiService)
    }
}
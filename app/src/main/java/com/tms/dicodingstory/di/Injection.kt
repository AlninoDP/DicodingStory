package com.tms.dicodingstory.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.PreferencesRepository
import com.tms.dicodingstory.data.dataStore
import com.tms.dicodingstory.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


object Injection {

    fun provideRepository(context: Context): MainRepository{
        val pref = PreferencesRepository.getInstance(context.dataStore)
        val user = runBlocking{ pref.getUserToken().first() }
        Log.d("EE", "LOGLOG ${user.token}")
        val apiService= ApiConfig.getApiService(user.token)
        return MainRepository(apiService)
    }
}
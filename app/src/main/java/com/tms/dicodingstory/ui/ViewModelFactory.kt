package com.tms.dicodingstory.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tms.dicodingstory.MainViewModel
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.PreferencesRepository
import com.tms.dicodingstory.data.dataStore
import com.tms.dicodingstory.di.Injection
import com.tms.dicodingstory.ui.addstory.AddStoryViewModel
import com.tms.dicodingstory.ui.auth.login.LoginViewModel
import com.tms.dicodingstory.ui.auth.register.RegisterViewModel
import com.tms.dicodingstory.ui.home.HomeViewModel
import com.tms.dicodingstory.ui.maps.MapsViewModel

class ViewModelFactory private constructor(
    private val mainRepository: MainRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                val preferences = PreferencesRepository.getInstance(context.dataStore)
                instance ?: ViewModelFactory(repository, preferences)
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                preferencesRepository
            ) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(
                mainRepository
            ) as T

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                mainRepository,
                preferencesRepository
            ) as T

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(
                mainRepository,
                preferencesRepository
            ) as T

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(
                mainRepository
            ) as T

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(mainRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }


}
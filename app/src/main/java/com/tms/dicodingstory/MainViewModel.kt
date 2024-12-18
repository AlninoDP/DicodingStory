package com.tms.dicodingstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tms.dicodingstory.data.PreferencesRepository
import com.tms.dicodingstory.data.User

class MainViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {

    fun getUserToken(): LiveData<User> {
        return preferencesRepository.getUserToken().asLiveData()
    }

}
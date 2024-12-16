package com.tms.dicodingstory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.PreferencesRepository
import com.tms.dicodingstory.data.local.entity.StoryEntity
import kotlinx.coroutines.launch

class HomeViewModel(
    mainRepository: MainRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {


    val story: LiveData<PagingData<StoryEntity>> =
        mainRepository.getAllStories().cachedIn(viewModelScope)

    fun logOut() {
        viewModelScope.launch {
            preferencesRepository.deleteUserToken()
        }
    }

}
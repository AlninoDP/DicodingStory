package com.tms.dicodingstory.ui.maps

import androidx.lifecycle.ViewModel
import com.tms.dicodingstory.data.MainRepository

class MapsViewModel(
    private val mainRepository: MainRepository,
) : ViewModel() {

    fun getStoryWithLocation() = mainRepository.getStoryListForMap()
}
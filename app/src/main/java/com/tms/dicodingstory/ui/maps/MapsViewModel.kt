package com.tms.dicodingstory.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.data.exception.ApiException
import com.tms.dicodingstory.data.remote.response.ErrorResponse
import com.tms.dicodingstory.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MapsViewModel(
    private val mainRepository: MainRepository,
) : ViewModel() {
    private val _storyList = MutableLiveData<Result<List<ListStoryItem?>>>()
    val storyList: LiveData<Result<List<ListStoryItem?>>> = _storyList

    fun getStoryWithLocation() {
        _storyList.value = Result.Loading(true)

        viewModelScope.launch {
            try {
                val response = mainRepository.getStories(location = 1)
                val stories = response.listStory
                _storyList.value = Result.Success(stories ?: emptyList())
            } catch (e: HttpException) {
                val errorMessageJson = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(errorMessageJson, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Unknown error occurred"
                _storyList.value = Result.Failure(ApiException(errorMessage))
            } catch (e: Exception) {
                _storyList.value = Result.Failure(e)
            } finally {
                _storyList.value = Result.Loading(false)
            }
        }

    }
}
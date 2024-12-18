package com.tms.dicodingstory.ui.addstory

import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.data.exception.ApiException
import com.tms.dicodingstory.data.remote.response.ErrorResponse
import com.tms.dicodingstory.data.remote.response.PostStoryResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class AddStoryViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _postStoryResponse = MutableLiveData<Result<PostStoryResponse>>()
    val postStoryResponse: LiveData<Result<PostStoryResponse>> get() = _postStoryResponse

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _descriptionText = MutableLiveData<String>()
    val descriptionText: LiveData<String> get() = _descriptionText

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?> get() = _currentLocation

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun updateDescriptionText(newText: String) {
        _descriptionText.value = newText
    }

    fun updateCurrentLocation(location: Location?) {
        _currentLocation.value = location
    }

    fun uploadStory(
        imageFile: File,
        description: String,
        latitude: Float? = null,
        longitude: Float? = null
    ) {
        viewModelScope.launch {
            _postStoryResponse.value = Result.Loading(true)
            try {
                val response =
                    mainRepository.uploadStory(imageFile, description, latitude, longitude)
                _postStoryResponse.value = Result.Success(response)
            } catch (e: HttpException) {
                val errorMessageJson = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(errorMessageJson, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Unknown error occurred"
                _postStoryResponse.value = Result.Failure(ApiException(errorMessage))
            } catch (e: Exception) {
                _postStoryResponse.value = Result.Failure(e)
            } finally {
                _postStoryResponse.value = Result.Loading(false)
            }
        }
    }


}
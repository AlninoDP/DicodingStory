package com.tms.dicodingstory.ui.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.data.remote.response.PostStoryResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class AddStoryViewModel(private val mainRepository: MainRepository): ViewModel() {

    private val _postStoryResponse = MutableLiveData<Result<PostStoryResponse>> ()
    val postStoryResponse: LiveData<Result<PostStoryResponse>> get() = _postStoryResponse

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get () = _imageUri

    private val _descriptionText = MutableLiveData<String>()
    val descriptionText: LiveData<String> get () = _descriptionText

    fun setImageUri(uri:Uri?) {
        _imageUri.value = uri
    }

    fun updateDescriptionText(newText: String) {
        _descriptionText.value = newText
    }

    fun uploadStory(imageFile: File, description:String) {
        viewModelScope.launch {
            _postStoryResponse.value = Result.Loading(true)
            try{
                val response = mainRepository.uploadStory(imageFile, description)
                _postStoryResponse.value = Result.Success(response)
            } catch (e: HttpException) {
                _postStoryResponse.value = Result.Failure(e)
            }catch (e: Exception) {
                _postStoryResponse.value = Result.Failure(e)
            } finally {
                _postStoryResponse.value = Result.Loading(false)
            }
        }
    }


}
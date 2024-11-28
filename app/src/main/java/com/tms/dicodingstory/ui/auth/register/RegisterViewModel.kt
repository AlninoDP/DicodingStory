package com.tms.dicodingstory.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.data.exception.ApiException
import com.tms.dicodingstory.data.remote.response.ErrorResponse
import com.tms.dicodingstory.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {

    private val _registerResponse = MutableLiveData<Result<RegisterResponse>>()
    val registerResponse: LiveData<Result<RegisterResponse>> get() = _registerResponse


    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResponse.value = Result.Loading(true)
            try {
                val response = repository.registerUser(name, email, password)
                _registerResponse.value = Result.Success(response)
            } catch (e: HttpException) {
                val errorMessageJson = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(errorMessageJson, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Unknown error occurred"
                _registerResponse.value = Result.Failure(ApiException(errorMessage))
            } catch (e: Exception) {
                _registerResponse.value = Result.Failure(e)
            } finally {
                _registerResponse.value = Result.Loading(false)
            }
        }
    }
}
package com.tms.dicodingstory.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.PreferencesRepository
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.data.User
import com.tms.dicodingstory.data.exception.ApiException
import com.tms.dicodingstory.data.remote.response.ErrorResponse
import com.tms.dicodingstory.data.remote.response.LoginResponse
import com.tms.dicodingstory.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val mainRepository: MainRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Result<LoginResponse>>()
    val loginResponse: LiveData<Result<LoginResponse>> get() = _loginResponse

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResponse.value = Result.Loading(true)

            wrapEspressoIdlingResource {
                try {
                    val response = mainRepository.login(email, password)
                    val userId = response.loginResult.userId
                    val name = response.loginResult.name
                    val token = response.loginResult.token
                    val user = User(userId, name, token)
                    preferencesRepository.saveUserToken(user)
                    _loginResponse.value = Result.Success(response)
                } catch (e: HttpException) {
                    val errorMessageJson = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(errorMessageJson, ErrorResponse::class.java)
                    val errorMessage = errorBody.message ?: "Unknown error occurred"
                    _loginResponse.value = Result.Failure(ApiException(errorMessage))
                } catch (e: Exception) {
                    _loginResponse.value = Result.Failure(e)
                } finally {
                    _loginResponse.value = Result.Loading(false)
                }
            }
        }

    }

}
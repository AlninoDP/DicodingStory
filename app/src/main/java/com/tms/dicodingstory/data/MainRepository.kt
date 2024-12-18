package com.tms.dicodingstory.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.tms.dicodingstory.data.local.entity.StoryEntity
import com.tms.dicodingstory.data.local.room.StoryDatabase
import com.tms.dicodingstory.data.remote.response.ListStoryItem
import com.tms.dicodingstory.data.remote.response.LoginResponse
import com.tms.dicodingstory.data.remote.response.PostStoryResponse
import com.tms.dicodingstory.data.remote.response.RegisterResponse
import com.tms.dicodingstory.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    fun getStoryListForMap(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading(true))
        try {
            val response = apiService.getStories(size = 20, location = 1)
            val stories = response.listStory
            emit(Result.Loading(false))
            emit(Result.Success(stories))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).liveData
    }

    suspend fun uploadStory(
        imageFile: File, description: String,
        latitude: Float? = null,
        longitude: Float? = null
    ): PostStoryResponse {
        // Prepare description
        val requestBody = description.toRequestBody("text/plain".toMediaType())

        // Prepare Image File
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        // Prepare latitude and longitude
        val latitudeRequestBody = latitude?.toString()?.toRequestBody("text/plain".toMediaType())
        val longitudeRequestBody = longitude?.toString()?.toRequestBody("text/plain".toMediaType())

        return apiService.uploadStory(
            multipartBody,
            requestBody,
            latitudeRequestBody,
            longitudeRequestBody
        )
    }

}
package com.tms.dicodingstory.data.remote.retrofit


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tms.dicodingstory.BuildConfig
import com.tms.dicodingstory.data.PreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(datastore: DataStore<Preferences>): ApiService {

            val pref = PreferencesRepository.getInstance(datastore)

            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val authInterceptor = Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val authToken = runBlocking {
                    pref.getUserToken().firstOrNull()?.token
                }
                if (!authToken.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $authToken")
                }
                chain.proceed(requestBuilder.build())
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)

        }
    }
}
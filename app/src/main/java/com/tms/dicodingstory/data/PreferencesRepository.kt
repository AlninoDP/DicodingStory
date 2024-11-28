package com.tms.dicodingstory.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class User(
    val userId: String,
    val name: String,
    val token: String
)

class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun getUserToken(): Flow<User> {
        return dataStore.data.map { preferences ->
            val userId = preferences[USER_ID_KEY] ?: ""
            val name = preferences[USER_NAME_KEY] ?: ""
            val token = preferences[TOKEN_KEY] ?: ""
            User(userId, name, token)
        }
    }

    suspend fun saveUserToken(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[USER_NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
        }
    }

    suspend fun deleteUserToken() {
        dataStore.edit {
            it.clear()
        }
    }


    companion object {
        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val USER_NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        @Volatile
        private var INSTANCE: PreferencesRepository? = null

        fun getInstance(dataStore: DataStore<Preferences>): PreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferencesRepository(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }

}
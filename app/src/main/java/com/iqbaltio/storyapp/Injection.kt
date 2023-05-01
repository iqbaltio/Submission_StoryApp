package com.iqbaltio.storyapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.iqbaltio.storyapp.api.ApiConfig
import com.iqbaltio.storyapp.data.StoryAppRepository
import com.iqbaltio.storyapp.data.UserPreferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

object Injection {
    fun provideRepository(context: Context): StoryAppRepository {
        val preferences = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryAppRepository(preferences, apiService)
    }
}
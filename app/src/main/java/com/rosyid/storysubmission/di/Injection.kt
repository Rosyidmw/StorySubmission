package com.rosyid.storysubmission.di

import android.content.Context
import com.rosyid.storysubmission.data.pref.UserPreference
import com.rosyid.storysubmission.data.pref.dataStore
import com.rosyid.storysubmission.data.remote.StoryRepository
import com.rosyid.storysubmission.data.remote.UserRepository
import com.rosyid.storysubmission.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = ApiConfig.getApiService(pref)
        return UserRepository.getInstance(user, pref)
    }

    fun providerStoriesRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = ApiConfig.getApiService(pref)
        return StoryRepository.getInstance(user)
    }
}
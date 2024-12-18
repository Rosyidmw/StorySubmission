package com.rosyid.storysubmission.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rosyid.storysubmission.data.remote.Result
import com.rosyid.storysubmission.data.remote.StoryRepository
import com.rosyid.storysubmission.data.remote.pref.ListStoryItem

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getStoriesWithLocation() : LiveData<Result<List<ListStoryItem>>> {
        return storyRepository.getStoriesWithLocation()
    }
}
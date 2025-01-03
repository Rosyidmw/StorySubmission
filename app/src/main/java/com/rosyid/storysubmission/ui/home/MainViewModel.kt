package com.rosyid.storysubmission.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rosyid.storysubmission.data.pref.UserModel
import com.rosyid.storysubmission.data.remote.StoryRepository
import com.rosyid.storysubmission.data.remote.UserRepository
import com.rosyid.storysubmission.data.remote.pref.ListStoryItem
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStory() = storyRepository.getStories()

    fun getPagedStories(): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getPagedStories().cachedIn(viewModelScope)
    }

    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    fun uploadStory(imageFile: File, desc: String, latitude: Double?, longitude: Double?) =
        storyRepository.uploadStory(imageFile, desc, latitude, longitude)

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}
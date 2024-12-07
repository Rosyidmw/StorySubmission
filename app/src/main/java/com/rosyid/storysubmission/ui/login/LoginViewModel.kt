package com.rosyid.storysubmission.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rosyid.storysubmission.data.pref.UserModel
import com.rosyid.storysubmission.data.remote.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
    fun login(email: String, password: String) = repository.login(email, password)

    fun getSession() = repository.getSession().asLiveData()
}
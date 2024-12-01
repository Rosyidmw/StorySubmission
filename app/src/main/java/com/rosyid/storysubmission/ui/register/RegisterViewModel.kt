package com.rosyid.storysubmission.ui.register

import androidx.lifecycle.ViewModel
import com.rosyid.storysubmission.data.remote.UserRepository

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}
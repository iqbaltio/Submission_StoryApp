package com.iqbaltio.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.iqbaltio.storyapp.data.ListStory
import com.iqbaltio.storyapp.data.LoginRequest
import com.iqbaltio.storyapp.data.StoryAppRepository
import com.iqbaltio.storyapp.data.UserModels
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repo: StoryAppRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repo.dataRegister(name, email, password)

    fun login(loginRequest: LoginRequest) = repo.dataLogin(loginRequest)
    fun saveUser(user : UserModels){
        viewModelScope.launch {
            repo.saveUserData(user)
        }
    }

    fun logout(){
        viewModelScope.launch {
            repo.logout()
        }
    }

    fun getStory() : LiveData<PagingData<ListStory>>{
        return repo.getStory().cachedIn(viewModelScope)
    }

    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody) =
        repo.addStory(token, file, description)

    fun getUser(): LiveData<UserModels> {
        return repo.getUserData()
    }
}
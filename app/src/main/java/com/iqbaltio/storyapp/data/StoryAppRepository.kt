package com.iqbaltio.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.iqbaltio.storyapp.api.ApiService
import com.iqbaltio.storyapp.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryAppRepository(private val preferences: UserPreferences, private val apiService: ApiService) {
    fun dataRegister(name: String, email: String, password: String) : LiveData<Result<RegisterResponse>> =
    liveData{
        try {
            val response = apiService.registerDO(
                RegisterRequest(name, email, password)
            )
            emit(Result.Success(response))
        }catch (e: Exception){
            Log.d("Register", e.message.toString())
            emit(Result.Error(e.message.toString()))
        }
    }

    fun dataLogin(loginRequest: LoginRequest) : LiveData<Result<StoryResponse>> =
        liveData {
            try {
                val response = apiService.loginDO(loginRequest)
                emit(Result.Success(response))
            }catch (e: Exception){
                Log.d("Login", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getUserData() : LiveData<UserModels>{
        return preferences.getUser().asLiveData()
    }

    suspend fun saveUserData(user : UserModels){
        preferences.saveUser(user)
    }

    suspend fun login(){
        preferences.login()
    }

    suspend fun logout(){
        preferences.logout()
    }

    fun getStory(): LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, preferences)
            }
        ).liveData
    }

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage(token, file, description)
            emit(Result.Success(response))
        } catch (e: java.lang.Exception) {
            Log.d("Signup", e.message.toString())
            emit(Result.Error(e.message.toString()))
        }
    }
}
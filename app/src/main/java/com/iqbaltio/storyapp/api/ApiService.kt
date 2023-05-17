package com.iqbaltio.storyapp.api

import com.iqbaltio.storyapp.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun loginDO(
        @Body req: LoginRequest
    ): StoryResponse

    @POST("register")
    suspend fun registerDO(
        @Body request: RegisterRequest
    ): RegisterResponse

    @GET("stories")
    suspend fun allStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,)
    : AllStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): FileUploadResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Header("Authorization") auth: String,
        @Query("location") location : Int = 1,
    ): AllStoriesResponse
}
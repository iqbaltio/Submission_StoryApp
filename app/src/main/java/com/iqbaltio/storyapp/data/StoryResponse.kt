package com.iqbaltio.storyapp.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

data class StoryResponse(
	val loginResult: LoginResult? = null,
	val error: Boolean? = null,
	val message: String? = null
)

data class LoginResult(
	val name: String? = null,
	val userId: String? = null,
	val token: String? = null
)

data class LoginRequest(
	@SerializedName("email")
	@Expose
	val email: String? = null,

	@SerializedName("password")
	@Expose
	val password: String? = null
)

data class RegisterResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class RegisterRequest(
	val name: String? = null,
	val email: String? = null,
	val password: String? = null
)

data class UserModels(
	val name: String,
	val token: String,
	val isLogin: Boolean
)

@Parcelize
data class ListStory(
	@field:SerializedName("name")
	var name: String?,

	@field:SerializedName("description")
	var description: String?,

	@field:SerializedName("photoUrl")
	var photoUrl: String?,

	@field:SerializedName("createdAt")
	var createdAt: String?,

	@field:SerializedName("lon")
	var longitude: Double,

	@field:SerializedName("lat")
	var latitude: Double,

	@field:SerializedName("id")
	var id: String
) : Parcelable

data class AllStoriesResponse (
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("listStory")
	val ListStory: ArrayList<ListStory>
)

data class FileUploadResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)


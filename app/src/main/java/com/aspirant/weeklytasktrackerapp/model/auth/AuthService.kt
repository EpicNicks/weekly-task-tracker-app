package com.aspirant.weeklytasktrackerapp.model.auth

import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse

interface AuthService {
    suspend fun login(username: String, password: String, onLoginResponse: (ApiResponse<String>?) -> Unit)

    fun getAuthToken(): String?

    fun logout()
}
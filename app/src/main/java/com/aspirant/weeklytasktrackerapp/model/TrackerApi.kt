package com.aspirant.weeklytasktrackerapp.model

import com.aspirant.weeklytasktrackerapp.model.entity.request.LoginRequest
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TrackerApi {
    @POST("account/login")
    fun login(@Body requestBody: LoginRequest): Call<ApiResponse<String>>
}


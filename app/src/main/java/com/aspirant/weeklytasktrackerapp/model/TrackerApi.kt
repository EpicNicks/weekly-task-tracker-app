package com.aspirant.weeklytasktrackerapp.model

import com.aspirant.weeklytasktrackerapp.model.entity.request.LoginRequest
import com.aspirant.weeklytasktrackerapp.model.entity.request.UpdateLogMinutesRequest
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerapp.model.entity.response.DailyLog
import com.aspirant.weeklytasktrackerapp.model.entity.response.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackerApi {

    // account queries
    @POST("account/login")
    fun login(@Body requestBody: LoginRequest): Call<ApiResponse<String>>

    // task queries
    @GET("tasks/active")
    fun getActiveTasks(@Header("Authorization") authorizationString: String): Call<ApiResponse<List<Task>>>

    // daily log queries
    @GET("logs/{logDate}/{taskId}")
    fun getLogForDate(
        @Path("logDate") logDate: String,
        @Path("taskId") taskId: Int,
        @Header("Authorization") authorizationString: String
    ): Call<ApiResponse<DailyLog>>

    @GET("logs/all")
    fun getAllLogsInRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Header("Authorization") authorizationString: String
    ): Call<ApiResponse<List<DailyLog>>>

    @PATCH("logs/change-daily-minutes/{logDate}")
    fun updateLogMinutes(
        @Path("logDate") logDate: String,
        @Body updateLogMinutesRequest: UpdateLogMinutesRequest,
        @Header("Authorization") authorizationString: String
    ): Call<ApiResponse<DailyLog>>

    @POST("logs/create")
    fun createLog(
        @Body dailyLog: DailyLog,
        @Header("Authorization") authorizationString: String
    ): Call<ApiResponse<DailyLog>>
}


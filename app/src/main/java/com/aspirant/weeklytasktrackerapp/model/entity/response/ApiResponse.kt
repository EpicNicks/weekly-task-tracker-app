package com.aspirant.weeklytasktrackerapp.model.entity.response

sealed class ApiResponse<out T> {
    data class Success<T>(val success: Boolean, val value: T) : ApiResponse<T>()
    data class Failure(val success: Boolean, val error: String) : ApiResponse<Nothing>()

    companion object {
        fun <T> success(value: T): ApiResponse<T> = Success(true, value)
        fun failure(error: String): ApiResponse<Nothing> = Failure(false, error)
    }
}
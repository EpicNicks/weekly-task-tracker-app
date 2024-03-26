package com.aspirant.weeklytasktrackerapp.model.auth

import android.content.Context
import android.content.SharedPreferences
import com.aspirant.weeklytasktrackerapp.model.RetrofitInstance
import com.aspirant.weeklytasktrackerapp.model.entity.request.LoginRequest
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException

class SharedPreferencesAuthService(context: Context) : AuthService {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    override suspend fun login(username: String, password: String): ApiResponse<String> {
        var result: ApiResponse<String>? = null
        RetrofitInstance.api.login(LoginRequest(username, password))
            .enqueue(object : Callback<ApiResponse<String>> {
                override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                    // Error logging in
                }

                override fun onResponse(call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>) {
                    when (val loginResponse = response.body()) {
                        is ApiResponse.Success -> {
                            with(sharedPreferences.edit()) {
                                putString(TOKEN_KEY, loginResponse.value)
                                apply()
                            }
                            result = loginResponse
                        }

                        is ApiResponse.Failure -> {
                            result = loginResponse
                        }

                        else -> {
                            result = ApiResponse.failure("An unexpected error has occurred. Body was null")
                        }
                    }
                }
            })
        if (result == null) {
            return ApiResponse.failure("An even more unexpected error has occurred")
        }
        return result as ApiResponse<String>
    }

    override fun getAuthToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    override fun logout() {
        with(sharedPreferences.edit()) {
            putString(TOKEN_KEY, null)
            apply()
        }
    }

    companion object {
        const val TOKEN_KEY = "token"
    }
}
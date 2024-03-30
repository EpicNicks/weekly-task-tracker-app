package com.aspirant.weeklytasktrackerapp.model.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.aspirant.weeklytasktrackerapp.model.RetrofitInstance
import com.aspirant.weeklytasktrackerapp.model.entity.request.LoginRequest
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedPreferencesAuthService(context: Context) : AuthService {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    override suspend fun login(
        username: String,
        password: String,
        onLoginResponse: (ApiResponse<String>?) -> Unit
    ) {
        val call = RetrofitInstance.api.login(LoginRequest(username, password))
        call.enqueue(object : Callback<ApiResponse<String>> {
            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                Log.e("loginResponse", "failure ${t.message}")
            }

            override fun onResponse(call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>) {
                Log.i("loginResponse", "${response.headers()}, ${response.body()}")
                Log.i("loginResponse", "success: ${response.isSuccessful}, response code: ${response.code()}")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        when (loginResponse) {
                            is ApiResponse.Success -> {
                                with(sharedPreferences.edit()) {
                                    putString(TOKEN_KEY, loginResponse.value)
                                    apply()
                                }
                            }

                            is ApiResponse.Failure -> {}
                        }
                    }
                    onLoginResponse(loginResponse)
                } else {
                    val loginResponse = response.errorBody()
                    if (loginResponse != null) {
                        Log.e("loginResponse", "login error body: $loginResponse")
                    }
                    onLoginResponse(ApiResponse.failure(loginResponse.toString()))
                }
            }
        })
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
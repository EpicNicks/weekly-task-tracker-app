package com.aspirant.weeklytasktrackerapp.view.tasktracker.weeklyview

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aspirant.weeklytasktrackerapp.model.RetrofitInstance
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerapp.model.entity.response.DailyLog
import com.aspirant.weeklytasktrackerapp.model.entity.response.Task
import com.aspirant.weeklytasktrackerapp.utils.dateFormat
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

fun datesOfWeek(date: LocalDate = LocalDate.now()): List<LocalDate> {
    val monday = date.minusDays(date.dayOfWeek.ordinal.toLong())
    return (0..6).map { monday.plusDays(it.toLong()) }
}

@Composable
fun WeeklyView(viewModel: WeeklyViewViewModel) {

    val selectedDate by rememberUpdatedState(newValue = viewModel.selectedDate)
    val logMap by rememberUpdatedState(newValue = viewModel.logMap)
    val tasks by rememberUpdatedState(newValue = viewModel.tasks)

    Column {
        datesOfWeek(selectedDate).map {
            WeeklyViewDay(
                viewModel = WeeklyViewDayViewModel(
                    date = it,
                    logs = logMap[dateFormat(it, "-")] ?: listOf(),
                    authService = viewModel.getAuthService(),
                    tasks = tasks,
                    onNavigateToLogin = viewModel.getOnNavigateToLogin()
                )
            )
        }
    }
}

class WeeklyViewViewModel(
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit
) : ViewModel() {

    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
        private set

    var logMap by mutableStateOf(mapOf<String, List<DailyLog>>())
        private set

    var tasks by mutableStateOf(listOf<Task>())
        private set

    init {
        getActiveTasks()
        getAllLogsForWeek()
    }

    fun getAuthService() = authService
    fun getOnNavigateToLogin() = onNavigateToLogin
    private fun getActiveTasks() {
        val authString = AuthService.authHeaderString(authService) ?: return
        viewModelScope.launch {
            val logTag = "get active tasks from Weekly View"
            val call = RetrofitInstance.api.getActiveTasks(authString)
            call.enqueue(object : Callback<ApiResponse<List<Task>>> {
                override fun onFailure(call: Call<ApiResponse<List<Task>>>, throwable: Throwable) {
                    Log.e(logTag, "getTasks onFailure ${throwable.message}")
                }

                override fun onResponse(
                    call: Call<ApiResponse<List<Task>>>,
                    response: Response<ApiResponse<List<Task>>>
                ) {
                    Log.i(logTag, "getTasks onResponse")
                    val apiResponse = response.body()
                    if (apiResponse == null) {
                        Log.w(logTag, "apiResponse body was null")
                        return
                    }
                    when (apiResponse) {
                        is ApiResponse.Success -> {
                            Log.i(logTag, "apiResponse: ${apiResponse.value}")
                            tasks = apiResponse.value
                        }

                        is ApiResponse.Failure -> {
                            Log.e(logTag, "apiResponse success: ${apiResponse.success}")
                        }
                    }
                }
            })
        }
    }

    fun getAllLogsForWeek() {
        val authToken = authService.getAuthToken()
        val weeklyViewTag = "weekly view"
        if (authToken == null) {
            Log.e(weeklyViewTag, "auth token was null")
            return
        }
        val authString = AuthService.authHeaderString(authToken)
        val dates = datesOfWeek(selectedDate)
        viewModelScope.launch {
            val call = RetrofitInstance.api.getAllLogsInRange(dateFormat(dates[0]), dateFormat(dates[6]), authString)
            call.enqueue(object : Callback<ApiResponse<List<DailyLog>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<DailyLog>>>,
                    response: Response<ApiResponse<List<DailyLog>>>
                ) {
                    Log.i(
                        weeklyViewTag,
                        "success: ${response.isSuccessful}, status: ${response.code()}, ${
                            response.raw().request().url()
                        }"
                    )
                    if (!response.isSuccessful) {
                        Log.e(weeklyViewTag, "response was not successful")
                        return
                    }
                    val responseBody = response.body()
                    if (responseBody == null) {
                        Log.e(weeklyViewTag, "response body was null")
                        return
                    }
                    when (responseBody) {
                        is ApiResponse.Success -> {
                            val mutLogMap = mutableMapOf<String, MutableList<DailyLog>>()
                            for (date in dates) {
                                mutLogMap[dateFormat(date, "-")] = mutableListOf()
                            }
                            for (log in responseBody.value) {
                                mutLogMap[log.logDate]?.add(log)
                            }
                            logMap = mutLogMap
                        }

                        is ApiResponse.Failure -> {}
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<DailyLog>>>, throwable: Throwable) {
                    Log.e(weeklyViewTag, "onFailure message: ${throwable.message}")
                }

            })
        }
    }
}
package com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import retrofit2.Callback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aspirant.weeklytasktrackerapp.model.RetrofitInstance
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerapp.model.entity.response.Task
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate

@Composable
fun TodayScreen(viewModel: TodayViewModel) {

    val tasks = rememberUpdatedState(newValue = viewModel.tasks)

    Column() {
        when (tasks.value.size) {
            0 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Looks like you have no tasks yet")
                    Button(
                        onClick = { viewModel.getUpdateDrawerIndex()(2) }
                    ) {
                        Text(text = "Click here to go make one")
                    }
                }
            }

            else -> {
                tasks.value.map { task ->
                    DailyLogCard(
                        viewModel = DailyLogCardViewModel(
                            authService = viewModel.getAuthService(),
                            onNavigateToLogin = viewModel.getOnNavigateToLogin(),
                            task = task,
                            date = LocalDate.now()
                        )
                    )
                }
            }
        }
    }
}

class TodayViewModel(
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit,
    private val updateDrawerIndex: (Int) -> Unit
) : ViewModel() {

    var tasks by mutableStateOf<List<Task>>(listOf())
        private set

    fun getAuthService() = authService
    fun getOnNavigateToLogin() = onNavigateToLogin
    fun getUpdateDrawerIndex() = updateDrawerIndex

    init {
        if (authService.getAuthToken() == null) {
            onNavigateToLogin()
        } else {
            getTasks()
        }
    }


    private fun getTasks() {
        val authToken = authService.getAuthToken()
        if (authToken == null) {
            onNavigateToLogin()
            return
        }
        val authString = AuthService.authHeaderString(authToken)
        viewModelScope.launch {
            val logTag = "get active tasks from TodayView"
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
}
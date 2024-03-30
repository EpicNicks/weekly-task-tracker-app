package com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aspirant.weeklytasktrackerapp.model.RetrofitInstance
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService
import com.aspirant.weeklytasktrackerapp.model.entity.request.UpdateLogMinutesRequest
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerapp.model.entity.response.DailyLog
import com.aspirant.weeklytasktrackerapp.model.entity.response.Task
import com.aspirant.weeklytasktrackerapp.utils.dateFormat
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import kotlin.math.floor

@Composable
fun DailyLogCard(viewModel: DailyLogCardViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val currentLogData by rememberUpdatedState(newValue = viewModel.currentLogData)
    var hourString by remember { mutableStateOf(((currentLogData?.dailyTimeMinutes ?: 0) / 60).toString()) }
    var minuteString by remember { mutableStateOf(((currentLogData?.dailyTimeMinutes ?: 0) % 60).toString()) }

    val task = viewModel.getTask()

    fun toTimeString(minutesLogged: Int): String {
        val hours = floor(minutesLogged / 60.0).toInt()
        val minutes = floor(minutesLogged % 60.0).toInt()
        return "${if (hours > 0) "$hours  hours and" else ""} $minutes minutes"
    }

    fun taskColor(): Color {
        val a = task.rgbTaskColor.substring(6, 8)
        val rgb = task.rgbTaskColor.substring(0, 6)
        return Color(android.graphics.Color.parseColor("#$a$rgb"))
    }


    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(4.dp)
            .border(width = 2.dp, color = taskColor())
    ) {
        Column {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { expanded = !expanded }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // left "border"
//                    Box(
//                        modifier = Modifier
//                            .size(width = 8.dp, height = 50.dp)
//                            .background(color = taskColor())
//                    )
                    Text(text = task.taskName, modifier = Modifier.padding(8.dp))
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }
            HorizontalDivider(color = taskColor())
            if (!expanded) {
                Text(
                    text = "Time spent today: ${toTimeString(currentLogData?.dailyTimeMinutes ?: 0)}",
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time Logged",
                        modifier = Modifier.weight(1f)
                    )
                    TextField(
                        value = hourString,
                        onValueChange = {
                            hourString = it
                            if (hourString.isNotEmpty() && hourString.toInt() >= 24) {
                                hourString = "24"
                                minuteString = "0"
                                viewModel.updateMinutes(0)
                            }
                            viewModel.updateHours(if (hourString.isNotEmpty()) hourString.toInt() else 0)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged {
                                if (!it.isFocused && hourString.isEmpty()) {
                                    hourString = "0"
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Text(text = "h")
                        }
                    )
                    TextField(
                        value = minuteString,
                        onValueChange = {
                            minuteString = it
                            viewModel.updateMinutes(if (minuteString.isNotEmpty()) minuteString.toInt() else 0)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged {
                                if (!it.isFocused && minuteString.isEmpty()) {
                                    minuteString = "0"
                                }
                                var minutesInt = minuteString.toInt()
                                if (minutesInt > 60) {
                                    if (hourString.isEmpty()) {
                                        hourString = "0"
                                    }
                                    hourString = (hourString.toInt() + minutesInt / 60).toString()
                                    minutesInt %= 60
                                    minuteString = minutesInt.toString()
                                    viewModel.updateMinutes(minutesInt)
                                    viewModel.updateHours(hourString.toInt())
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Text(text = "m")
                        }
                    )
                }
                Button(
                    onClick = { viewModel.updateDailyLog() },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 20.dp)
                ) {
                    Text(text = "Update Daily Log")
                }
            }
        }
    }
}

class DailyLogCardViewModel(
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit,
    private val task: Task,
    private val date: LocalDate
) : ViewModel() {
    var currentLogData by mutableStateOf<DailyLog?>(null)
        private set

    private var minutes by mutableIntStateOf(0)
    private var hours by mutableIntStateOf(0)

    init {
        initDailyLog()
    }

    fun updateMinutes(minutes: Int) {
        this.minutes = minutes
    }

    fun updateHours(hours: Int) {
        this.hours = hours
    }

    private fun initDailyLog() {
        val authToken = authService.getAuthToken()
        if (authToken == null) {
            onNavigateToLogin()
            return
        }
        val logCardTag = "log card for task with name: ${task.taskName}"
        viewModelScope.launch {
            val formattedDate = dateFormat(date)
            Log.i(logCardTag, "api called with logs/${formattedDate}/${task.id}")
            val call = RetrofitInstance.api.getLogForDate(formattedDate, task.id, authToken)
            call.enqueue(object : Callback<ApiResponse<DailyLog>> {
                override fun onResponse(
                    call: Call<ApiResponse<DailyLog>>,
                    response: Response<ApiResponse<DailyLog>>
                ) {
                    Log.i(logCardTag, "success: ${response.isSuccessful}, response code: ${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e(logCardTag, "onResponse response was null")
                            return
                        } else {
                            when (responseBody) {
                                is ApiResponse.Success -> {
                                    Log.i(logCardTag, "logData received: ${responseBody.value}")
                                    currentLogData = responseBody.value
                                }

                                is ApiResponse.Failure -> {}
                            }
                        }
                    } else {
                        val responseBody = response.errorBody()
                        if (responseBody == null) {
                            Log.e(logCardTag, "onResponse error body was null")
                        } else {
                            Log.e(logCardTag, "onResponse error body: $responseBody")
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<DailyLog>>, throwable: Throwable) {
                    Log.e(logCardTag, "onFailure ${throwable.message}")
                }

            })
        }
    }

    fun getTask() = task


    fun updateDailyLog() {
        val authToken = authService.getAuthToken()
        if (authToken == null) {
            onNavigateToLogin()
            return
        }
        val logCardTag = "log card for task with name: ${task.taskName}"
        val dailyTimeMinutes = hours * 60 + minutes
        viewModelScope.launch {
            val call = RetrofitInstance.api.updateLogMinutes(
                logDate = dateFormat(date),
                authorizationString = authToken,
                updateLogMinutesRequest = UpdateLogMinutesRequest(taskId = task.id, dailyTimeMinutes = dailyTimeMinutes)
            )
            call.enqueue(object : Callback<ApiResponse<DailyLog>> {
                override fun onResponse(p0: Call<ApiResponse<DailyLog>>, p1: Response<ApiResponse<DailyLog>>) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(p0: Call<ApiResponse<DailyLog>>, p1: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}
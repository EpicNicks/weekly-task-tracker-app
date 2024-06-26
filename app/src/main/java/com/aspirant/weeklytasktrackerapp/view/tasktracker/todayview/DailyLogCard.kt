package com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DailyLogCard(viewModel: DailyLogCardViewModel) {
    val (focusRequester) = FocusRequester.createRefs()
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentLogData by rememberUpdatedState(newValue = viewModel.currentLogData)
    var hourString by remember(currentLogData) {
        mutableStateOf(((currentLogData?.dailyTimeMinutes ?: 0) / 60).toString())
    }
    var minuteString by remember(currentLogData) {
        mutableStateOf(((currentLogData?.dailyTimeMinutes ?: 0) % 60).toString())
    }

    LaunchedEffect(key1 = currentLogData) {
        if (currentLogData != null) {
            viewModel.updateHours(currentLogData!!.dailyTimeMinutes / 60)
            viewModel.updateMinutes(currentLogData!!.dailyTimeMinutes % 60)
        }
    }

    val task = viewModel.getTask()

    fun toTimeString(minutesLogged: Int): String {
        val hours = floor(minutesLogged / 60.0).toInt()
        val minutes = floor(minutesLogged % 60.0).toInt()

        if (hours > 0) {
            return "$hours hour${if (hours != 1) "s" else ""} ${if (minutes > 0) "and $minutes minute${if (minutes != 1) "s" else ""}" else ""}"
        }
        return "$minutes minute${if (minutes != 1) "s" else ""}"
    }

    fun taskColor(): Color {
        val a = task.rgbTaskColor.substring(6, 8)
        val rgb = task.rgbTaskColor.substring(0, 6)
        return Color(android.graphics.Color.parseColor("#$a$rgb"))
    }

    fun submit() {
        expanded = false
        viewModel.updateDailyLog()
        keyboardController?.hide()
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
                            hourString = it.trim()
                            if (hourString.isNotEmpty() && (hourString.toIntOrNull() ?: 0) >= 24) {
                                hourString = "24"
                                minuteString = "0"
                                viewModel.updateMinutes(0)
                            }
                            viewModel.updateHours(hourString.trim().toIntOrNull() ?: 0)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                keyboardController?.hide()
                            }
                            .onFocusChanged {
                                if (it.isFocused && hourString.isEmpty() || hourString == "0") {
                                    hourString = ""
                                }
                                if (!it.isFocused && hourString.isEmpty()) {
                                    hourString = "0"
                                }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester.requestFocus() }
                        ),
                        trailingIcon = {
                            Text(text = "h")
                        }
                    )
                    TextField(
                        value = minuteString,
                        onValueChange = {
                            minuteString = it.trim()
                            viewModel.updateMinutes(minuteString.trim().toIntOrNull() ?: 0)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused && minuteString.isEmpty() || minuteString == "0") {
                                    minuteString = ""
                                }
                                if (!it.isFocused && minuteString.isEmpty()) {
                                    minuteString = "0"
                                }
                                var minutesInt = minuteString
                                    .trim()
                                    .toIntOrNull() ?: 0
                                if (minutesInt > 60) {
                                    if (hourString.isEmpty()) {
                                        hourString = "0"
                                    }
                                    hourString = (hourString
                                        .trim()
                                        .toInt() + minutesInt / 60).toString()
                                    minutesInt %= 60
                                    minuteString = minutesInt.toString()
                                    viewModel.updateMinutes(minutesInt)
                                    viewModel.updateHours(
                                        hourString
                                            .trim()
                                            .toInt()
                                    )
                                }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                submit()
                            }
                        ),
                        trailingIcon = {
                            Text(text = "m")
                        }
                    )
                }
                Button(
                    onClick = {
                        submit()
                    },
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
            val call =
                RetrofitInstance.api.getLogForDate(formattedDate, task.id, AuthService.authHeaderString(authToken))
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
        val authString = AuthService.authHeaderString(authToken)
        val logCardTag = "log card for task with name: ${task.taskName}"
        val dailyTimeMinutes = hours * 60 + minutes
        val dateString = dateFormat(date)
        viewModelScope.launch {
            val call = when (currentLogData) {
                null -> RetrofitInstance.api.createLog(
                    dailyLog = DailyLog(
                        dailyTimeMinutes = dailyTimeMinutes,
                        taskId = task.id,
                        logDate = dateString,
                        collectedPoints = false
                    ),
                    authorizationString = authString
                )

                else -> RetrofitInstance.api.updateLogMinutes(
                    logDate = dateString,
                    authorizationString = authString,
                    updateLogMinutesRequest = UpdateLogMinutesRequest(
                        taskId = task.id,
                        dailyTimeMinutes = dailyTimeMinutes
                    )
                )
            }
            call.enqueue(object : Callback<ApiResponse<DailyLog>> {
                override fun onResponse(call: Call<ApiResponse<DailyLog>>, response: Response<ApiResponse<DailyLog>>) {
                    Log.i(
                        logCardTag,
                        "success: ${response.isSuccessful}, status code: ${response.code()} requestUrl: ${
                            response.raw().request().url()
                        }"
                    )
                    val responseBody = response.body()
                    if (responseBody == null) {
                        Log.e(logCardTag, "response body was null")
                        return
                    }
                    when (responseBody) {
                        is ApiResponse.Success -> {
                            Log.i(logCardTag, "updated entry for ${task.taskName}: ${responseBody.value}")
                            currentLogData = responseBody.value
                        }

                        is ApiResponse.Failure -> {
                            Log.e(logCardTag, "log card update failed with error: ${responseBody.error}")
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<DailyLog>>, throwable: Throwable) {
                    Log.e(logCardTag, "log card for task with name: ${task.taskName} failed to update")
                }

            })
        }
    }
}
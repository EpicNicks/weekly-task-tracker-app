package com.aspirant.weeklytasktrackerapp.model.entity.response

data class DailyLog(
    val logDate: String,
    val dailyTimeMinutes: Int,
    val collectedPoints: Boolean,
    val taskId: Int
)

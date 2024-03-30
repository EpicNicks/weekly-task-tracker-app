package com.aspirant.weeklytasktrackerapp.model.entity.request

data class UpdateLogMinutesRequest(
    val taskId: Int,
    val dailyTimeMinutes: Int
)

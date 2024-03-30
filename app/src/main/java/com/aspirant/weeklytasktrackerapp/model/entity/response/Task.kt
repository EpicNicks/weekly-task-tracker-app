package com.aspirant.weeklytasktrackerapp.model.entity.response

data class Task(
    val id: Int,
    val taskName: String,
    val weeklyTargetMinutes: Int,
    val rgbTaskColor: String,
    val isActive: Boolean,
    val userId: Int,
)

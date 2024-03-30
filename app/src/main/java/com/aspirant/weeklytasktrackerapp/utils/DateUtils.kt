package com.aspirant.weeklytasktrackerapp.utils

import java.time.LocalDate
import java.util.Date

fun dateFormat(date: LocalDate, separator: String = ""): String {
    return "${date.year}$separator${date.monthValue.toString().padStart(2, '0')}$separator${
        date.dayOfMonth.toString().padStart(2, '0')
    }"
}
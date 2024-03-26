package com.aspirant.weeklytasktrackerapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: TrackerApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://freetime.49385219.xyz/api")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackerApi::class.java)
    }
}
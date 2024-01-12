package com.example.weatherapp.app.core.usecases

import android.content.Context
import com.example.weatherapp.R
import java.util.Properties

fun getWeatherAPI(context: Context): String {
    val inputStream = context.resources.openRawResource(R.raw.api_keys)
    val properties = Properties()
    properties.load(inputStream)
    val apiKey = properties.getProperty("api_key")

    return apiKey
}
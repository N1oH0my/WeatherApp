package com.example.weatherapp.app.core.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun ParseDateString(dateString: String, language: String): String {
    val formatterIn = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val formatterOut = when (language.toLowerCase()) {
        "ru" -> DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
        "en" -> DateTimeFormatter.ofPattern("d MMMM", Locale("en"))
        else -> throw IllegalArgumentException("Unsupported language: $language")
    }

    val dateTime = LocalDateTime.parse(dateString, formatterIn)
    val formattedDate = dateTime.format(formatterOut)

    return formattedDate
}
package com.example.weatherapp.app.core.usecases

fun ParseTimeString(input: String): String {
    val regex = Regex("T(\\d{2}):(\\d{2})")
    val matchResult = regex.find(input)

    var time: String = "null"

    return matchResult?.let { result ->
        val hour = result.groupValues[1].toInt()
        val minute = result.groupValues[2].toInt()

        time = if (hour > 12) {
            val formattedHour = (hour - 12).toString().padStart(2, '0')
            "$formattedHour:${result.groupValues[2]} pm"
        } else {
            "$hour:${result.groupValues[2]} am"
        }

        time
    }?: time
}
package com.example.weatherapp.DataModels

data class WeatherDayItem(
    //val _location_key: String,

    val _city: String,
    val _date: String,

    val _max_temp: String,
    val _min_temp: String,

    val _sky_day: String,
    val _sky_night: String,

    val _sky_day_img_url: String,
    val _sky_night_img_url: String,

    val _air_quality: String,
    val _wind: String,

    val _sunrise: String,
    val _sunset: String,

)

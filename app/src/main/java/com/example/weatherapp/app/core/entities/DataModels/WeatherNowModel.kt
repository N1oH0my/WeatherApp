package com.example.weatherapp.app.core.entities.DataModels

data class WeatherNowModel(
    var _city: String = "null",
    var _date: String = "null",

    var _cur_temp: String = "null",

    var _cur_sky: String = "null",
    var _cur_sky_img_url: String = "null",

    var _air_quality: String = "null",
    var _wind: String = "null",
    var _wind_direction: String = "null",

    var _sunrise: String = "null",
    var _sunset: String = "null",

    var _alert: String = "null",
    var _mobile_link: String ="null",
    )

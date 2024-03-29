package com.example.weatherapp.app.presentation.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.app.core.entities.DataModels.WeatherDayItem
import com.example.weatherapp.app.core.entities.DataModels.WeatherHoursModel
import com.example.weatherapp.app.core.entities.DataModels.WeatherNowModel

class MainViewModel: ViewModel() {
    var mobile_link: String? = null

    var live_language = MutableLiveData<String>()

    var main_one = WeatherMainOneH()
    var main_two = WeatherMainTwoD()
    var live_data_main_one = MutableLiveData<WeatherMainOneH>()
    var live_data_main_two = MutableLiveData<WeatherMainTwoD>()

    var live_data_days = MutableLiveData<MutableList<WeatherDayItem>>()
    var live_data_hours = MutableLiveData<MutableList<WeatherHoursModel>>()
    data class WeatherMainOneH(

        var _cur_temp: String = "null",

        var _cur_sky: String = "null",
        var _cur_sky_img_url: String = "null",

    )
    data class WeatherMainTwoD(
        var _city: String = "null",
        var _date: String = "null",

        var _air_quality: String = "null",
        var _wind: String = "null",
        var _wind_direction: String = "null",

        var _sunrise: String = "null",
        var _sunset: String = "null",

        var _alert: String = "null",
        var _mobile_link: String ="null",
    )
}
package com.example.weatherapp.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.DataModels.WeatherDayItem
import com.example.weatherapp.DataModels.WeatherHoursModel

class MainViewModel: ViewModel() {
    var city: String? = null
    var live_data_main = MutableLiveData<WeatherDayItem>()

    var live_data_days = MutableLiveData<MutableList<WeatherDayItem>>()
    var live_data_hours = MutableLiveData<MutableList<WeatherHoursModel>>()
}
package com.example.weatherapp.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.DataModels.WeatherHoursModel

class MainViewModel: ViewModel() {
    val live_data_current = MutableLiveData<WeatherHoursModel>()
    val live_data_list = MutableLiveData<List<WeatherHoursModel>>()
}
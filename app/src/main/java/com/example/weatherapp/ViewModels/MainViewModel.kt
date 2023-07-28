package com.example.weatherapp.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.DataModels.WeatherHoursModel

class MainViewModel: ViewModel() {
    public var live_data_current = MutableLiveData<WeatherHoursModel>()
    var live_data_list = MutableLiveData<List<WeatherHoursModel>>()
}
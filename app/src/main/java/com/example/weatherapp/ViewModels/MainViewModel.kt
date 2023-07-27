package com.example.weatherapp.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val live_data_current = MutableLiveData<String>()
    val live_data_list = MutableLiveData<String>()
}
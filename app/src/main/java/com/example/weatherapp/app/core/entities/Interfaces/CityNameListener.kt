package com.example.weatherapp.app.core.entities.Interfaces

interface CityNameListener {
    fun onSuccess(cityName: String)
    fun onError(errorMessage: String)
}
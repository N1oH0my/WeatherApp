package com.example.weatherapp.app.framework.database

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun saveSelectedCity(city: String) {
        sharedPreferences.edit().putString("SelectedCity", city).apply()
    }

    fun getSelectedCity(): String? {
        return sharedPreferences.getString("SelectedCity", null)
    }

    fun saveSelectedLanguage(language: String) {
        sharedPreferences.edit().putString("SelectedLanguage", language).apply()
    }

    fun getSelectedLanguage(): String? {
        return sharedPreferences.getString("SelectedLanguage", null)
    }
}
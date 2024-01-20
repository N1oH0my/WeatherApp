package com.example.weatherapp.app.framework.network.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.weatherapp.app.core.entities.Interfaces.CityNameListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

val client = OkHttpClient()

fun getCityName(latitude: Double, longitude: Double, listener: CityNameListener) {
    val url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$latitude&lon=$longitude"
    val request = Request.Builder().url(url).build()
    client.newCall(request).enqueue(/* responseCallback = */ object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            listener.onError("Ошибка при выполнении запроса: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val jsonData = response.body()?.string()
            if (jsonData != null) {
                val jsonObject = JSONObject(jsonData)
                val cityName = jsonObject.getString("address")
                listener.onSuccess(cityName)
            }
        }
    })
}
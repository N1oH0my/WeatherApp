package com.example.weatherapp.app.framework.network.checkLocation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.example.weatherapp.app.presentation.view.Fragments.DialogManager

//------------------------DialogManager----------------------------------------
/*@RequiresApi(Build.VERSION_CODES.O)
private fun checkLocation(language: String = "en") {

    if (isLocationEnabled()) {
        getLocation(language)
    } else {
        //getLocation()
        DialogManager.LocationSettingsDialog(requireContext(), object : DialogManager.Listener {
            override fun onClick(city_name: String?) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

        })
    }
}
@SuppressLint("SuspiciousIndentation")
private fun isLocationEnabled(): Boolean {
    val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
}*/
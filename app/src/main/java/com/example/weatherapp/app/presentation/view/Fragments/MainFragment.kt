package com.example.weatherapp.app.presentation.view.Fragments

import com.example.weatherapp.app.framework.database.PreferenceHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.weatherapp.app.presentation.Adapters.ViewPageAdapter
import com.example.weatherapp.app.presentation.ViewModels.MainViewModel
import com.example.weatherapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import android.provider.Settings
import com.example.weatherapp.app.core.entities.Interfaces.CityNameListener
import com.example.weatherapp.app.framework.network.API.GetAllForecasts
import com.example.weatherapp.app.framework.network.location.getCityName
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Locale


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var p_launcher: ActivityResultLauncher<String>
    private lateinit var f_location_client: FusedLocationProviderClient

    private val cur_data: MainViewModel by activityViewModels()
    private lateinit var saved_info: PreferenceHelper

    private var f_list = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance(),
    )
    private val hd_title_list_en = listOf(
        "Hourly",
        "Daily",
    )
    private val hd_title_list_ru = listOf(
        "Часы",
        "Дни",
    )
    private val locationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    //-----------------------Start-----------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //---
        getActivity()?.getWindow()?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //---

        UpdateCurrentData()
        UpdateCurrentDataLanguage()
        Init()
        PermissionChecker()
        var  language = saved_info.getSelectedLanguage()
        var  city = saved_info.getSelectedCity()

        if (city != null) {
            saved_info.saveSelectedCity(city)
        }
        else
        {
            saved_info.saveSelectedCity("Voronezh")

        }
        if (language != null)
        {
            saved_info.saveSelectedLanguage(language)

            cur_data.live_language.value = language
        }
        else
        {
            saved_info.saveSelectedLanguage("ru")

            cur_data.live_language.value = "ru"
        }

    }
    //----------------------Init------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun Init() = with(binding)
    {

        saved_info = PreferenceHelper(requireContext())
        f_location_client = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)

        idViewPage.adapter = adapter

        TabLayoutMediator(idHDaysBtn, idViewPage)
        {
            tab, pos -> tab.text = hd_title_list_en[pos]
        }.attach()

        idSeeMoreDetails.setOnClickListener {
            var url = "https://www.accuweather.com"///ru/search-locations?query=${cur_data.city}"
            if (cur_data.main_two._mobile_link != "null")
            {
                url = cur_data.main_two._mobile_link!!
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        idSupport.setOnClickListener{
            val url = "https://t.me/niohomy"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        idLocationBtn.setOnClickListener {
            var language = saved_info.getSelectedLanguage()
            if (language!= null)
            {
                checkLocation(language)
            }
            else if (cur_data.live_language.value != null) {
                checkLocation( cur_data.live_language.value!!)
            }
            else{
                checkLocation("ru")
            }

        }
        idSearchBtn.setOnClickListener {
            DialogManager.FindByNameDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(city_name: String?) {
                    Log.d("MyLog", "$city_name")
                    var language = saved_info.getSelectedLanguage()
                    if (city_name != null) {
                        if (language != null) {
                            GetAllForecasts(requireContext(), saved_info, cur_data, city_name, language)
                        } else if (cur_data.live_language.value != null) {
                            GetAllForecasts(requireContext(), saved_info, cur_data, city_name, cur_data.live_language.value!!)
                        } else {
                            GetAllForecasts(requireContext(), saved_info, cur_data, city_name, "ru")
                        }
                    }
                }

            })
        }

        idSetLanguage.setOnClickListener {
            DialogManager.SetLanguageDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(language: String?) {
                    Log.d("MyLog", "$language")
                    var last_language = saved_info.getSelectedLanguage()
                    if (last_language != language) {
                        cur_data.live_language.value = language
                    }
                }

            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        //checkLocation()
    }
    //----------------------Permissions------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun PermissionListener() {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show()
                //getLocation(language)
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                // Обработка случая, когда разрешение на местоположение отклонено
            }
        }
        permissionLauncher.launch(locationPermission)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun PermissionChecker() {
        if (!hasLocationPermissions()) {
            PermissionListener()
        } else {
            //getLocation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasLocationPermissions(): Boolean {
        return locationPermission.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocation(language: String = "en") {
        if (!hasLocationPermissions()) {
            // Разрешения на местоположение не предоставлены
            return
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        f_location_client.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val listener = object : CityNameListener {
                        override fun onSuccess(cityName: String) {
                            val jsonObject = JSONObject(cityName)
                            val city = jsonObject.getString("city")

                            //Toast.makeText(requireContext(), "Your city is $city", Toast.LENGTH_SHORT).show()
                            GetAllForecasts(requireContext(), saved_info, cur_data, city, language)
                        }
                        override fun onError(errorMessage: String) {
                            //Toast.makeText(requireContext(), "Location is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    getCityName(location.latitude, location.longitude,  listener)

                } else {
                    Toast.makeText(requireContext(), "Location is null", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception: Exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to get location: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    //------------------------DialogManager----------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
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
    }
    //------------------------Update----------------------------------------
    private fun UpdateCurrentData() = with(binding)
    {
        cur_data.live_data_main_one.observe(viewLifecycleOwner){
            idCurTemp.text = it._cur_temp

            idCurSky.text = it._cur_sky
            Picasso.get().load(it._cur_sky_img_url).into(idCurSkyMainImg)
        }
        cur_data.live_data_main_two.observe(viewLifecycleOwner){
            idCurCityName.text = it._city
            idCurDate.text = it._date

            idCurAir.text = it._air_quality
            idCurWind.text = it._wind + " " + it._wind_direction
            idCurSunrise.text = it._sunrise
            idCurSunset.text = it._sunset
            idAlert.text = it._alert
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun UpdateCurrentDataLanguage() = with(binding)
    {
        cur_data.live_language.observe(viewLifecycleOwner){
            val languageCode:String = cur_data.live_language.value?: "null"
            //cur_data.live_language.value?.let { it1 -> checkLocation(it1) }
            if (languageCode!="null")
            {
                saved_info.saveSelectedLanguage(languageCode)
                when (languageCode) {
                    "en" -> {

                            idSupport.text = "Support"
                            idSeeMoreDetails.text = "See more details"
                            idTextAir.text = "Air:"
                            idTextWind.text = "Wind:"
                            idTextSunrise.text = "Sunrise:"
                            idTextSunset.text = "Sunset:"
                            idBy.text = "by N1oH0my"
                            idSetLanguage.text = "Language"
                            val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)
                            idViewPage.adapter = adapter
                            TabLayoutMediator(idHDaysBtn, idViewPage)
                            {
                                    tab, pos -> tab.text = hd_title_list_en[pos]
                            }.attach()

                            val locale = Locale("en")
                            Locale.setDefault(locale)
                            val resources = resources
                            val configuration = resources.configuration
                            configuration.locale = locale
                            resources.updateConfiguration(configuration, resources.displayMetrics)

                            val last_city = saved_info.getSelectedCity()
                            if (last_city != null)
                            {
                                GetAllForecasts(requireContext(), saved_info, cur_data, last_city, languageCode)
                            }
                            else
                            {
                                checkLocation(languageCode)
                            }
                    }
                    "ru" -> {

                            idSupport.text = "Поддержка"
                            idSetLanguage.text = "Язык"
                            idSeeMoreDetails.text = "Посмотреть подробнее"
                            idTextAir.text = "Категория воздуха"
                            idTextWind.text = "Ветер"
                            idTextSunrise.text = "Восход"
                            idTextSunset.text = "Закат"
                            idBy.text = "создано N1oH0my"

                            val locale = Locale("ru")
                            Locale.setDefault(locale)
                            val resources = resources
                            val configuration = resources.configuration
                            configuration.locale = locale
                            resources.updateConfiguration(configuration, resources.displayMetrics)

                            val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)
                            idViewPage.adapter = adapter
                            TabLayoutMediator(idHDaysBtn, idViewPage)
                            {
                                    tab, pos -> tab.text = hd_title_list_ru[pos]
                            }.attach()
                            val last_city = saved_info.getSelectedCity()
                            if (last_city != null)
                            {
                                GetAllForecasts(requireContext(), saved_info, cur_data, last_city, languageCode)
                            }
                            else
                            {
                                checkLocation(languageCode)
                            }

                    }
                    else -> {

                    }
                }
            }

        }
    }
    //----------------------------------------------------------------
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}


package com.example.weatherapp.Fragments

import PreferenceHelper
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Adapters.ViewPageAdapter
import com.example.weatherapp.DataModels.WeatherDayItem
import com.example.weatherapp.DataModels.WeatherHoursModel
import com.example.weatherapp.ViewModels.MainViewModel
import com.example.weatherapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.provider.Settings
import com.example.weatherapp.DataModels.WeatherNowModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import java.util.Locale


class MainFragment : Fragment() {

    private val WEATHER_API_KEY: String = "ZhvnfeXeICWsbRy1Xy0hxUN2ajAtfLnV"
    //private val WEATHER_API_KEY: String = "FPv7HgXJ8uHDgICrYKtJmwyj67bsf00G"
    private lateinit var binding: FragmentMainBinding
    private lateinit var p_launcher: ActivityResultLauncher<String>
    private lateinit var f_location_client: FusedLocationProviderClient

    private val cur_data: MainViewModel by activityViewModels()
    private lateinit var saved_info:PreferenceHelper

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
        // Inflate the layout for this fragment
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
            PermissionChecker(language)
            cur_data.live_language.value = language
        }
        else
        {
            saved_info.saveSelectedLanguage("ru")
            PermissionChecker("ru")
            cur_data.live_language.value = "ru"
        }
        //getLocation("ru")



        /**/
        //val cityName = "Voronezh"
        //GetAllForecasts(cityName)
        //DailyRequestWeather(requireContext(),"296543", "Voronezh")
        //HourlyRequestWeather(requireContext(),"296543")

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
            DialogManager.FindByNameDialog(requireContext(), object: DialogManager.Listener{
                override fun onClick(city_name: String?) {
                    Log.d("MyLog", "$city_name")
                    var language = saved_info.getSelectedLanguage()
                    if (city_name != null) {
                        if (language!= null)
                        {
                            GetAllForecasts(city_name, language)
                        }
                        else if (cur_data.live_language.value != null) {
                            GetAllForecasts(city_name, cur_data.live_language.value!!)
                        }
                        else{
                            GetAllForecasts(city_name, "ru")
                        }
                    }
                }

            })
        }

        idSetLanguage.setOnClickListener {
            DialogManager.SetLanguageDialog(requireContext(), object: DialogManager.Listener{
                override fun onClick(language: String?) {
                    Log.d("MyLog", "$language")
                    var last_language = saved_info.getSelectedLanguage()
                    if (last_language != language)
                    {
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
    private fun PermissionListener(language: String = "en") {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show()
                getLocation(language)
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                // Обработка случая, когда разрешение на местоположение отклонено
            }
        }
        permissionLauncher.launch(locationPermission)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun PermissionChecker(language: String = "en") {
        if (!hasLocationPermissions()) {
            PermissionListener(language)
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
                    val cityName = getCityName(location.latitude, location.longitude)
                    Toast.makeText(requireContext(), "Your city is $cityName", Toast.LENGTH_SHORT).show()
                    GetAllForecasts(cityName, language)
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

    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext())
        val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        if (addressList?.isNotEmpty() == true) {
            return addressList[0].locality ?: "000"
        }

        return "000"
    }


    //---------------------------API-------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun GetAllForecasts(cur_city_name: String, language: String = "en")
    {
        saved_info.saveSelectedCity(cur_city_name)

        FindLocationKey(requireContext(), cur_city_name) { locationKey ->
            if (locationKey != null) {
                //Toast.makeText(activity, "Found $cur_city_name", Toast.LENGTH_SHORT).show()

                DailyRequestWeather(requireContext(),locationKey, cur_city_name, language)
                HourlyRequestWeather(requireContext(),locationKey, language)

                Log.d("ML", "My Item MAIN1\n${cur_data.main_one._cur_sky} \n ${cur_data.main_one._cur_temp} \n ${cur_data.main_one._cur_sky_img_url}")
                Log.d("ML", "MAIN2\n${cur_data.main_two._city} \n${cur_data.main_two._date}" +
                        "\n ${cur_data.main_two._wind} \n ${cur_data.main_two._wind_direction}"+
                        "\n ${cur_data.main_two._air_quality} \n "+
                        "\n ${cur_data.main_two._sunrise} \n ${cur_data.main_two._sunset}")


            } else {
                // Ключ расположения не найден или произошла ошибка,
                var cityName = "Voronezh"
                if (language == "ru")
                    cityName = "Воронеж"
                GetAllForecasts(cityName, language)
                Toast.makeText(activity, "Sry,\n weather for ur city was not found,\n enter the nearest city", Toast.LENGTH_SHORT).show()

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun DailyRequestWeather(context: Context, locationKey: String, city_name: String, language: String = "en") {
        val queue = Volley.newRequestQueue(context)
        //var language: String = "ru"
        val url =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/" +
                    "$locationKey?" +
                    "apikey=$WEATHER_API_KEY&" +
                    "language=$language&details=true&metric=true"

        val jsonArrayRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                //val headForecast = response.getJSONArray("Headline")
                //cur_data.main_item._alert = headForecast.getJSONObject(0).getString("Text")?:" "
                //cur_data.main_item._mobile_link = headForecast.getJSONObject(0).getString("MobileLink")?:" "
                val headForecast = response.getJSONObject("Headline")
                cur_data.main_two._alert = headForecast.getString("Text")?:" "
                cur_data.main_two._mobile_link = headForecast.getString("MobileLink")?:" "

                val check_alert = cur_data.main_two._alert
                val check_link = cur_data.main_two._mobile_link
                Log.d("ML", "CHeck $check_alert\n$check_link")
                val dailyForecastsArray = response.getJSONArray("DailyForecasts")
                val jsonObjectList = mutableListOf<JSONObject>()
                for (i in 0 until 5) {
                    val jsonObject = dailyForecastsArray.getJSONObject(i)
                    jsonObjectList.add(jsonObject)
                }
                Log.d("MyLog", "weather days ok\n")

                ParseDailyDataFromJsonObjects(jsonObjectList, city_name, language)
            },
            Response.ErrorListener { error ->
                Log.d("MyLog", "weather error")
            })

        queue.add(jsonArrayRequest)
    }
    private fun HourlyRequestWeather(context: Context, locationKey: String, language: String = "en") {
        val queue = Volley.newRequestQueue(context)
        //var language: String = "ru"
        val url =
            "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/" +
                    "$locationKey?" +
                    "apikey=$WEATHER_API_KEY&" +
                    "language=$language&details=true&metric=true"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                val jsonObjectList = mutableListOf<JSONObject>()
                for (i in 0 until 12) {
                    val jsonObject = response.getJSONObject(i)
                    jsonObjectList.add(jsonObject)
                }
                Log.d("MyLog", "weather hours ok\n")

                ParseHourlyDataFromJsonObjects(jsonObjectList)
            },
            Response.ErrorListener { error ->
                Log.d("MyLog", "weather error")
            })

        queue.add(jsonArrayRequest)
    }
    private fun FindLocationKey(context: Context, city: String,language: String = "en", callback: (String?) -> Unit) {
        /*val url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=$WEATHER_API_KEY&q=$city&language=en&details=false&offset=1"

        val requestQueue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val locationKey = response.getJSONObject(0).getString("Key")
                Log.d("MyLog", "location key: $locationKey \n")
                callback(locationKey)
            },
            Response.ErrorListener { error ->
                Log.d("MyLog", "lc error")
                callback(null)
            })

        requestQueue.add(jsonArrayRequest)
        */
        //var language: String = "en"
        val url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" +
                "$WEATHER_API_KEY&q=" +
                "$city" +
                "&language=$language&details=false&offset=1"

        val requestQueue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    if (response.length() > 0) {
                        val locationKey = response.getJSONObject(0).getString("Key")
                        Log.d("MyLog", "location key: $locationKey \n")
                        callback(locationKey)
                    } else {
                        Log.d("MyLog", "Response is empty")
                        callback(null)
                    }
                } catch (e: JSONException) {
                    Log.e("MyLog", "Error parsing JSON response: ${e.message}")
                    callback(null)
                }
            },
            Response.ErrorListener { error ->
                Log.e("MyLog", "Volley request error: ${error.message}")
                callback(null)
            })

        requestQueue.add(jsonArrayRequest)
    }

    fun ParseHourlyDataFromJsonObjects(jsonObjects: List<JSONObject>) {
        var item_list: MutableList<WeatherHoursModel> = mutableListOf()
        for (jsonObject in jsonObjects) {
            var date_time:String = jsonObject.getString("DateTime") ?: "null"
            var icon_code:String = jsonObject.getString("WeatherIcon")?: "null"
            if (icon_code.length == 1)
            {
                icon_code = "0"+icon_code
            }
            val item = WeatherHoursModel(
                _sky = jsonObject.getString("IconPhrase"),
                _sky_img = "https://developer.accuweather.com/sites/default/files/"+
                        icon_code +
                "-s.png",
                _temp = jsonObject.getJSONObject("Temperature").getString("Value") +" °C",
                _hour = ParseTimeString(date_time),
            )
            item_list.add(item)

            Log.d("MyLog", "My H Item\n${item._sky} \n ${item._temp} \n ${item._hour}")
        }
        cur_data.main_one._cur_temp = item_list[0]._temp
        cur_data.main_one._cur_sky = item_list[0]._sky
        cur_data.main_one._cur_sky_img_url = item_list[0]._sky_img
        Log.d("ML", "My H Item MAIN\n${cur_data.main_one._cur_sky} \n ${cur_data.main_one._cur_temp} \n ${cur_data.main_one._cur_sky_img_url}")

        cur_data.live_data_hours.value =(item_list)
        cur_data.live_data_main_one.value = cur_data.main_one
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun ParseDailyDataFromJsonObjects(jsonObjects: List<JSONObject>, city_name: String, language: String = "en") {
        var item_list: MutableList<WeatherDayItem> = mutableListOf()
        for (jsonObject in jsonObjects) {

            var date_time:String = jsonObject.getString("Date") ?: "null"
            var sunrise_time:String = jsonObject.getJSONObject("Sun").getString("Rise")?: "null"
            var sunset_time:String = jsonObject.getJSONObject("Sun").getString("Set")?: "null"
            val air_array = jsonObject.getJSONArray("AirAndPollen")

            var icon_day_code = jsonObject.getJSONObject("Day").getString("Icon")?:"null"
            var icon_night_code = jsonObject.getJSONObject("Night").getString("Icon")?:"null"
            if (icon_day_code.length == 1)
                icon_day_code = "0"+icon_day_code
            if (icon_night_code.length == 1)
                icon_night_code = "0"+icon_night_code

            val item = WeatherDayItem(
                _city = city_name,
                _date = ParseDateString(date_time, language),
                _max_temp = jsonObject.getJSONObject("Temperature")
                    .getJSONObject("Maximum").getString("Value")+" °C",
                _min_temp = jsonObject.getJSONObject("Temperature")
                    .getJSONObject("Minimum").getString("Value")+" °C",
                _sky_day = jsonObject.getJSONObject("Day").getString("IconPhrase"),
                _sky_night = jsonObject.getJSONObject("Night").getString("IconPhrase"),

                _sky_day_img_url = "https://developer.accuweather.com/sites/default/files/"+
                        icon_day_code +
                        "-s.png",
                _sky_night_img_url = "https://developer.accuweather.com/sites/default/files/"+
                        icon_night_code +
                        "-s.png",

                _air_quality = air_array.getJSONObject(0).getString("Category"),
                _wind = jsonObject.getJSONObject("Day").getJSONObject("Wind")
                    .getJSONObject("Speed").getString("Value") +
                        jsonObject.getJSONObject("Day").getJSONObject("Wind")
                            .getJSONObject("Speed").getString("Unit"),
                _wind_direction = jsonObject.getJSONObject("Day").getJSONObject("Wind")
                    .getJSONObject("Direction").getString("Localized"),
                _sunrise = ParseTimeString(sunrise_time),
                _sunset = ParseTimeString(sunset_time),
            )
            item_list.add(item)
            Log.d("MyLog", "My Item\n${item._city} \n${item._date}" +
                    "\n ${item._max_temp} \n ${item._min_temp}"+
                    "\n ${item._sky_day} \n ${item._sky_night}"+
                    "\n ${item._air_quality} \n ${item._wind}"+
                    "\n ${item._sunrise} \n ${item._sunset}")
        }
        //cur_data.live_data_days.postValue(item_list)
        cur_data.main_two._city = item_list[0]._city
        cur_data.main_two._date = item_list[0]._date

        cur_data.main_two._wind = item_list[0]._wind
        cur_data.main_two._wind_direction = item_list[0]._wind_direction
        cur_data.main_two._air_quality = item_list[0]._air_quality

        cur_data.main_two._sunrise = item_list[0]._sunrise
        cur_data.main_two._sunset = item_list[0]._sunset
        Log.d("ML", "My D Item MAIN\n${cur_data.main_two._city} \n${cur_data.main_two._date}" +
                "\n ${cur_data.main_two._wind} \n ${cur_data.main_two._wind_direction}"+
                "\n ${cur_data.main_two._air_quality} \n "+
                "\n ${cur_data.main_two._sunrise} \n ${cur_data.main_two._sunset}")

        cur_data.live_data_days.value = (item_list)
        cur_data.live_data_main_two.value = cur_data.main_two
    }
    private fun ParseTimeString(input: String): String {
        val regex = Regex("T(\\d{2}):(\\d{2})")
        val matchResult = regex.find(input)

        var time: String = "null"

        return matchResult?.let { result ->
            val hour = result.groupValues[1].toInt()
            val minute = result.groupValues[2].toInt()

            time = if (hour > 12) {
                val formattedHour = (hour - 12).toString().padStart(2, '0')
                "$formattedHour:${result.groupValues[2]} pm"
            } else {
                "$hour:${result.groupValues[2]} am"
            }

            time
        }?: time
    }
    @RequiresApi(Build.VERSION_CODES.O)
    /*fun ParseDateString(dateString: String): String {
        val formatterIn = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val formatterOut = DateTimeFormatter.ofPattern("d MMMM")

        val dateTime = LocalDateTime.parse(dateString, formatterIn)
        val formattedDate = dateTime.format(formatterOut)

        return formattedDate
    }*/
    fun ParseDateString(dateString: String, language: String): String {
        val formatterIn = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val formatterOut = when (language.toLowerCase()) {
            "ru" -> DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
            "en" -> DateTimeFormatter.ofPattern("d MMMM", Locale("en"))
            else -> throw IllegalArgumentException("Unsupported language: $language")
        }

        val dateTime = LocalDateTime.parse(dateString, formatterIn)
        val formattedDate = dateTime.format(formatterOut)

        return formattedDate
    }
    //------------------------DialogManager----------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLocation(language: String = "en") {

        if (isLocationEnabled()) {
            getLocation(language)
        } else {
            //getLocation()
            DialogManager.LocationSettingsDialog(requireContext (), object : DialogManager.Listener{
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
                        if (idSupport.text != "Supportt")
                        {
                            val locale = Locale("en")
                            Locale.setDefault(locale)
                            val resources = resources
                            val configuration = resources.configuration
                            configuration.locale = locale
                            resources.updateConfiguration(configuration, resources.displayMetrics)

                            idSupport.text = "Support"
                            idSeeMoreDetails.text = "See more details"
                            idTextAir.text = "Air:"
                            idTextWind.text = "Wind:"
                            idTextSunrise.text = "Sunrise:"
                            idTextSunset.text = "Sunset:"
                            idBy.text = "by N1oH0my"

                            val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)
                            idViewPage.adapter = adapter
                            TabLayoutMediator(idHDaysBtn, idViewPage)
                            {
                                    tab, pos -> tab.text = hd_title_list_en[pos]
                            }.attach()

                            val last_city = saved_info.getSelectedCity()
                            if (last_city != null)
                            {
                                GetAllForecasts(last_city, languageCode)
                            }
                            else
                            {
                                checkLocation(languageCode)
                            }
                        }
                    }
                    "ru" -> {
                        if (idSupport.text != "Поддержка")
                        {

                            val locale = Locale("ru")
                            Locale.setDefault(locale)
                            val resources = resources
                            val configuration = resources.configuration
                            configuration.locale = locale
                            resources.updateConfiguration(configuration, resources.displayMetrics)

                            idSupport.text = "Поддержка"
                            idSetLanguage.text = "Язык"
                            idSeeMoreDetails.text = "Посмотреть подробнее"
                            idTextAir.text = "Категория воздуха:"
                            idTextWind.text = "Ветер:"
                            idTextSunrise.text = "Восход:"
                            idTextSunset.text = "Закат:"
                            idBy.text = "создано N1oH0my"

                            val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)
                            idViewPage.adapter = adapter
                            TabLayoutMediator(idHDaysBtn, idViewPage)
                            {
                                    tab, pos -> tab.text = hd_title_list_ru[pos]
                            }.attach()
                            val last_city = saved_info.getSelectedCity()
                            if (last_city != null)
                            {
                                GetAllForecasts(last_city, languageCode)
                            }
                            else
                            {
                                checkLocation(languageCode)
                            }
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
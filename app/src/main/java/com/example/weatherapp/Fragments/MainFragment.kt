package com.example.weatherapp.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
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


class MainFragment : Fragment() {

    private val WEATHER_API_KEY: String = "ZhvnfeXeICWsbRy1Xy0hxUN2ajAtfLnV"
    private lateinit var binding: FragmentMainBinding
    private lateinit var p_launcher: ActivityResultLauncher<String>
    private lateinit var f_location_client: FusedLocationProviderClient

    private val cur_data: MainViewModel by activityViewModels()

    private var f_list = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance(),
    )
    private val hd_title_list = listOf(
        "Hourly",
        "Daily",
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
        Init()
        PermissionChecker()
        //GetLocation()


        UpdateCurrentData()
        /**/
        //val cityName = "Paris"
        //GetAllForecasts(cityName)

        //HourlyRequestWeather(requireContext(),"296543")
        //DailyRequestWeather(requireContext(),"296543", "Voronezh")
    }
    //----------------------Init------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun Init() = with(binding)
    {
        f_location_client = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)

        idViewPage.adapter = adapter

        TabLayoutMediator(idHDaysBtn, idViewPage)
        {
            tab, pos -> tab.text = hd_title_list[pos]
        }.attach()

        idLocationBtn.setOnClickListener {
            getLocation()
        }
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
                getLocation()
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
            getLocation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasLocationPermissions(): Boolean {
        return locationPermission.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocation() {
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
                    GetAllForecasts(cityName)
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
            return addressList[0].locality ?: "null"
        }

        return "null"
    }


    //---------------------------API-------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun GetAllForecasts(cur_city_name: String)
    {
        FindLocationKey(requireContext(), cur_city_name) { locationKey ->
            if (locationKey != null) {
                Toast.makeText(activity, "Found u", Toast.LENGTH_SHORT).show()

                DailyRequestWeather(requireContext(),locationKey, cur_city_name)
                HourlyRequestWeather(requireContext(),locationKey)

            } else {
                // Ключ расположения не найден или произошла ошибка,
                Toast.makeText(activity, "Sry,\n weather for ur city was not found,\n enter the nearest city", Toast.LENGTH_LONG).show()

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun DailyRequestWeather(context: Context, locationKey: String, city_name: String) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/" +
                    "$locationKey?" +
                    "apikey=$WEATHER_API_KEY&" +
                    "language=en&details=true&metric=true"

        val jsonArrayRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                //val mainJsonObject = response.getJSONArray("")
                val dailyForecastsArray = response.getJSONArray("DailyForecasts")
                val jsonObjectList = mutableListOf<JSONObject>()
                for (i in 0 until 5) {
                    val jsonObject = dailyForecastsArray.getJSONObject(i)
                    jsonObjectList.add(jsonObject)
                }
                Log.d("MyLog", "weather ok\n")

                ParseDailyDataFromJsonObjects(jsonObjectList, city_name)
            },
            Response.ErrorListener { error ->
                Log.d("MyLog", "weather error")
            })

        queue.add(jsonArrayRequest)
    }
    private fun HourlyRequestWeather(context: Context, locationKey: String) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/" +
                    "$locationKey?" +
                    "apikey=$WEATHER_API_KEY&" +
                    "language=en&details=true&metric=true"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                val jsonObjectList = mutableListOf<JSONObject>()
                for (i in 0 until 12) {
                    val jsonObject = response.getJSONObject(i)
                    jsonObjectList.add(jsonObject)
                }
                Log.d("MyLog", "weather ok\n")

                ParseHourlyDataFromJsonObjects(jsonObjectList)
            },
            Response.ErrorListener { error ->
                Log.d("MyLog", "weather error")
            })

        queue.add(jsonArrayRequest)
    }
    private fun FindLocationKey(context: Context, city: String, callback: (String?) -> Unit) {
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

        val url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=$WEATHER_API_KEY&q=$city&language=en&details=false&offset=1"

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

            Log.d("MyLog", "My Item\n${item._sky} \n ${item._temp} \n ${item._hour}")
        }
        cur_data.live_data_hours.value =(item_list)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun ParseDailyDataFromJsonObjects(jsonObjects: List<JSONObject>, city_name: String) {
        var item_list: MutableList<WeatherDayItem> = mutableListOf()
        for (jsonObject in jsonObjects) {

            var date_time:String = jsonObject.getString("Date") ?: "null"
            var sunrise_time:String = jsonObject.getJSONObject("Sun").getString("Rise")
            var sunset_time:String = jsonObject.getJSONObject("Sun").getString("Set")
            val air_array = jsonObject.getJSONArray("AirAndPollen")

            var icon_day_code = jsonObject.getJSONObject("Day").getString("Icon")?:"null"
            var icon_night_code = jsonObject.getJSONObject("Night").getString("Icon")?:"null"
            if (icon_day_code.length == 1)
                icon_day_code = "0"+icon_day_code
            if (icon_night_code.length == 1)
                icon_night_code = "0"+icon_night_code

            val item = WeatherDayItem(
                _city = city_name,
                _date = ParseDateString(date_time),
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
        cur_data.live_data_days.value = (item_list)
        cur_data.live_data_main.value = item_list.get(0)
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
    fun ParseDateString(dateString: String): String {
        val formatterIn = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val formatterOut = DateTimeFormatter.ofPattern("d MMMM")

        val dateTime = LocalDateTime.parse(dateString, formatterIn)
        val formattedDate = dateTime.format(formatterOut)

        return formattedDate
    }
    //------------------------Update----------------------------------------
    private fun UpdateCurrentData() = with(binding)
    {
        cur_data.live_data_main.observe(viewLifecycleOwner){
            idCurCityName.text = it._city
            idCurDate.text = it._date

            idCurMax.text = it._max_temp
            idCurMin.text = it._min_temp

            idCurDaySky.text = it._sky_day
            idCurNightSky.text = it._sky_night

            idCurAir.text = it._air_quality
            idCurWind.text = it._wind
            idCurSunrise.text = it._sunrise
            idCurSunset.text = it._sunset
        }
    }
    //----------------------------------------------------------------
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
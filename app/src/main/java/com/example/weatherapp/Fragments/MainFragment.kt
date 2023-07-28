package com.example.weatherapp.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Adapters.ViewPageAdapter
import com.example.weatherapp.DataModels.WeatherHoursModel
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.Fragments.HoursFragment
import com.example.weatherapp.Fragments.DaysFragment
import com.example.weatherapp.ViewModels.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject



class MainFragment : Fragment() {

    private val WEATHER_API_KEY: String = "ZhvnfeXeICWsbRy1Xy0hxUN2ajAtfLnV"
    private lateinit var binding: FragmentMainBinding
    private lateinit var p_launcher: ActivityResultLauncher<String>

    private var f_list = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance(),
    )
    private val hd_title_list = listOf(
        "Hourly",
        "Daily",
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //---
        getActivity()?.getWindow()?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //---
        PermissionChecker()
        Init()

        /*
        val cityName = "Paris"
        FindLocationKey(requireContext(), cityName) { locationKey ->
            if (locationKey != null) {
                // Ключ расположения найден, выполняйте необходимые действия
                Toast.makeText(activity, "Found u", Toast.LENGTH_SHORT).show()
                RequestWeather(requireContext(),locationKey)
            } else {
                // Ключ расположения не найден или произошла ошибка,
                Toast.makeText(activity, "Location not found :(", Toast.LENGTH_SHORT).show()
            }
        }
        */
        RequestWeather(requireContext(),"296543")
    }
    //----------------------Init------------------------------------------
    private fun Init() = with(binding)
    {
        val adapter = ViewPageAdapter(activity as FragmentActivity, f_list)

        idViewPage.adapter = adapter

        TabLayoutMediator(idHDaysBtn, idViewPage)
        {
            tab, pos -> tab.text = hd_title_list[pos]
        }.attach()
    }
    //----------------------Permissions------------------------------------------
    private fun PermissionListner()
    {
        p_launcher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
           Toast.makeText(activity, "permission is $it", Toast.LENGTH_SHORT).show()
        }
    }
    private fun PermissionChecker() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionListner()
            p_launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено
            } else {
                // Разрешение не предоставлено
            }
        }
    }*/
    //---------------------------API-------------------------------------
    private fun RequestWeather(context: Context, locationKey: String) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/$locationKey?apikey=$WEATHER_API_KEY&language=en&details=true&metric=true"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                val jsonObjectList = mutableListOf<JSONObject>()
                for (i in 0 until 12) {
                    val jsonObject = response.getJSONObject(i)
                    jsonObjectList.add(jsonObject)
                }
                Log.d("MyLog", "weather ok\n")

                parseDataFromJsonObjects(jsonObjectList)
            },
            Response.ErrorListener { error ->
                Log.d("MyLog", "weather error")
            })

        queue.add(jsonArrayRequest)
    }
    private fun FindLocationKey(context: Context, city: String, callback: (String?) -> Unit) {
        val url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=$WEATHER_API_KEY&q=$city&language=en&details=false&offset=1"

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
    }

    fun parseDataFromJsonObjects(jsonObjects: List<JSONObject>) {
        for (jsonObject in jsonObjects) {
            var date_time:String = jsonObject.getString("DateTime") ?: "null"
            val item = WeatherHoursModel(
                _sky = jsonObject.getString("IconPhrase"),
                _sky_img = "cloudy.png",
                _temp = jsonObject.getJSONObject("Temperature").getString("Value"),
                _hour = parseTimeString(date_time),
            )
            Log.d("MyLog", "My Item\n${item._sky} \n ${item._temp} \n ${item._hour}")
        }
    }
    private fun parseTimeString(input: String): String {
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
    //----------------------------------------------------------------
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
package com.example.weatherapp.app.framework.network.API

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.app.core.entities.DataModels.WeatherDayItem
import com.example.weatherapp.app.core.entities.DataModels.WeatherHoursModel
import com.example.weatherapp.app.core.usecases.ParseDateString
import com.example.weatherapp.app.core.usecases.ParseTimeString
import com.example.weatherapp.app.core.usecases.getWeatherAPI
import com.example.weatherapp.app.framework.database.PreferenceHelper
import com.example.weatherapp.app.presentation.ViewModels.MainViewModel
import org.json.JSONException
import org.json.JSONObject

//---------------------------API-------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
fun GetAllForecasts(context: Context, saved_info: PreferenceHelper, cur_data: MainViewModel, cur_city_name: String, language: String = "en")
{
    /**/


    FindLocationKey(context, cur_city_name) { locationKey ->
        if (locationKey != null && locationKey != "server_error") {
            //Toast.makeText(activity, "Found $cur_city_name", Toast.LENGTH_SHORT).show()

            DailyRequestWeather(context, cur_data, locationKey, cur_city_name, language)
            HourlyRequestWeather(context, cur_data, locationKey, language)

            Log.d("ML", "My Item MAIN1\n${cur_data.main_one._cur_sky} \n ${cur_data.main_one._cur_temp} \n ${cur_data.main_one._cur_sky_img_url}")
            Log.d("ML", "MAIN2\n${cur_data.main_two._city} \n${cur_data.main_two._date}" +
                    "\n ${cur_data.main_two._wind} \n ${cur_data.main_two._wind_direction}"+
                    "\n ${cur_data.main_two._air_quality} \n "+
                    "\n ${cur_data.main_two._sunrise} \n ${cur_data.main_two._sunset}")

            saved_info.saveSelectedCity(cur_city_name)

        } else if(locationKey != "server_error") {
            // Ключ расположения не найден или произошла ошибка,
            /*var cityName = "Voronezh"
            if (language == "ru")
                cityName = "Воронеж"
            GetAllForecasts(context, saved_info, cur_data, cityName, language)*/
            //Toast.makeText(activity, "Sry,\n weather for ur city was not found", Toast.LENGTH_SHORT).show()

        }
        else{
            //Toast.makeText(activity, "Sry,\nserver is temporarily unavailable or the tokens have run out", Toast.LENGTH_LONG).show()
        }
    }

}
@RequiresApi(Build.VERSION_CODES.O)
fun DailyRequestWeather(context: Context, cur_data: MainViewModel, locationKey: String, city_name: String, language: String = "en") {
    val queue = Volley.newRequestQueue(context)
    //var language: String = "ru"
    val url =
        "http://dataservice.accuweather.com/forecasts/v1/daily/5day/" +
                "$locationKey?" +
                "apikey=${getWeatherAPI(context)}&" +
                "language=$language&details=true&metric=true"

    val jsonArrayRequest = JsonObjectRequest(
        Request.Method.GET, url, null,
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

            ParseDailyDataFromJsonObjects(jsonObjectList,cur_data, city_name, language)
        },
        Response.ErrorListener { error ->
            Log.d("MyLog", "weather error")
        })

    queue.add(jsonArrayRequest)
}
fun HourlyRequestWeather(context: Context, cur_data: MainViewModel, locationKey: String, language: String = "en") {
    val queue = Volley.newRequestQueue(context)
    //var language: String = "ru"
    val url =
        "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/" +
                "$locationKey?" +
                "apikey=${getWeatherAPI(context)}&" +
                "language=$language&details=true&metric=true"

    val jsonArrayRequest = JsonArrayRequest(
        Request.Method.GET, url, null,
        Response.Listener { response ->

            val jsonObjectList = mutableListOf<JSONObject>()
            for (i in 0 until 12) {
                val jsonObject = response.getJSONObject(i)
                jsonObjectList.add(jsonObject)
            }
            Log.d("MyLog", "weather hours ok\n")

            ParseHourlyDataFromJsonObjects(jsonObjectList, cur_data)
        },
        Response.ErrorListener { error ->
            Log.d("MyLog", "weather error")
        })

    queue.add(jsonArrayRequest)
}
fun FindLocationKey(context: Context, city: String, language: String = "en", callback: (String?) -> Unit) {
    val url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" +
            "${getWeatherAPI(context)}&q=" +
            "$city" +
            "&language=$language&details=false&offset=1"

    val requestQueue = Volley.newRequestQueue(context)
    val jsonArrayRequest = JsonArrayRequest(
        Request.Method.GET, url, null,
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
            Log.e("MyLog", "Volley request error lc: ${error.message}")
            callback("server_error")
        })

    requestQueue.add(jsonArrayRequest)
}

fun ParseHourlyDataFromJsonObjects(jsonObjects: List<JSONObject>, cur_data: MainViewModel) {
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
fun ParseDailyDataFromJsonObjects(jsonObjects: List<JSONObject>, cur_data: MainViewModel, city_name: String, language: String = "en") {
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

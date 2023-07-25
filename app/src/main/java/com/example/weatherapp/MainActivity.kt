package com.example.weatherapp

import android.app.DownloadManager.Request
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject

const val API_WEATHER_KEY = "QUlAOSqDHT8XOBwxntWFsw==I0QtGYNHsCvFQZqw"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idButton.setOnClickListener{
            GetResult("London")
        }

    }

    private fun GetResult(town: String)
    {
        val url = "https://api.api-ninjas.com/v1/weather?city=$town"

        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                // Обработка успешного ответа от сервера

                Log.d("MyLog","Response : ${response.getString("temp")}")


            },
            Response.ErrorListener { error ->
                // Обработка ошибки запроса
                Log.d("MyLog","Volley error")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-Api-Key"] = API_WEATHER_KEY
                return headers
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}
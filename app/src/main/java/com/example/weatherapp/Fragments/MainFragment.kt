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
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.Fragments.HoursFragment
import com.example.weatherapp.Fragments.DaysFragment
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
        //RequestWeather(requireContext(),"296543")
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
                val h0 = response.getJSONObject(0).toString()
                val h1 = response.getJSONObject(1).toString()
                val h2 = response.getJSONObject(2).toString()
                val h3 = response.getJSONObject(3).toString()
                val h4 = response.getJSONObject(4).toString()
                val h5 = response.getJSONObject(5).toString()
                val h6 = response.getJSONObject(6).toString()
                val h7 = response.getJSONObject(7).toString()
                val h8 = response.getJSONObject(8).toString()
                val h9 = response.getJSONObject(9).toString()
                val h10 = response.getJSONObject(10).toString()
                val h11 = response.getJSONObject(11).toString()
                Log.d("MyLog", "1:$h0\n2:$h1\n3:$h2\n4:$h3\n5:$h4\n6:$h5\n7:$h6\n8:$h7\n9:$h8\n10:$h9\n11:$h10\n12:$h11")
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
    //----------------------------------------------------------------
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
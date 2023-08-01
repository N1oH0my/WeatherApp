package com.example.weatherapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Adapters.HoursWeatherAdapter
import com.example.weatherapp.DataModels.WeatherHoursModel
import com.example.weatherapp.R
import com.example.weatherapp.ViewModels.MainViewModel
import com.example.weatherapp.databinding.FragmentHoursBinding


class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: HoursWeatherAdapter

    private val cur_data: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        InitRecyclerView()
        cur_data.live_data_hours.observe(viewLifecycleOwner)
        {

            adapter.submitList(cur_data.live_data_hours.value)
        }
    }

    private fun InitRecyclerView() = with(binding)
    {
        idRecyclerH.layoutManager = LinearLayoutManager(activity)
        adapter = HoursWeatherAdapter()
        idRecyclerH.adapter = adapter

        /*
        val testlist = listOf<WeatherHoursModel>(
            WeatherHoursModel("Частичная облачность", "partly_cloudy_img.png", "22°C", "09:00 AM"),
            WeatherHoursModel("Ясное небо", "clear_skies_img.png", "28°C", "02:00 PM"),
            WeatherHoursModel("Грозы", "thunderstorms_img.png", "17°C", "07:00 PM"),
            WeatherHoursModel("Пасмурно", "cloudy_img.png", "20°C", "10:00 AM"),
            WeatherHoursModel("Дождь", "rain_img.png", "15°C", "03:00 PM"),
            WeatherHoursModel("Солнечно", "sunny_img.png", "30°C", "08:00 PM")
        )

        adapter.submitList(testlist)*/
    }

    companion object {

        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
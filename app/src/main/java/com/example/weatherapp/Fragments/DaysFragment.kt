package com.example.weatherapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapters.DaysWeatherAdapter
import com.example.weatherapp.DataModels.WeatherDayItem
import com.example.weatherapp.ViewModels.MainViewModel
import com.example.weatherapp.databinding.FragmentDaysBinding


class DaysFragment : Fragment() {

    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: DaysWeatherAdapter

    private val cur_data: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        InitRecyclerView()
        cur_data.live_data_days.observe(viewLifecycleOwner)
        {
            adapter.submitList(cur_data.live_data_days.value)
        }
    }

    private fun InitRecyclerView() = with(binding)
    {
        idRecyclerD.layoutManager = LinearLayoutManager(activity)
        adapter = DaysWeatherAdapter()
        idRecyclerD.adapter = adapter

        /*
        val testlist = listOf<WeatherDayItem>(
            WeatherDayItem(
                "Малооблачно",
                "partly_cloudy_img.png",
                "22°C",
                "10°C",
                "День",
                "Ночь",
                "sky_day_img_url.png",
                "sky_night_img_url.png",
                "Хорошее",
                "3 м/с",
                "Восточное",
                "05:30",
                "20:00"
            ),
            WeatherDayItem(
                "Ясно",
                "clear_skies_img.png",
                "28°C",
                "15°C",
                "День",
                "Ночь",
                "sky_day_img_url.png",
                "sky_night_img_url.png",
                "Отличное",
                "5 м/с",
                "Южное",
                "05:45",
                "19:45"
            ),
            WeatherDayItem(
                "Грозы",
                "thunderstorms_img.png",
                "17°C",
                "12°C",
                "День",
                "Ночь",
                "sky_day_img_url.png",
                "sky_night_img_url.png",
                "Удовлетворительное",
                "2 м/с",
                "Северное",
                "06:00",
                "19:30"
            ),
            WeatherDayItem(
                "Пасмурно",
                "cloudy_img.png",
                "19°C",
                "14°C",
                "День",
                "Ночь",
                "sky_day_img_url.png",
                "sky_night_img_url.png",
                "Хорошее",
                "4 м/с",
                "Западное",
                "06:15",
                "19:15"
            ),
            WeatherDayItem(
                "Дождь",
                "rain_img.png",
                "16°C",
                "11°C",
                "День",
                "Ночь",
                "sky_day_img_url.png",
                "sky_night_img_url.png",
                "Удовлетворительное",
                "3 м/с",
                "Юго-западное",
                "06:30",
                "19:00"
            ),
            WeatherDayItem(
                "Снег",
                "snow_img.png",
                "0°C",
                "-5°C",
                "День",
                "Ночь",
                "sky_day_img_url.png",
                "sky_night_img_url.png",
                "Плохое",
                "1 м/с",
                "Северо-восточное",
                "06:45",
                "18:45"
            )
        )


        adapter.submitList(testlist)*/
    }
    companion object {

        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}
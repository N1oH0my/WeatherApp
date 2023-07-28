package com.example.weatherapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Adapters.HoursWeatherAdapter
import com.example.weatherapp.DataModels.WeatherHoursModel
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHoursBinding


class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: HoursWeatherAdapter
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
    }

    private fun InitRecyclerView() = with(binding)
    {
        idRecyclerH.layoutManager = LinearLayoutManager(activity)
        adapter = HoursWeatherAdapter()
        idRecyclerH.adapter = adapter

        val testlist = listOf<WeatherHoursModel>(
            WeatherHoursModel("Partly Cloudy", "partly_cloudy_img.png", "22°C", "09:00 AM"),
            WeatherHoursModel("Clear Skies", "clear_skies_img.png", "28°C", "02:00 PM"),
            WeatherHoursModel("Thunderstorms", "thunderstorms_img.png", "17°C", "07:00 PM"),
        )

        adapter.submitList(testlist)
    }

    companion object {

        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
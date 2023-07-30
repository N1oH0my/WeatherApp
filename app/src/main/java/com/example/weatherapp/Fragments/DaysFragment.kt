package com.example.weatherapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapters.DaysWeatherAdapter
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
        val testlist = listOf<WeatherHoursModel>(
            WeatherHoursModel("Partly Cloudy", "partly_cloudy_img.png", "22°C", "09:00 AM"),
            WeatherHoursModel("Clear Skies", "clear_skies_img.png", "28°C", "02:00 PM"),
            WeatherHoursModel("Thunderstorms", "thunderstorms_img.png", "17°C", "07:00 PM"),
        )
        */

        //adapter.submitList(testlist)
    }
    companion object {

        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}
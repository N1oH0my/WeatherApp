package com.example.weatherapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.DataModels.WeatherHoursModel
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HoursListItemBinding

class HoursWeatherAdapter: ListAdapter<WeatherHoursModel, HoursWeatherAdapter.Holder>(Comparator()){

    class Holder(view: View): RecyclerView.ViewHolder(view) {
        val binding = HoursListItemBinding.bind(view)
        fun bind(item: WeatherHoursModel) = with(binding) {
            idHSky.text = item._sky
            //idHSkyImg
            idHTemp.text = item._temp
            idHTime.text = item._hour
        }
    }


    class Comparator: DiffUtil.ItemCallback<WeatherHoursModel>(){
        override fun areItemsTheSame(
            oldItem: WeatherHoursModel,
            newItem: WeatherHoursModel
        ): Boolean {
            // обычно в БД сравнение по id
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WeatherHoursModel,
            newItem: WeatherHoursModel
        ): Boolean {
            // просто сравнение
            return oldItem == newItem
        }

    }

    // запускается столько раз сколько айтемов в списке
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hours_list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}
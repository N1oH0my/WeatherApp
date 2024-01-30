package com.example.weatherapp.app.presentation.Adapters

import android.content.Context
import android.provider.Settings.System.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.app.core.entities.DataModels.WeatherDayItem
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DaysListItemBinding
import com.squareup.picasso.Picasso

class DaysWeatherAdapter: ListAdapter<WeatherDayItem, DaysWeatherAdapter.Holder>(Comparator()){

    class Holder(view: View): RecyclerView.ViewHolder(view) {
        val binding = DaysListItemBinding.bind(view)
        fun bind(item: WeatherDayItem, context: Context) = with(binding) {
            /*idTextDDay.text = context.getString(R.string.Day)
            idTextDNight.text = context.getString(R.string.Night)
            idDTextWind.text = context.getString(R.string.Wind)*/

            idDDate.text = item._date
            idDTempMax.text = item._max_temp
            idDTempMin.text = item._min_temp
            idDWind.text = (item._wind+" "+item._wind_direction)

            Picasso.get().load(item._sky_day_img_url).into(idDDaySkyImg)
            Picasso.get().load(item._sky_night_img_url).into(idDNightSkyImg)
        }
    }


    class Comparator: DiffUtil.ItemCallback<WeatherDayItem>(){
        override fun areItemsTheSame(
            oldItem: WeatherDayItem,
            newItem: WeatherDayItem
        ): Boolean {
            // обычно в БД сравнение по id
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WeatherDayItem,
            newItem: WeatherDayItem
        ): Boolean {
            // просто сравнение
            return oldItem == newItem
        }

    }

    // запускается столько раз сколько айтемов в списке
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.days_list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val context = holder.itemView.context
        // Use the 'context' variable as needed
        holder.bind(getItem(position), context)
    }
}
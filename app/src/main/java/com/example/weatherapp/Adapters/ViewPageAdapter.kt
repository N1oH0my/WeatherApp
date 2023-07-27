package com.example.weatherapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageAdapter(fa: FragmentActivity, val f_list: List<Fragment>):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return f_list.size
    }

    override fun createFragment(position: Int): Fragment {
        return f_list[position]
    }

}
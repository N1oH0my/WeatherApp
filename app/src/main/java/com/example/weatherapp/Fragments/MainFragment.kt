package com.example.weatherapp.Fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.example.weatherapp.Adapters.ViewPageAdapter
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.Fragments.HoursFragment
import com.example.weatherapp.Fragments.DaysFragment
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {

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
    //----------------------------------------------------------------
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
    private fun PermissionChecker()
    {
        if (IsPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            PermissionListner()
            p_launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    //----------------------------------------------------------------
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
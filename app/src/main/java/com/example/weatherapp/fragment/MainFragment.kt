package com.example.weatherapp.fragment

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.weatherapp.fragment.IsPermissionsGranted
import com.example.weatherapp.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var p_launcher: ActivityResultLauncher<String>


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
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
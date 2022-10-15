package com.summer.ourdrive.ui.screens.pin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.summer.ourdrive.databinding.ActivityPinBinding

class PinActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPinBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavHost()
    }

    private fun initNavHost() {
        navHostFragment =
            (supportFragmentManager.findFragmentById(binding.actPinNavHost.id) as NavHostFragment?)!!
        controller = navHostFragment.navController
    }

}
package com.summer.ourdrive.ui.screens.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.summer.ourdrive.R
import com.summer.ourdrive.databinding.ActivityMainBinding
import com.summer.ourdrive.viewmodelFactory.AppViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var controller: NavController
    private var doubleBackPress: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tlActMain)
        initNavHost()
        listeners()
    }

    private fun listeners() {
        binding.tlActMain.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (controller.currentDestination?.id == R.id.frag_main) {
            if (doubleBackPress == 0) {
                Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show()
                doubleBackPress = 1
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(this, AppViewModelFactory(application))[MainViewModel::class.java]
    }

    private fun initNavHost() {
        navHostFragment =
            (supportFragmentManager.findFragmentById(binding.actMainNavHost.id) as NavHostFragment?)!!
        controller = navHostFragment.navController
        controller.addOnDestinationChangedListener { controller, _, _ ->
            doubleBackPress = 0
            if (controller.currentDestination?.id == R.id.frag_main) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            } else {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
        }
    }
}
package com.summer.ourdrive.viewmodelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.summer.ourdrive.apiservices.RetrofitBuilder
import com.summer.ourdrive.repositories.ApiRepository
import com.summer.ourdrive.repositories.DatabaseRepository
import com.summer.ourdrive.ui.screens.main.MainViewModel

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    ApiRepository(RetrofitBuilder.getAPiService()),
                    DatabaseRepository(application)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}
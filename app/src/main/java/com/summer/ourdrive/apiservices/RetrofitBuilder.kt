package com.summer.ourdrive.apiservices

import com.squareup.moshi.Moshi
import com.summer.ourdrive.utils.Utils
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {
    private var apiService: ApiService? = null
    fun getAPiService(): ApiService {
        if (apiService == null) {
            val moshi = Moshi.Builder()
                .build()
            apiService = Retrofit.Builder()
                .baseUrl(Utils.WORLD_TIME_CLOCK_API)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build().create(ApiService::class.java)
        }
        return apiService!!
    }
}
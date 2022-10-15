package com.summer.ourdrive.apiservices


import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/timezone/Asia/Kolkata/")
    suspend fun getUnixTime(): Response<TimeClockResponse?>
}
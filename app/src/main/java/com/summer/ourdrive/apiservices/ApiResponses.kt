package com.summer.ourdrive.apiservices

import com.squareup.moshi.Json


data class TimeClockResponse(
    @Json(name = "abbreviation")
    var abbreviation: String?,
    @Json(name = "unixtime")
    var unixtime: Long?
)

data class FirebaseFolder(
    @Json(name = "create_unix_time")
    var create_unix_time: Long?,
    @Json(name = "folder_name")
    var folder_name: String?
)

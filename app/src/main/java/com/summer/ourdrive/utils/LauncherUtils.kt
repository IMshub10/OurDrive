package com.summer.ourdrive.utils

import android.app.Activity
import android.content.Intent

object LauncherUtils {
    fun finishAndStartActivity(intent: Intent, activity: Activity) {
        activity.apply {
            finish()
            startActivity(intent)
        }
    }
}
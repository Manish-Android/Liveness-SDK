package com.manish.livenesssdk

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

object LivenessSDK {

    const val LIVENESS_RESULT = "LIVENESS_RESULT"
    const val RESULT_SUCCESS = "SUCCESS"
    const val RESULT_FAILED = "FAILED"

    fun startLivenessCheck(launcher: ActivityResultLauncher<Intent>, context: Context) {
        val intent = Intent(context, LivenessActivity::class.java)
        launcher.launch(intent)
    }
}
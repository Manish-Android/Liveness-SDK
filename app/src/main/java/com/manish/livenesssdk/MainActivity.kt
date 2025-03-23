package com.manish.livenesssdk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.manish.livenesssdk.LivenessSDK.startLivenessCheck
import com.manish.livenesssdk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val livenessLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val livenessResult = result.data?.getStringExtra(LivenessSDK.LIVENESS_RESULT)
                if (livenessResult == LivenessSDK.RESULT_SUCCESS) {
                    Toast.makeText(this, "Liveness Check Passed!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Liveness Check Failed!", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartLiveness.setOnClickListener {
            startLivenessCheck(livenessLauncher, this)
        }
    }
}

package com.manish.livenesssdk

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.manish.livenesssdk.AppConstent.blink
import com.manish.livenesssdk.AppConstent.headLeft
import com.manish.livenesssdk.AppConstent.headRight
import com.manish.livenesssdk.AppConstent.leftEyeBlink
import com.manish.livenesssdk.AppConstent.rightEyeBlink
import com.manish.livenesssdk.AppConstent.smile
import com.manish.livenesssdk.LivenessSDK.LIVENESS_RESULT
import com.manish.livenesssdk.LivenessSDK.RESULT_FAILED
import com.manish.livenesssdk.LivenessSDK.RESULT_SUCCESS
import com.manish.livenesssdk.databinding.ActivityLivenessBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.jvm.java

class LivenessActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: ActivityLivenessBinding
    private var overallTimer: CountDownTimer? = null
    private var isNavigationStarted = false
    private lateinit var shuffledSteps: List<Pair<String, Int>>
    private var currentStepIndex = 0

    private val livenessActions = listOf(
        smile to R.drawable.smiling_face,
        headRight to R.drawable.right_head_turn_face,
        headLeft to R.drawable.left_head_turn_face,
        blink to R.drawable.blinking_face,
        leftEyeBlink to R.drawable.left_eye_close_face,
        rightEyeBlink to R.drawable.right_eye_close_face
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLivenessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Shuffle steps and images
        shuffledSteps = livenessActions.shuffled()

        // ✅ Show first step
        updateInstruction()

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

        startOverallTimer()

    }
    private fun startOverallTimer() {
        overallTimer?.cancel() // Cancel any existing timer

        overallTimer = object : CountDownTimer(20000, 1000) { // 20 sec total
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "Time left: ${millisUntilFinished / 1000}s" // Show countdown
            }



            override fun onFinish() {
                binding.tvInstrucation.text = "Time Expired! Restarting..."

             /*   val intent = Intent()
                intent.putExtra(LIVENESS_RESULT, RESULT_FAILED) // Send failure result
                setResult(RESULT_CANCELED, intent)
                finish() // Close the activity*/

                restartLivenessCheck()
            }

        }.start()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.preview.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImage(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    val smileProb = face.smilingProbability ?: 0.0f
                    val leftEyeOpen = face.leftEyeOpenProbability ?: 0.0f
                    val rightEyeOpen = face.rightEyeOpenProbability ?: 0.0f
                    val headTurnAngle = face.headEulerAngleY

                    val (currentAction, _) = shuffledSteps[currentStepIndex] // Get shuffled action

                    when (currentAction) {
                        smile -> if (smileProb > 0.7) runOnUiThread { completeStep() }
                        headRight -> if (headTurnAngle < -15) runOnUiThread { completeStep() }
                        headLeft -> if (headTurnAngle > 15) runOnUiThread { completeStep() }
                        blink -> if (leftEyeOpen < 0.3 && rightEyeOpen < 0.3) runOnUiThread { completeStep() }
                        rightEyeBlink -> if (leftEyeOpen < 0.3 && rightEyeOpen > 0.7) runOnUiThread { completeStep() }
                        leftEyeBlink -> if (rightEyeOpen < 0.3 && leftEyeOpen > 0.7) runOnUiThread { completeStep() }
                    }
                }
                imageProxy.close()
            }
            .addOnFailureListener {
                Log.e("Liveness", "Face detection failed", it)
                imageProxy.close()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun completeStep() {
        if (currentStepIndex < shuffledSteps.size - 1) {
            currentStepIndex++
            updateInstruction()
        } else {
            overallTimer?.cancel()
            binding.tvInstrucation.text = "Liveness Check Completed! ✅"
            binding.imageView.setImageResource(R.drawable.ic_task_completed)

            if (!isNavigationStarted) { // ✅ Prevent multiple calls
                isNavigationStarted = true
                val intent = Intent()
                intent.putExtra(LIVENESS_RESULT, RESULT_SUCCESS) // ✅ Send success result
                setResult(RESULT_OK, intent) // Send result back to caller
                finish() // Close the activity
            }
        }
    }


    private fun updateInstruction() {
        val (instruction, imageRes) = shuffledSteps[currentStepIndex]
        binding.tvInstrucation.text = instruction
        binding.imageView.setImageResource(imageRes)
    }

    private fun restartLivenessCheck() {
        currentStepIndex = 0
        shuffledSteps = livenessActions.shuffled() // Reshuffle steps
        updateInstruction()
        startOverallTimer() // Restart the timer
    }


}
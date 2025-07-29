# Liveness-SDK
A lightweight Android SDK for **liveness detection** using **Google ML Kit**. This SDK allows developers to easily integrate **face detection-based liveness checks** into their apps.

## 📌 Features
✅ **Face Detection** using Google ML Kit  
✅ Supports **smile detection, blinking, and head movements**  
✅ **Easy Integration** with just one function call  
✅ Works with **Activity Result API**  

## Instal

//-------------------Add this in your settings.gradle.kts file -------------------------------------
``` setting Gradle
  maven { url = uri("https://jitpack.io") } // ✅ Add JitPack repo
```
//----------------- Add this dependency in build.gradle.kts file -----------------------------------
``` build gradle kts
dependencies {
    implementation("com.github.Manish-Android:Liveness-SDK:1.0.0") // ✅ Replace with latest version
}
```
## USE 

//------------------- Add this in your Activity  ---------------------------------------------------
``` use
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
```
//------------------- Add this line in your on create ---------------------------------------------   
``` on create
LivenessSDK.startLivenessCheck(livenessLauncher, this)  // ✅ Start liveness check
```

## License
This SDK uses Google ML Kit for face detection.
By using this SDK, you must comply with Google ML Kit's Terms of Service.

## Support & Contributions
🔹 If you encounter any issues, feel free to open an issue.
🔹 Want to improve this SDK? Fork it and submit a pull request! 🚀

## Demo Video

https://github.com/user-attachments/assets/43aa87cc-e1ac-45ba-8964-59c8863f79dc


 
	


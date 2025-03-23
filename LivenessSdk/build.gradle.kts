plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.manish.livenesssdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        version = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.androidx.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Use this dependency to use the dynamically downloaded model in Google Play Services
    implementation (libs.play.services.mlkit.face.detection)

    implementation(libs.androidx.camera.core) // Replace with the latest version
    implementation(libs.androidx.camera.camera2) // Replace with the latest version
    implementation(libs.androidx.camera.lifecycle.v130) // Replace with the latest version

    implementation(libs.androidx.camera.core.v130)

    // CameraX Lifecycle (to bind camera to lifecycle)
    implementation(libs.androidx.camera.lifecycle)

    // CameraX View (for PreviewView)
    implementation(libs.androidx.camera.view)

    // CameraX Extensions (optional, for features like night mode)
    implementation(libs.androidx.camera.extensions)

    //Dimen
    implementation (libs.ssp.android)
    implementation (libs.sdp.android)
}
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.fpt.project"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fpt.project"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // Image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    
    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    
    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Google Play Services
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}
android {
    namespace = "com.paradise.drowsydetector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.paradise.drowsydetector"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // dagger hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // LeakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
//    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:2.7")

    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // optional - RxJava2 support
    implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")

    // optional - RxJava3 support
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0")


    // Alternatively - use the following artifact without an Android dependency.
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")


    // ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")

    // TedPermission Normal
    implementation("io.github.ParkSangGwon:tedpermission-normal:3.3.0")
    // TedPermission Coroutine
    implementation("io.github.ParkSangGwon:tedpermission-coroutine:3.3.0")

    // mlkit face detection
    implementation("com.google.mlkit:face-detection:16.1.5")
    // mlkit face mesh
    implementation("com.google.mlkit:face-mesh-detection:16.0.0-beta1")

    // GPS api
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.3.0-alpha05"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    //noinspection GradleDependency
    implementation("androidx.camera:camera-core:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    //noinspection GradleDependency
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX View class
    //noinspection GradleDependency
    implementation("androidx.camera:camera-view:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")

    // activity_ktx
    implementation("androidx.activity:activity-ktx:1.8.0")
    // fragment_ktx, viewmodels()를 사용하기 위해
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    // viewmodel_ktx
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    // LifecycleScope_ktx
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    //livedata_ktx
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // optional - helpers for implementing LifecycleOwner in a Service
    implementation("androidx.lifecycle:lifecycle-service:2.6.2")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    //gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Room DB
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // RxBinding
    implementation("com.jakewharton.rxbinding4:rxbinding:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-core:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-appcompat:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-drawerlayout:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-leanback:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-recyclerview:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-slidingpanelayout:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-swiperefreshlayout:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-viewpager:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-viewpager2:4.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-material:4.0.0")

    // FlowBinding
    val flowbinding_version = "1.2.0"
    implementation("io.github.reactivecircus.flowbinding:flowbinding-android:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-material:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-activity:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-appcompat:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-core:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-lifecycle:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-navigation:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-preference:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-recyclerview:${flowbinding_version}")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-viewpager2:${flowbinding_version}")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
    correctErrorTypes = true
}
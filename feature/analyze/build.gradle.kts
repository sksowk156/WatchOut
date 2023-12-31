import Dependencies.Location
import Dependencies.MpAndroidChart

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

//plugins {
//    `android-library`
//    `kotlin-android`
//}
//
//apply<MainGradlePlugin>()

android {
    namespace = "com.paradise.analyze"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    kotlinOptions {
        jvmTarget = "18"
    }

}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")

    core_model()
    core_data()
    core_domain()
    core_common()
    core_common_ui()

    // navi
    navigation()
    // dagger hilt
    hilt()
    // Preferences DataStore
    dataStore()
    // mlkit face detection
    mlkit()
    // GPS api
    implementation(Dependencies.Location)
    // Camera
    camera()
    // ktx
    ktx()
    // viewmodel_ktx
    lifecycleKTX()
    // retrofit
    retrofit()
    //Coroutine
    coroutine()
    // Room DB
    room()
    // RxBinding
    rxBinding()

    implementation("com.google.guava:guava:30.1.1-android")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
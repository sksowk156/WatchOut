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
    namespace = "com.paradise.home"
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
    core_model()
    core_data()
    core_domain()
    core_common()
    core_common_ui()
    // navi
    navigation()
    // dagger hilt
    hilt()
    // TedPermission
    tedPermission()
    // ktx
    ktx()
    // viewmodel_ktx
    lifecycleKTX()
    //Coroutine
    coroutine()
    // RxBinding
    rxBinding()


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
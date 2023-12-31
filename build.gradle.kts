// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.20" apply false

    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("androidx.navigation.safeargs") version "2.5.3" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//        maven { url = uri("https://www.jitpack.io") }
//    }
//
//    dependencies {
//        classpath("com.android.tools.build:gradle:8.1.1")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
//        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
//        classpath(Dependencies.navi)
//        classpath("com.google.gms:google-services:4.4.0")
//    }
//}
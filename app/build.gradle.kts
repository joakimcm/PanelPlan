plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.0"
    alias(libs.plugins.google.devtools.ksp)

    // Hilt
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "no.uio.ifi.in2000.joakimcm.solcelleapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.joakimcm.solcelleapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // Ktor
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation(libs.ktor.client.android)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    //Mapbox
    implementation("com.mapbox.maps:android:11.11.0")
    implementation("com.mapbox.extension:maps-compose:11.11.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // RoomDB
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    //Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // ViewModel
    implementation(libs.androidx.hilt.navigation.compose.v110)

    //Debug?
    implementation(libs.slf4j.simple)

    // Date
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Testing
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation(libs.androidx.core.testing)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation(libs.mockwebserver)

    // Icons
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material3:material3:1.2.0-beta01")
    implementation(libs.androidx.material.icons.extended)

    //Lottie animation
    implementation(libs.lottie.compose)

    // for splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.9.0")


}
kapt {
    correctErrorTypes = true
}
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}
import org.jetbrains.kotlin.fir.declarations.builder.buildScript

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.realmKotlin)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}


android {
    namespace = "com.example.callscreenapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "color.call.screen.theme.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 6
        versionName = "1.0.0001"

//        javaCompileOptions {
//            annotationProcessorOptions {
//                argument("room.schemaLocation", "$projectDir/schemas")
//            }
//        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    dependencies{
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    }
    defaultConfig {
        multiDexEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")

    implementation("org.reduxkotlin:redux-kotlin-threadsafe-jvm:0.5.5")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.activity:activity-ktx:1.7.0")
//    implementation("com.google.code.gson:gson:2.8.8")
    implementation("io.realm.kotlin:library-base:1.11.0")
    implementation("io.realm.kotlin:library-sync:1.11.0")// If using Device Sync
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0") // If using coroutines with the SDK
//    implementation("com.google.android.play:core:1.10.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // animation
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation ("pl.bclogic:pulsator4droid:1.0.3")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.firebase:firebase-analytics:21.6.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("com.google.firebase:firebase-auth-ktx")

    //Admob Ads
    implementation ("com.google.android.gms:play-services-ads:23.0.0")
    implementation ("com.google.android.gms:play-services-appset:16.0.2")
    implementation ("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation ("com.google.android.gms:play-services-basement:18.3.0")

    //Billing
    implementation ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation ("com.google.guava:guava:31.1-jre")
    implementation ("com.android.billingclient:billing:6.2.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    implementation (project(":whaleutils"))
    implementation (project(":CrashDebugger"))
}
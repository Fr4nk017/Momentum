plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

android {
    namespace = "com.momentum.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.momentum.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    // Alinea Java/Kotlin/Javac/KSP a Java 21 (asegúrate de instalar JDK 21)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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

    buildFeatures { compose = true }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

// Kotlin toolchain a 21
kotlin {
    jvmToolchain(21)
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    testImplementation("junit:junit:4.13.2")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // DataStore (si lo usas)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room (SQLite)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // WorkManager (si lo usas)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Material Components: attrs/temas XML que usa Material3
    implementation("com.google.android.material:material:1.12.0")

    // Location (opcional)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Retrofit + Moshi
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    // Moshi + soporte para data classes de Kotlin
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

// OkHttp logging (para ver las peticiones en Logcat, opcional pero útil)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coroutines para ViewModel / flows (si no las tienes ya)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")


}

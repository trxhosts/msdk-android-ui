plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
}

android {

    compileSdk = 34

    namespace = "com.trxhost.msdk.ui.integration.exampl"
    android.buildFeatures.buildConfig = true

    defaultConfig {

        applicationId = "com.trxhost.msdk.ui.integration.example"
        namespace = "com.trxhost.msdk.ui.integration.example"

        minSdk = 26

        versionName = "1.0.0"
        versionCode = 1

        buildConfigField(
            "String",
            "PROJECT_SECRET_KEY",
            "\"123\""
        )

        buildConfigField(
            "int",
            "PROJECT_ID",
            "123"
        )

        buildConfigField(
            "String",
            "GPAY_MERCHANT_ID",
            "\"\""
        )
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.trxhost:msdk-ui:2.1.3")

    implementation(compose.ui)
    implementation(compose.material)
    implementation(compose.preview)

    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.core:core-ktx:1.10.1")

    implementation( "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}
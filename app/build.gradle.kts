plugins {
    id("com.android.application")
    // 删掉了 id("org.jetbrains.kotlin.android")，因为它已经“注册过了”
}

android {
    namespace = "com.example.myfirstapp"
    compileSdk = 34 // 确保这里是 34，不要用那个复杂的 release(36)

    defaultConfig {
        applicationId = "com.example.myfirstapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    // 1. 基础 UI 库（保持不动）
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 2. 网络与图片（保持不动）
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 3. 📷 CameraX 引擎（保持不动）
    val camerax_version = "1.3.0"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    // 🧠 J.A.R.V.I.S 视觉大脑 (全新升级版：Google ML Kit)
    implementation("com.google.mlkit:image-labeling:17.0.7")

    // 5. 🌟 Room 数据库（保持不动）
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // 测试库
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

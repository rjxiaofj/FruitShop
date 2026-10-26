plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "cn.xrj.fruitshop"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cn.xrj.fruitshop"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
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
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.picasso)
}

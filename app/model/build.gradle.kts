plugins {
    alias(libs.plugins.kotlin.serialization)
    id("hnau-kmp")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.io)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.appcompat)
            }
        }
    }
}

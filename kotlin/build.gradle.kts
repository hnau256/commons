plugins {
    kotlin("plugin.serialization")
    id("hnau-kmp")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.atomicfu)
            }
        }
    }
}

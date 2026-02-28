plugins {
    kotlin("plugin.serialization")
    id("hnau-kmp")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.arrow.core)
                api(libs.arrow.core.serialization)
                api(libs.arrow.fx.coroutines)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.atomicfu)
                api(libs.kermit)
                api(libs.kermit.extensions)
            }
        }
    }
}

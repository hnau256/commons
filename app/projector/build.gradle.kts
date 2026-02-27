plugins {
    alias(libs.plugins.kotlin.serialization)
    id("hnau-compose")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":app:model"))
                implementation(project(":dynamiccolor"))
                implementation(libs.kotlinx.immutable)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.appcompat)
                implementation(libs.android.activityCompose)
            }
        }
    }
}

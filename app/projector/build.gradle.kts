plugins {
    id(hnau.plugins.kotlin.serialization.get().pluginId)
    id(hnau.plugins.hnau.ui.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlin"))
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

plugins {
    id(hnau.plugins.kotlin.serialization.get().pluginId)
    id(hnau.plugins.hnau.kmpAndroid.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlin"))
                implementation(hnau.kotlinx.io)
                implementation(hnau.kotlinx.serialization.json)
            }
        }
    }
}

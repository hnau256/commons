plugins {
    id(hnau.plugins.kotlin.serialization.get().pluginId)
    id(hnau.plugins.hnau.kmp.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(hnau.kotlinx.atomicfu)
                implementation(hnau.kotlinx.serialization.json)
            }
        }

        jvmMain {
            dependencies {
                implementation(hnau.slf4j.api)
            }
        }
    }
}

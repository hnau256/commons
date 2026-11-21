plugins {
    id(hnau.plugins.ksp.get().pluginId)
    id(hnau.plugins.kotlin.serialization.get().pluginId)
    id(hnau.plugins.hnau.kmpAndroidWithCompose.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlin"))
                implementation(project(":app:model"))
                implementation(project(":gen:enumvalues:annotations"))
                implementation(project(":gen:fold:annotations"))
            }
        }
        desktopMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${hnau.versions.kotlinxCoroutines.get()}")
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(project(":gen:enumvalues:processor"))
    kspCommonMainMetadata(project(":gen:fold:processor"))
}

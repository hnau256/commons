plugins {
    id(hnau.plugins.ksp.get().pluginId)
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
                implementation(project(":gen:enumvalues:annotations"))
                implementation(project(":gen:fold:annotations"))
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(project(":gen:enumvalues:processor"))
    kspCommonMainMetadata(project(":gen:fold:processor"))
}

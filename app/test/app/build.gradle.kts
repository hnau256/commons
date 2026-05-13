plugins {
    id(hnau.plugins.kotlin.serialization.get().pluginId)
    id(hnau.plugins.ksp.get().pluginId)
    id(hnau.plugins.hnau.kmpAndroidWithCompose.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlin"))
                implementation(project(":app:model"))
                implementation(project(":app:projector"))
                implementation(hnau.kotlinx.serialization.json)
                implementation(project(":gen:pipe:annotations"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.hnau.commons.app.test.app.DesktopAppKt"
    }
}

dependencies {
    kspCommonMainMetadata(project(":gen:pipe:processor"))
}

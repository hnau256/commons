plugins {
    id(hnau.plugins.kotlin.serialization.get().pluginId)
    id(hnau.plugins.ksp.get().pluginId)
    id(hnau.plugins.hnau.kmpAndroidWithCompose.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":app:model"))
                implementation(project(":app:projector"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.hnau.commons.app.test.app.DesktopAppKt"
    }
}

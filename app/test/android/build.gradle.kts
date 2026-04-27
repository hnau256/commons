import java.util.Properties

plugins {
    id(hnau.plugins.hnau.jvmAndroidApp.get().pluginId)
}

android {
    namespace = "hnau.commons.app.test.android"

    defaultConfig {
        applicationId = "hnau.commons.test"

        val versionPropsFile = file("version.properties")
        val versionProps =
            Properties().apply {
                load(versionPropsFile.inputStream())
            }
        val localVersionCode = (versionProps["versionCode"] as String).toInt()
        versionName = versionProps["versionName"] as String + "." + localVersionCode
        versionCode = localVersionCode

        tasks.named("preBuild") {
            doFirst {
                versionProps.setProperty("versionCode", (localVersionCode + 1).toString())
                versionProps.store(versionPropsFile.outputStream(), null)
            }
        }
    }
}

dependencies {
    implementation(project(":app:projector"))
    implementation(project(":app:model"))
    implementation(project(":app:test:app"))
}

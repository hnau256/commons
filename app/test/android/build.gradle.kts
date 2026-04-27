import java.util.Properties

plugins {
    id(hnau.plugins.ksp.get().pluginId)
    id(hnau.plugins.hnau.jvmAndroidApp.get().pluginId)
}

android {
    namespace = "hnau.commonsAppTest.android"

    defaultConfig {
        applicationId = "hnau.commonsAppTest"

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

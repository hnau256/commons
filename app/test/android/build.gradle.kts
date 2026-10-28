plugins {
    id(hnau.plugins.hnau.jvmAndroidApp.get().pluginId)
}

android {
    namespace = "hnau.commons.app.test.android"

    defaultConfig {
        applicationId = "hnau.commons.test"
    }
}

dependencies {
    implementation(project(":app:projector"))
    implementation(project(":app:model"))
    implementation(project(":app:test:app"))
}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("jvm") {
            id = "hnau-jvm"
            implementationClass = "hnau.plugins.JvmPlugin"
        }
        create("kmp") {
            id = "hnau-kmp"
            implementationClass = "hnau.plugins.KmpPlugin"
        }
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
}

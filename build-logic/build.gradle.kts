plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
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
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

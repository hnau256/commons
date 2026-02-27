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
            implementationClass = "org.hnau.plugins.JvmPlugin"
        }
        create("kmp") {
            id = "hnau-kmp"
            implementationClass = "org.hnau.plugins.KmpPlugin"
        }
        create("compose") {
            id = "hnau-compose"
            implementationClass = "org.hnau.plugins.ComposePlugin"
        }
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
}

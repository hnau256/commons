plugins {
    kotlin("plugin.serialization")
    id("hnau-jvm")
}

dependencies {
    implementation("androidx.annotation:annotation:1.8.0")
    implementation("com.google.errorprone:error_prone_annotations:2.26.0")
}


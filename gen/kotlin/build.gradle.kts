plugins {
    id("hnau-jvm")
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.ksp.api)
    implementation(project(":kotlin"))
}


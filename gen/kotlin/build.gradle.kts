plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.hnau.project")
}

hnau {
    jvm()
}

dependencies {
    implementation(project(":kotlin"))
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.ksp.api)
}

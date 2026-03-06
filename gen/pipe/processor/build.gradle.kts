plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.hnau.project")
}

hnau {
    jvm()
}

dependencies {
    implementation(project(":kotlin"))
    implementation(project(":gen:kotlin"))
    implementation(project(":gen:pipe:annotations"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}

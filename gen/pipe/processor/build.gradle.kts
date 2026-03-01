plugins {
    id("hnau-jvm")
}

dependencies {
    implementation(project(":gen:kotlin"))
    implementation(project(":gen:pipe:annotations"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}
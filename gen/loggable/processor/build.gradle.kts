plugins {
    id("hnau-jvm")
}

dependencies {
    implementation(project(":gen:loggable:annotations"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}

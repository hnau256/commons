plugins {
    id("org.jetbrains.kotlin.jvm")
    id(hnau.plugins.hnau.jvm.get().pluginId)
}

dependencies {
    implementation(project(":kotlin"))
    implementation(project(":gen:kotlin"))
    implementation(project(":gen:sealup:annotations"))
    implementation(hnau.ksp.api)
    implementation(hnau.kotlinpoet.core)
    implementation(hnau.kotlinpoet.ksp)
}

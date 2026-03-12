plugins {
    id(hnau.plugins.hnau.jvm.get().pluginId)
}

dependencies {
    implementation(project(":kotlin"))
    implementation(project(":gen:kotlin"))
    implementation(project(":gen:pipe:annotations"))
    implementation(hnau.ksp.api)
    implementation(hnau.kotlinpoet.core)
    implementation(hnau.kotlinpoet.ksp)
}

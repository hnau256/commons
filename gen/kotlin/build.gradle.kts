plugins {
    id(hnau.plugins.hnau.jvm.get().pluginId)
}

dependencies {
    implementation(project(":kotlin"))
    implementation(hnau.kotlinpoet.core)
    implementation(hnau.kotlinpoet.ksp)
    implementation(hnau.ksp.api)
}

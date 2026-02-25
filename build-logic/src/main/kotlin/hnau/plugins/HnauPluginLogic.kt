package hnau.plugins

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

enum class HnauProjectType { JVM, KMP }

internal fun Project.configureHnau(type: HnauProjectType) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val jvmVersion = libs.findVersion("jvm").get().requiredVersion
    val jvmTarget = JvmTarget.fromTarget(jvmVersion)

    when (type) {
        HnauProjectType.JVM -> {
            plugins.apply("org.jetbrains.kotlin.jvm")
            configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.toVersion(jvmVersion)
                targetCompatibility = JavaVersion.toVersion(jvmVersion)
            }
        }
        HnauProjectType.KMP -> {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            configure<KotlinMultiplatformExtension> {
                jvm()
            }
        }
    }

    // Явная проверка: наш плагин должен быть подключен ПОСЛЕ плагинов-технологий
    if (plugins.hasPlugin("org.jetbrains.kotlin.plugin.serialization")) {
        implementation("kotlinx-serialization-core")
        implementation("kotlinx-serialization-json")
    }

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            if (this is KotlinJvmCompilerOptions) {
                this.jvmTarget.set(jvmTarget)
            }
        }
    }
}

private fun Project.implementation(libName: String) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies.add("implementation", libs.findLibrary(libName).get())
}

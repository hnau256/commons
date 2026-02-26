package hnau.plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

enum class HnauProjectType { JVM, KMP }

internal fun Project.configureHnau(type: HnauProjectType) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val jvmVersion = libs.requireVersion("jvm")
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
            plugins.apply("com.android.kotlin.multiplatform.library")
            plugins.apply("org.jetbrains.kotlin.multiplatform")

            val kotlinExtension = extensions.getByType<KotlinMultiplatformExtension>()
            kotlinExtension.jvm()

            (kotlinExtension as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
                namespace = "hnau.commons." + path.drop(1).replace(':', '.')
                compileSdk = libs.requireVersion("androidCompileSdk").toInt()
                minSdk = libs.requireVersion("androidMinSdk").toInt()
            }
        }
    }

    plugins.apply("maven-publish")
    configure<PublishingExtension> {
        val artifactIdValue = path.drop(1).replace(':', '-')
        publications.withType<MavenPublication>().configureEach {
            artifactId = when (name) {
                "kotlinMultiplatform" -> artifactIdValue
                else -> artifactId.replace(project.name, artifactIdValue)
            }
        }

        if (type == HnauProjectType.JVM) {
            publications.create<MavenPublication>("maven") {
                from(components["java"])
                artifactId = artifactIdValue
            }
        }
    }

    // Безусловные зависимости
    addDependency("arrow-core")
    addDependency("arrow-core-serialization")
    addDependency("arrow-fx-coroutines")
    addDependency("kotlinx-coroutines-core")
    addDependency("kotlinx-datetime")
    addDependency("kotlinx-atomicfu")

    // Явная проверка: наш плагин должен быть подключен ПОСЛЕ плагинов-технологий
    if (plugins.hasPlugin("org.jetbrains.kotlin.plugin.serialization")) {
        addDependency("kotlinx-serialization-core")
        addDependency("kotlinx-serialization-json")
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

private fun VersionCatalog.requireVersion(alias: String): String =
    findVersion(alias).get().requiredVersion

private fun Project.addDependency(libName: String) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val library = libs.findLibrary(libName).get()

    val kmpExtension = extensions.findByType(KotlinMultiplatformExtension::class.java)
    when (kmpExtension) {
        null -> dependencies.add("implementation", library)
        else ->
            kmpExtension.sourceSets.getByName("commonMain").dependencies {
                implementation(library)
            }
    }
}

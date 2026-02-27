package org.hnau.plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

enum class HnauProjectType {
    JVM,
    KMP,
    COMPOSE,
    ;

    val isKmp: Boolean get() = this == KMP || this == COMPOSE
}

private sealed interface HnauDependency {
    class External(
        val alias: String,
    ) : HnauDependency

    class Internal(
        val project: Project,
    ) : HnauDependency
}

internal fun Project.configureHnau(type: HnauProjectType) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val jvmVersion = libs.requireVersion("jvm")
    val jvmTarget = JvmTarget.fromTarget(jvmVersion)

    when {
        type == HnauProjectType.JVM -> {
            plugins.apply("org.jetbrains.kotlin.jvm")
            configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.toVersion(jvmVersion)
                targetCompatibility = JavaVersion.toVersion(jvmVersion)
                withSourcesJar()
            }
        }

        type.isKmp -> {
            if (type == HnauProjectType.COMPOSE) {
                plugins.apply("org.jetbrains.kotlin.plugin.compose")
                plugins.apply("org.jetbrains.compose")
            }

            plugins.apply("com.android.kotlin.multiplatform.library")
            plugins.apply("org.jetbrains.kotlin.multiplatform")

            val kotlinExtension = extensions.getByType<KotlinMultiplatformExtension>()
            val jvmTargetName = if (type == HnauProjectType.COMPOSE) "desktop" else "jvm"

            kotlinExtension.jvm(jvmTargetName) {
                withSourcesJar()
            }

            kotlinExtension.linuxX64 {
            }

            (kotlinExtension as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
                namespace = "org.hnau.commons." + hnauPath('.')
                compileSdk = libs.requireVersion("androidCompileSdk").toInt()
                minSdk = libs.requireVersion("androidMinSdk").toInt()
            }

            if (type == HnauProjectType.COMPOSE) {
                afterEvaluate {
                    val composeVersion = libs.requireVersion("compose")
                    kotlinExtension.sourceSets.getByName("commonMain").dependencies {
                        implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
                        implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
                        implementation("org.jetbrains.compose.material3:material3:$composeVersion")
                        implementation("org.jetbrains.compose.ui:ui:$composeVersion")
                        implementation("org.jetbrains.compose.components:components-resources:$composeVersion")
                        implementation("org.jetbrains.compose.components:components-ui-tooling-preview:$composeVersion")
                    }
                }
            }
        }
    }

    // Dokka
    plugins.apply("org.jetbrains.dokka")
    val dokkaHtmlTask = tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")
    val javadocJar =
        tasks.register<Jar>("javadocJar") {
            from(dokkaHtmlTask.flatMap { it.outputDirectory })
            archiveClassifier.set("javadoc")
        }

    // Publishing
    plugins.apply("maven-publish")
    configure<PublishingExtension> {
        val artifactIdValue = hnauPath('-')

        if (type == HnauProjectType.JVM) {
            publications.create<MavenPublication>("maven") {
                from(components["java"])
            }
        }

        publications.withType<MavenPublication>().configureEach {
            groupId = "org.hnau.commons"
            artifactId =
                when (name) {
                    "kotlinMultiplatform" -> artifactIdValue
                    else -> artifactId.replace(project.name, artifactIdValue)
                }
            artifact(javadocJar)
        }
    }

    // Dependencies
    if (path != ":kotlin") {
        addDependency(HnauDependency.Internal(project(":kotlin")))
    }

    // Conditional dependencies
    if (plugins.hasPlugin("org.jetbrains.kotlin.plugin.serialization")) {
        addDependency(HnauDependency.External("kotlinx-serialization-core"))
        addDependency(HnauDependency.External("kotlinx-serialization-json"))
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

private fun VersionCatalog.requireVersion(alias: String): String = findVersion(alias).get().requiredVersion

private fun Project.hnauPath(separator: Char): String = path.drop(1).replace(':', separator)

private fun Project.addDependency(dependency: HnauDependency) {
    val actualDependency =
        when (dependency) {
            is HnauDependency.External -> {
                val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
                libs.findLibrary(dependency.alias).get()
            }
            is HnauDependency.Internal -> dependency.project
        }

    val kmpExtension = extensions.findByType(KotlinMultiplatformExtension::class.java)
    when (kmpExtension) {
        null -> dependencies.add("implementation", actualDependency)
        else ->
            kmpExtension.sourceSets.getByName("commonMain").dependencies {
                implementation(actualDependency)
            }
    }
}

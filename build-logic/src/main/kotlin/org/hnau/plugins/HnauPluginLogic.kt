package org.hnau.plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar
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
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.buildtools.api.jvm.JvmSnapshotBasedIncrementalCompilationOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

enum class HnauProjectType {
    JVM,
    KMP,
    COMPOSE,
    ;

    val isKmp: Boolean
        get() = when (this) {
            JVM -> false
            KMP, COMPOSE -> true
        }
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

            if (type != HnauProjectType.COMPOSE) {
                kotlinExtension.linuxX64()
            }

            (kotlinExtension as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
                namespace = "org.hnau.commons." + hnauPath('.')
                compileSdk = libs.requireVersion("androidCompileSdk").toInt()
                minSdk = libs.requireVersion("androidMinSdk").toInt()
            }

            if (type == HnauProjectType.COMPOSE) {
                addDependency(HnauDependency.External("compose-runtime"))
                addDependency(HnauDependency.External("compose-foundation"))
                addDependency(HnauDependency.External("compose-material3"))
                addDependency(HnauDependency.External("compose-ui"))
            }
        }
    }

    // Dokka
    plugins.apply("org.jetbrains.dokka")

    plugins.apply("com.vanniktech.maven.publish")
    plugins.apply("signing")

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral()
        signAllPublications()

        configure(
            when (type.isKmp) {
                true -> KotlinMultiplatform(
                    javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
                    sourcesJar = SourcesJar.Sources(),
                )

                false -> KotlinJvm(
                    javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
                    sourcesJar = SourcesJar.Sources(),
                )
            }

        )

        coordinates(
            groupId = "org.hnau.commons",
            artifactId = hnauPath('-'),
            version = project.version.toString()
        )

        pom {
            name.set(project.name)
            url.set("https://github.com/hnau256/commons")
            description.set(project.name)
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("hnau256")
                    name.set("Mark Zorikhin")
                    email.set("hnau256@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:github.com/hnau256/commons.git")
                url.set("https://github.com/hnau256/commons")
            }
        }
    }

    extra["mavenCentralUsername"] = providers.gradleProperty("mavenCentralUsername").orNull
    extra["mavenCentralPassword"] = providers.gradleProperty("mavenCentralPassword").orNull

    // Настройка GPG
    configure<SigningExtension> {
        val keyId = providers.gradleProperty("signing.keyId").orNull
        val password = providers.gradleProperty("signing.password").orNull
        val secretKey = providers.gradleProperty("signing.secretKey").orNull

        if (secretKey != null && password != null) {
            useInMemoryPgpKeys(keyId, secretKey, password)
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

private fun VersionCatalog.requireVersion(alias: String): String =
    findVersion(alias).get().requiredVersion

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

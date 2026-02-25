import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    java
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val jvmVersion = libs.findVersion("jvm").get().requiredVersion
val jvmTarget =
    org.jetbrains.kotlin.gradle.dsl.JvmTarget
        .fromTarget(jvmVersion)

java {
    sourceCompatibility = JavaVersion.toVersion(jvmVersion)
    targetCompatibility = JavaVersion.toVersion(jvmVersion)
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        if (this is org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions) {
            this.jvmTarget.set(jvmTarget)
        }
    }
}

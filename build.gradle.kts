import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:14.2.0")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<KtlintExtension> {
        // ktlint 1.7.0+ умеет парсить context-параметры (Kotlin 2.2+).
        version.set("1.8.0")
        filter {
            // Не форматируем/не проверяем сгенерированные исходники (KSP и пр.).
            exclude { element -> element.file.path.contains("/build/") }
        }
    }

    // Исходники из build/ попадают в source set как выход KSP-задач;
    // упорядочиваем, чтобы Gradle не считал это неявной зависимостью.
    tasks.withType(BaseKtLintCheckTask::class.java).configureEach {
        mustRunAfter(tasks.matching { it.name.startsWith("ksp") })
    }
}

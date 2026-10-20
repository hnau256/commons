package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.gradle.api.specs.Spec
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.utils.versions.Version
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

internal fun Project.configureKtlint() {
    applyPlugin(
        plugin = Versions.Plugins.ktlint.withoutAlias.withoutVersion,
    )

    extensions.configure(
        KtlintExtension::class.java,
    ) { ktlint ->
        // ktlint 1.7.0+ умеет парсить context-параметры (Kotlin 2.2+).
        ktlint.version.set(
            Version.Ktlint.version,
        )
        ktlint.additionalEditorconfig.set(
            ktlintEditorConfig,
        )
        ktlint.filter { filter ->
            // Не проверяем и не форматируем сгенерированные исходники (KSP и пр.).
            filter.exclude(
                Spec { element ->
                    element.file.path.contains("/build/")
                },
            )
        }
    }

    // Исходники из build/ попадают в source set как выход KSP-задач;
    // упорядочиваем, чтобы Gradle не считал это неявной зависимостью.
    tasks
        .withType(BaseKtLintCheckTask::class.java)
        .configureEach { task ->
            task.mustRunAfter(
                tasks.matching { it.name.startsWith("ksp") },
            )
        }
}

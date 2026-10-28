package org.hnau.commons.plugins.project.projectext

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import java.util.Properties

internal fun Project.configureAndroidVersion(
    applicationExtension: ApplicationExtension,
) {
    val versionPropertiesFile = file("version.properties")
    if (!versionPropertiesFile.exists()) {
        return
    }

    val versionProperties = Properties().apply {
        versionPropertiesFile.inputStream().use { load(it) }
    }
    val localVersionName = versionProperties.getProperty("versionName")
        ?: error("Property 'versionName' not found in ${versionPropertiesFile.absolutePath}")
    val localVersionCode = versionProperties.getProperty("versionCode")
        ?.toIntOrNull()
        ?: error("Property 'versionCode' is missing or incorrect in ${versionPropertiesFile.absolutePath}")

    applicationExtension.defaultConfig.apply {
        versionName = "$localVersionName.$localVersionCode"
        versionCode = localVersionCode
    }

    tasks.matching { task ->
        val isAssemble = task.name.startsWith("assemble")
        val isBundle = task.name.startsWith("bundle")
        (isAssemble || isBundle) &&
            task.name != "assemble" &&
            task.name != "bundle" &&
            !task.name.contains("Test", ignoreCase = true)
    }.configureEach { task ->
        task.doLast {
            val properties = Properties().apply {
                versionPropertiesFile.inputStream().use { load(it) }
            }
            val versionCode = properties.getProperty("versionCode")
                ?.toIntOrNull()
                ?: error("Property 'versionCode' is missing or incorrect in ${versionPropertiesFile.absolutePath}")
            properties.setProperty("versionCode", (versionCode + 1).toString())
            versionPropertiesFile.outputStream().use { properties.store(it, null) }
        }
    }
}

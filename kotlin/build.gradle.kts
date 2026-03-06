plugins {
    id("org.hnau.project")
}

hnau {
    kmp(includeHnauCommons = false)
    serialization = true
    ksp {
        arrowOptics = true
    }
}

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.arrow.core)
                api(libs.arrow.core.serialization)
                api(libs.arrow.optics)
                api(libs.arrow.fx.coroutines)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.atomicfu)
                api(libs.kermit)
            }
        }
    }
}

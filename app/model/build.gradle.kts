plugins {
    id("org.hnau.project")
}

hnau {
    kmp {}
    serialization = true
}

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlin"))
                implementation(libs.kotlinx.io)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.appcompat)
            }
        }
    }
}

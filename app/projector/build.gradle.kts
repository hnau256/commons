plugins {
    id("org.hnau.project")
}

hnau {
    kmp {
        compose = true
    }
    serialization = true
}

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kotlin"))
                implementation(project(":app:model"))
                implementation(project(":dynamiccolor"))
                implementation(libs.kotlinx.immutable)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.appcompat)
                implementation(libs.android.activityCompose)
            }
        }
    }
}

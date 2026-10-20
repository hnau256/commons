plugins {
    id(
        hnau.plugins.hnau.jvm
            .get()
            .pluginId,
    )
}

dependencies {
    // ktlint предоставляет эти классы во время работы, поэтому только compileOnly.
    compileOnly("com.pinterest.ktlint:ktlint-rule-engine-core:1.8.0")
    compileOnly("com.pinterest.ktlint:ktlint-cli-ruleset-core:1.8.0")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.2.0")
}

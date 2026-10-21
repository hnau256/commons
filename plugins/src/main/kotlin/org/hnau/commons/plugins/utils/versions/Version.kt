package org.hnau.commons.plugins.utils.versions

import org.hnau.commons.plugins.internal.hnauCommonsVersion

enum class Version(
    val version: String,
) {
    HnauCommons(hnauCommonsVersion),

    //https://kotlinlang.org/docs/releases.html
    Kotlin("2.4.0"),

    //https://github.com/Kotlin/kotlinx.serialization/releases
    KotlinxSerialization("1.11.0"),

    //https://developer.android.com/build/releases/gradle-plugin
    AndroidGradlePlugin("9.2.0"),

    //https://github.com/JetBrains/compose-multiplatform/releases
    ComposeMultiplatform("1.11.1"),
    ComposeMultiplatformMaterial3("1.11.0-alpha07"),

    //https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-core
    CommposeMultiplatformIcons("1.7.3"),

    JetpackCompose("1.11.2"),

    //https://mvnrepository.com/artifact/androidx.compose.material/material-icons-core
    JetpackComposeIcons("1.7.8"),
    JetpackComposeMaterial3("1.5.0-alpha17"),

    //https://developer.android.com/jetpack/androidx/releases/activity
    ActivityCompose("1.13.0"),

    //https://developer.android.com/jetpack/androidx/releases/appcompat
    AndroidAppCompat("1.7.1"),

    //https://developer.android.com/jetpack/androidx/releases/lifecycle
    LifecycleViewmodelCompose("2.11.0"),

    //https://developers.google.com/android/guides/releases?hl=ru
    GoogleServicesPlugin("4.5.0"),

    //https://github.com/google/ksp/releases
    Ksp("2.3.9"),

    //https://github.com/Kotlin/dokka/releases
    DokkaPlugin("2.2.0"),

    //https://github.com/vanniktech/gradle-maven-publish-plugin/releases
    VanniktechPlugin("0.37.0"),

    //https://github.com/arrow-kt/arrow/releases
    Arrow("2.2.3"),

    //https://github.com/Kotlin/kotlinx.coroutines/releases
    KotlinxCoroutines("1.11.0"),

    //https://github.com/Kotlin/kotlinx-datetime/releases
    KotlinxDateTime("0.8.0"),

    //https://github.com/Kotlin/kotlinx-atomicfu/releases
    KotlinxAtomicFu("0.33.0"),

    //https://github.com/Kotlin/kotlinx-io/releases
    KotlinxIO("0.9.0"),

    //https://github.com/touchlab/Kermit/releases
    Kermit("2.1.0"),

    //https://github.com/square/kotlinpoet/releases
    Kotlinpoet("2.3.0"),

    //https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    OkHttp("5.4.0"),

    //https://github.com/ionspin/kotlin-multiplatform-bignum/releases/
    BigNum("0.3.10"),

    //https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    Slf4j("2.0.18"), //Also update KermitSlf4jServiceProvider
    ;

    val alias: Alias = name
        .replaceFirstChar(Char::lowercase)
        .let(::Alias)
}

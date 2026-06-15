# Gradle Convention Plugins — Two-Phase Publishing

Convention plugins live in `plugins/` and are published to `~/.m2` (Maven Local).
Other modules resolve them from there, **not** from source. When you change plugin source code:

## Two-Phase Deploy

### Phase 1: Publish plugins from source

1. Restore any plugin code changes you need (e.g. `applyKotlinComposePlugin()`).
2. In `version.properties`:
   ```properties
   version=1.23.X        # bump to a NEW version
   pluginVersion=1.23.Y  # uncomment, set to LAST published version (for settings plugin bootstrap)
   ```
3. Run:
   ```bash
   ./gradlew --stop
   rm -rf plugins/build
   ./gradlew :plugins:publishToMavenLocal
   ```
   This compiles plugins from **current source** and publishes them as `version=X`.

### Phase 2: Build the project with new plugins

4. In `version.properties` — comment `pluginVersion` back:
   ```properties
   version=1.23.X
   # pluginVersion=1.23.X
   ```
5. Run the main build:
   ```bash
   ./gradlew :app:test:android:assembleDebug
   ```

## Why this is needed

- `pluginVersion=1.23.Y` bootstraps `HnauSettingsPlugin` from the last published JAR — needed to even parse `settings.gradle.kts`.
- `version=1.23.X` (new, unpublished) forces Gradle to build plugins from source instead of resolving from `~/.m2`.
- Without this, Gradle resolves plugins from the cached JAR in `~/.m2` and source changes are silently ignored.

## Bootstrap mechanic in `settings.gradle.kts`

```kotlin
resolutionStrategy {
    eachPlugin {
        if (requested.id.id == "org.hnau.plugin.settings") {
            val pluginVersion = properties.pluginVersion ?: projectVersion
            useVersion(pluginVersion)
        }
    }
}
```

- Settings plugin uses `pluginVersion` (if set) → resolves from published JAR.
- All other `org.hnau.plugin.*` use `version` from the catalog → resolves from source if version > any published.

## Important plugin call chain

`HnauJvmAndroidAppPlugin` → `configureJvm(isAndroidApp=true)` → **must** call `applyKotlinComposePlugin()`.

The compose plugin (`org.jetbrains.kotlin.plugin.compose`) depends on `kotlin-android`.
With Kotlin 2.4.0 + AGP 9.x this is normal — both are required and work together.
DO NOT remove `applyKotlinComposePlugin()` — it breaks Compose compiler activation.

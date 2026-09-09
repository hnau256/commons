# Commons — publishing a new version to `~/.m2`

All Commons artifacts (`commons-kotlin`, `gen/*` annotations+processors, `app-model`, `app-projector`,
and the `org.hnau.plugin.*` convention plugins) are published to `~/.m2` (Maven Local). Consumer
projects (e.g. PinFin) resolve them from there, **not** from source. Version lives in
`version.properties`; it is baked as the `hnauCommonsVersion` const into the compiled `plugins`
jar, and every `hnau.*` alias in consumer builds resolves through it.

## Runbook — publish a new version

### 1. Bump `version.properties`

At rest `pluginVersion` is COMMENTED OUT and its value is the last published version. To publish:

```properties
version=1.27.9        # NEW version being published
# pluginVersion=1.27.8  # last published version — the value under the comment
```

Uncomment `pluginVersion` for the duration of the publication; its value is already correct (it was
raised to the last published version at the end of the previous publication):

```properties
version=1.27.9
pluginVersion=1.27.8
```

### 2. Publish the plugins (bootstrap phase)

With `pluginVersion` still lowered, publish ONLY the plugins. The lowered `pluginVersion` makes the
build bootstrap `org.hnau.plugin.settings` from the last published JAR, which is needed to even parse
`settings.gradle.kts` while the new `version` is not yet published:

```bash
./gradlew :plugins:publishToMavenLocal
```

This publishes the `org.hnau.plugin.*` convention plugins **from source** at the new `version`,
while bootstrapping via the old plugins. (Adjust `:plugins:` to the actual project path if it
differs.)

### 3. Publish everything (full phase)

Now that the new plugins are in `~/.m2`, publish the full graph using the new plugins. Remove the
lowered `pluginVersion` override first — comment it out AND raise its value to the new `version` —
so the build resolves the plugins at the new `version`:

```properties
version=1.27.9
# pluginVersion=1.27.9
```

```bash
./gradlew publishToMavenLocal
```

Publishes ALL modules (kotlin, gen/*, app/model, app/projector, plugins) at `version`.
Do not publish just `plugins/` — consumers resolve the full `hnau.*` graph at one version, so the
whole repo must be there.

### 4. After a successful publish

Raise the `pluginVersion` value to the new version and comment it back out (already the case if you
left it commented and raised in step 3):

```properties
version=1.27.9
# pluginVersion=1.27.9
```

So the commented value always points at the last published version — ready for the next bump, where
you only change `version` and uncomment `pluginVersion` again.

### 5. Point consumers at the new version

Bump the settings-plugin pin in each consumer, e.g. PinFin `settings.gradle.kts`:

```kotlin
id("org.hnau.plugin.settings") version "1.27.9"
```

Because `hnauCommonsVersion` is baked into the settings plugin at build time, this single bump
re-points ALL `hnau.*` artifact/plugin aliases (including the KSP generators: pipe, loggable,
sealup, enumvalues, fold) to the new version.

### 6. Rebuild the consumer

```bash
cd <consumer>
./gradlew :data:compileKotlinJvm   # e.g. PinFin — first module that runs the KSP generators
```

## Rules

- **ALWAYS bump `version`** — never republish/overwrite an already-deployed version in `~/.m2`.
- `pluginVersion` (comment value) always holds the last published version; keep it COMMENTED at rest
  and uncommented only for the duration of a publication. Never set it to an unpublished version.
- **Use the lowered `pluginVersion` ONLY for the bootstrap phase** (`:plugins:publishToMavenLocal`).
  Before the full publish (step 3), remove the override by commenting it out **and raising its value
  to the new `version`** so everything else builds against the new plugins at the new `version`.
- `publishToMavenLocal` is versioned, so consumers must resolve the new version (step 4) or they
  keep using the old jar and your source changes are **silently ignored**.
- If a consumer fails to resolve the new version after bumping, run with `--refresh-dependencies`
  (or wipe the stale entry under `~/.gradle/caches/modules-2/files-2.1/org.hnau.commons/...`).

## How version wiring works

- `hnauCommonsVersion` const is generated in `plugins/build.gradle.kts` from
  `rootProject.extra["hnauCommonsVersion"]` at plugin compile time and read by
  `plugins/.../utils/versions/Version.kt` (`Version.HnauCommons`). All `hnau.*` aliases use it.
- Settings plugin bootstrap (`settings.gradle.kts`):

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

  - Settings plugin uses `pluginVersion` (if set) → resolves from the published JAR.
  - All other `org.hnau.plugin.*` use `version` from the catalog → resolved from source when
    `version` is newer than anything published.

## Important plugin call chain

`HnauJvmAndroidAppPlugin` → `configureJvm(isAndroidApp=true)` → **must** call `applyKotlinComposePlugin()`.

The compose plugin (`org.jetbrains.kotlin.plugin.compose`) depends on `kotlin-android`.
With Kotlin 2.4.0 + AGP 9.x this is normal — both are required and work together.
DO NOT remove `applyKotlinComposePlugin()` — it breaks Compose compiler activation.
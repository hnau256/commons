package org.hnau.commons.app.model.file

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.files.Path

fun File.exists(): Boolean =
    fileSystem.exists(path)

fun File.source(): RawSource =
    fileSystem.source(path)

fun File.mkDirs() {
    fileSystem.createDirectories(path)
}

fun File.list(): List<File> = fileSystem
    .list(path)
    .map { childPath ->
        copy(path = childPath)
    }

fun File.delete() {
    fileSystem.delete(path)
}

fun File.sink(
    append: Boolean = false,
): RawSink = fileSystem.sink(
    path = path,
    append = append,
)

inline fun File.map(
    transformPath: (Path) -> Path,
): File = copy(
    path = transformPath(path),
)

operator fun File.plus(
    pathPart: String,
): File = map { path ->
    Path(path, pathPart)
}

val File.parent: File?
    get() = path.parent?.let { parent ->
        copy(
            path = parent,
        )
    }

val File.absolutePath: String
    get() = fileSystem.resolve(path).toString()
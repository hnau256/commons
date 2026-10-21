package org.hnau.commons.plugins.settings

import org.hnau.commons.plugins.utils.SharedConfig
import org.hnau.commons.plugins.utils.versions.ProjectId

internal fun SharedConfigExtension.toSharedConfig(
    projectId: ProjectId,
): SharedConfig = SharedConfig(
    projectId = projectId,
    publish = publish,
)
package org.hnau.commons.plugins.settings

import org.hnau.commons.plugins.utils.SharedConfig

internal fun SharedConfigExtension.toSharedConfig(): SharedConfig = SharedConfig(
    groupId = groupIdNotNull,
    publish = publish,
)
package com.mikael.mkAPI.kotlin.spigot.api.plugin

import java.io.File

interface MKPluginInstance {
    val plugin: Any?
    val systemName: String?
    val pluginFolder: File?
}
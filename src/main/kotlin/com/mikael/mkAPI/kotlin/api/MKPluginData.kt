package com.mikael.mkAPI.kotlin.api

import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import net.eduard.api.lib.database.SQLManager
import net.eduard.api.lib.database.api.DatabaseElement

interface MKPluginData : DatabaseElement {

    override val sqlManager: SQLManager
        get() = apimanager.sqlManager

}
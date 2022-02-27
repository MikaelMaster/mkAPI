package com.mikael.mkAPI.kotlin.api

import net.eduard.api.lib.database.SQLManager

class APIManager {
    companion object {
        lateinit var instance: APIManager
    }

    init {
        instance = this@APIManager
    }

    val dbManager get() = sqlManager.dbManager
    lateinit var sqlManager: SQLManager

}
package com.mikael.mkAPI.kotlin.objects

import com.mikael.mkAPI.kotlin.api.MKPluginData
import net.eduard.api.lib.database.annotations.ColumnPrimary
import net.eduard.api.lib.database.annotations.ColumnUnique
import net.eduard.api.lib.database.annotations.TableName
import net.eduard.api.lib.hybrid.PlayerUser

@TableName("mkapi_minigame_players")
class MinigameProfile : MKPluginData {

    @ColumnPrimary
    var id = 0L

    @ColumnUnique
    var player = PlayerUser("Mikael")

    var isOnline = false

}
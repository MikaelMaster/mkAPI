package com.mikael.mkAPI.kotlin.bungee.api

import net.eduard.api.lib.kotlin.fixColors
import net.md_5.bungee.BungeeTitle
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

fun ProxiedPlayer.sendTitle(title: String, subTitle: String, stayInfinite: Boolean = false) {
    val titlePacket = BungeeTitle()
    titlePacket.title(*TextComponent.fromLegacyText(title))
    titlePacket.subTitle(*TextComponent.fromLegacyText(subTitle))
    titlePacket.fadeIn(5)
    titlePacket.fadeOut(5)
    var timeToStay = 20 * 3
    if (stayInfinite) {
        timeToStay = Int.MAX_VALUE
    }
    titlePacket.stay(timeToStay)
    titlePacket.send(this)
}

fun String.toTextComponent(fixColors: Boolean = true): TextComponent {
    if (fixColors) {
        return TextComponent(this).fixColors() as TextComponent
    }
    return TextComponent(this)
}
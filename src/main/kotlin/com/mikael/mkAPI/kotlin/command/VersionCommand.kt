package com.mikael.mkAPI.kotlin.command

import net.eduard.api.lib.manager.CommandManager
import org.bukkit.command.CommandSender

class VersionCommand : CommandManager("mkapi") {

    init {
        usage = "/mkapi"
        permission = ""
    }

    override fun command(sender: CommandSender, args: Array<String>) {
        sender.sendMessage("§amkAPI §ev1.0 §f- §bdesenvolvido por Mikael.")
    }

}
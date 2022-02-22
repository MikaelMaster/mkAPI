package com.mikael.mkAPI.java.spigot;

import com.mikael.mkAPI.java.APIJavaUtils;
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class SpigotMain extends JavaPlugin {

    public static Boolean serverEnabled = false;
    public static Boolean mkMinigameAPIEnabled = false;
    public static Boolean mkBungeeAPIEnabled = false;

    static {
        try {
            APIJavaUtils.downloadNeedLibs("mkAPI");
        } catch (IOException ex) {
            ex.printStackTrace();
            APIJavaUtils.jarLoaderlog("§cOcorreu um erro no tratamento das libs.");
            APIJavaUtils.shutdownServer();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        SpigotMainKt.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        SpigotMainKt.INSTANCE.onDisable();
    }
}

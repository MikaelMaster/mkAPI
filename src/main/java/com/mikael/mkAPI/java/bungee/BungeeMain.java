package com.mikael.mkAPI.java.bungee;

import com.mikael.mkAPI.java.APIJavaUtils;
import com.mikael.mkAPI.kotlin.bungee.BungeeMainKt;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class BungeeMain extends Plugin {

    public static Plugin instance;

    static {
        try {
            APIJavaUtils.downloadNeedLibs("mkAPIProxy");
        } catch (IOException ex) {
            ex.printStackTrace();
            APIJavaUtils.jarLoaderlog("§cThere was an error handling the libs.");
            APIJavaUtils.shutdownServer();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
        BungeeMainKt.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        BungeeMainKt.INSTANCE.onDisable();
    }
}

package com.mikael.mkAPI.java;

import com.mikael.mkAPI.kotlin.MainKt;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class Main extends JavaPlugin {

    public static Boolean serverEnabled = false;
    static {
        try {
            Utils.log("§eIniciando download de libs...");
            File folder = new File("plugins/mkAPI/libs");
            folder.mkdirs();
            Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-1.6.10.jar");
            Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.6.10/kotlin-stdlib-common-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-common-1.6.10.jar");
            Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.6.10/kotlin-stdlib-jdk8-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-jdk8-1.6.10.jar");
            Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.6.10/kotlin-stdlib-jdk7-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-jdk7-1.6.10.jar");
            Utils.log("§aDownload de libs finalizado!");
            Utils.loadLibraries(folder);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.log("§cOcorreu um erro no tratamento das libs. :c");
            Bukkit.shutdown();
        }
        serverEnabled = true;
    }

    @Override
    public void onEnable() {
        MainKt.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        MainKt.INSTANCE.onDisable();
    }

    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage("§b[mkAPI] §f" + msg);
    }
}

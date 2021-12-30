package com.mikael.mkAPI.java;

import com.mikael.mkAPI.kotlin.MainKt;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;

public class Main extends JavaPlugin {

    public static Boolean serverEnabled = false;

    static {
        try {
            File folder = new File("plugins/mkAPI/libs");
            folder.mkdirs();
            File ktLib1 = new File("plugins/mkAPI/libs/kotlin-stdlib-1.6.10.jar");
            File ktLib2 = new File("plugins/mkAPI/libs/kotlin-stdlib-common-1.6.10.jar");
            File ktLib3 = new File("plugins/mkAPI/libs/kotlin-stdlib-jdk8-1.6.10.jar");
            File ktLib4 = new File("plugins/mkAPI/libs/kotlin-stdlib-jdk7-1.6.10.jar");
            if (ktLib1.exists() && ktLib2.exists() && ktLib3.exists() && ktLib4.exists()) {
                Utils.jarLoaderlog("§aTodas as libs já estão em disco.");
            } else {
                Utils.jarLoaderlog("§cOps, as libs não estão em disco. :c");
                Utils.jarLoaderlog("§eIniciando download de libs...");
                Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-1.6.10.jar");
                Utils.jarLoaderlog("§625% concluído(s).");
                Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.6.10/kotlin-stdlib-common-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-common-1.6.10.jar");
                Utils.jarLoaderlog("§650% concluído(s).");
                Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.6.10/kotlin-stdlib-jdk8-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-jdk8-1.6.10.jar");
                Utils.jarLoaderlog("§675% concluído(s).");
                Utils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.6.10/kotlin-stdlib-jdk7-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-jdk7-1.6.10.jar");
                Utils.jarLoaderlog("§6100% concluído(s).");
                Utils.jarLoaderlog("§aDownload de libs finalizado!");
            }
            Utils.loadLibraries(folder);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.jarLoaderlog("§cOcorreu um erro no tratamento das libs. :c");
            Bukkit.shutdown();
        }
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

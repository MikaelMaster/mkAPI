package com.mikael.mkAPI.java.spigot;

import com.mikael.mkAPI.java.APIJavaUtils;
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class SpigotMain extends JavaPlugin {

    public static Boolean serverEnabled = false;
    public static Boolean mkMinigameAPIEnabled = false;

    static {
        try {
            File folder = new File("plugins/mkAPI/libs");
            folder.mkdirs();
            File ktLib1 = new File("plugins/mkAPI/libs/kotlin-stdlib-1.6.10.jar");
            File ktLib2 = new File("plugins/mkAPI/libs/kotlin-stdlib-common-1.6.10.jar");
            File ktLib3 = new File("plugins/mkAPI/libs/kotlin-stdlib-jdk8-1.6.10.jar");
            File ktLib4 = new File("plugins/mkAPI/libs/kotlin-stdlib-jdk7-1.6.10.jar");
            if (ktLib1.exists() && ktLib2.exists() && ktLib3.exists() && ktLib4.exists()) {
                APIJavaUtils.jarLoaderlog("§aTodas as libs já estão em disco.");
            } else {
                APIJavaUtils.jarLoaderlog("§cOps, as libs não estão em disco. :c");
                APIJavaUtils.jarLoaderlog("§eIniciando download de libs...");
                APIJavaUtils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-1.6.10.jar");
                APIJavaUtils.jarLoaderlog("§625% concluído(s).");
                APIJavaUtils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.6.10/kotlin-stdlib-common-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-common-1.6.10.jar");
                APIJavaUtils.jarLoaderlog("§650% concluído(s).");
                APIJavaUtils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.6.10/kotlin-stdlib-jdk8-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-jdk8-1.6.10.jar");
                APIJavaUtils.jarLoaderlog("§675% concluído(s).");
                APIJavaUtils.downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.6.10/kotlin-stdlib-jdk7-1.6.10.jar", "plugins/mkAPI/libs/kotlin-stdlib-jdk7-1.6.10.jar");
                APIJavaUtils.jarLoaderlog("§6100% concluído(s).");
                APIJavaUtils.jarLoaderlog("§aDownload de libs finalizado!");
            }
            APIJavaUtils.loadLibraries(folder);
        } catch (IOException ex) {
            ex.printStackTrace();
            APIJavaUtils.jarLoaderlog("§cOcorreu um erro no tratamento das libs. :c");
            APIJavaUtils.shutdownServer();
        }
    }

    @Override
    public void onEnable() {
        SpigotMainKt.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        SpigotMainKt.INSTANCE.onDisable();
    }
}

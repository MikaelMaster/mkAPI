package com.mikael.mkAPI.java.bungee;

import com.mikael.mkAPI.java.APIJavaUtils;
import com.mikael.mkAPI.kotlin.bungee.BungeeMainKt;
import net.md_5.bungee.api.plugin.Plugin;
import java.io.File;
import java.io.IOException;

public class BungeeMain extends Plugin {

    static {
        try {
            File folder = new File("plugins/mkAPIProxy/libs");
            folder.mkdirs();
            File ktLib1 = new File("plugins/mkAPIProxy/libs/kotlin-stdlib-1.6.10.jar");
            File ktLib2 = new File("plugins/mkAPIProxy/libs/kotlin-stdlib-common-1.6.10.jar");
            File ktLib3 = new File("plugins/mkAPIProxy/libs/kotlin-stdlib-jdk8-1.6.10.jar");
            File ktLib4 = new File("plugins/mkAPIProxy/libs/kotlin-stdlib-jdk7-1.6.10.jar");
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
        BungeeMainKt.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        BungeeMainKt.INSTANCE.onDisable();
    }
}

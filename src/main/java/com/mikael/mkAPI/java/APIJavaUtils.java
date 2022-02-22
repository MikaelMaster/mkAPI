package com.mikael.mkAPI.java;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Objects;

public class APIJavaUtils {

    public static void fastLog(String msg) {
        if (isProxyServer()) {
            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent("§b[mkAPIProxy] §f" + msg));
        } else {
            Bukkit.getConsoleSender().sendMessage("§b[mkAPI] §f" + msg);
        }
    }

    public static boolean isProxyServer() {
        try {
            ProxyServer.getInstance();
            return true;
        } catch (Error e) {
            return false;
        }
    }

    public static void shutdownServer() {
        if (isProxyServer()) {
            ProxyServer.getInstance().stop();
        } else {
            Bukkit.shutdown();
        }
    }

    private static String getCompletedPercentage(Double completeAmount, Double totalAmount) {
        double percentage = (completeAmount / totalAmount) * 100;
        int intValue = (int) Math.round(percentage);
        return intValue + "%";
    }

    public static void downloadNeedLibs(String pluginFolder) throws IOException {
        File folder = new File("plugins/" + pluginFolder + "/libs");
        folder.mkdirs();
        File ktLib1 = new File("plugins/" + pluginFolder + "/libs/kotlin-stdlib-1.6.10.jar");
        File ktLib2 = new File("plugins/" + pluginFolder + "/libs/kotlin-stdlib-common-1.6.10.jar");
        File ktLib3 = new File("plugins/" + pluginFolder + "/libs/kotlin-stdlib-jdk8-1.6.10.jar");
        File ktLib4 = new File("plugins/" + pluginFolder + "/libs/kotlin-stdlib-jdk7-1.6.10.jar");
        File jedisLib = new File("plugins/" + pluginFolder + "/libs/jedis-4.1.1.jar");
        File slf4jApiLib = new File("plugins/" + pluginFolder + "/libs/slf4j-api-1.7.32.jar");
        File slf4jSimpleLib = new File("plugins/" + pluginFolder + "/libs/slf4j-simple-1.7.32.jar");
        if (ktLib1.exists() && ktLib2.exists() && ktLib3.exists() && ktLib4.exists() && jedisLib.exists() && slf4jApiLib.exists() && slf4jSimpleLib.exists()) {
            jarLoaderlog("§aTodas as libs já estão em disco.");
        } else {
            jarLoaderlog("§cOps, nem todas libs estão em disco.");
            jarLoaderlog("§eIniciando download de libs...");
            jarLoaderlog("§6" + getCompletedPercentage(0.0, 7.0) + " concluído(s).");
            downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "plugins/" + pluginFolder + "/libs/kotlin-stdlib-1.6.10.jar");
            jarLoaderlog("§6" + getCompletedPercentage(1.0, 7.0) + " concluído(s).");
            downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.6.10/kotlin-stdlib-common-1.6.10.jar", "plugins/" + pluginFolder + "/libs/kotlin-stdlib-common-1.6.10.jar");
            jarLoaderlog("§6" + getCompletedPercentage(2.0, 7.0) + " concluído(s).");
            downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.6.10/kotlin-stdlib-jdk8-1.6.10.jar", "plugins/" + pluginFolder + "/libs/kotlin-stdlib-jdk8-1.6.10.jar");
            jarLoaderlog("§6" + getCompletedPercentage(3.0, 7.0) + " concluído(s).");
            downloadFile("https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.6.10/kotlin-stdlib-jdk7-1.6.10.jar", "plugins/" + pluginFolder + "/libs/kotlin-stdlib-jdk7-1.6.10.jar");
            jarLoaderlog("§6" + getCompletedPercentage(4.0, 7.0) + " concluído(s).");
            downloadFile("https://repo1.maven.org/maven2/redis/clients/jedis/4.1.1/jedis-4.1.1.jar", "plugins/" + pluginFolder + "/libs/jedis-4.1.1.jar");
            jarLoaderlog("§6" + getCompletedPercentage(5.0, 7.0) + " concluído(s).");
            downloadFile("https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar", "plugins/" + pluginFolder + "/libs/slf4j-api-1.7.32.jar");
            jarLoaderlog("§6" + getCompletedPercentage(6.0, 7.0) + " concluído(s).");
            downloadFile("https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.32/slf4j-simple-1.7.32.jar", "plugins/" + pluginFolder + "/libs/slf4j-simple-1.7.32.jar");
            jarLoaderlog("§6" + getCompletedPercentage(7.0, 7.0) + " concluído(s).");
            jarLoaderlog("§aDownload de libs finalizado!");
        }
        jarLoaderlog("§eIniciando carregamento de libs...");
        loadLibraries(folder);
    }

    public static void downloadFile(String fromUrl, String localFileName) throws IOException {
        File localFile = new File(localFileName);
        if (localFile.exists()) {
            localFile.delete();
        }
        localFile.createNewFile();
        URL url = new URL(fromUrl);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(localFileName));
        URLConnection conn = url.openConnection();
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[1024 * 8];
        int numRead;
        while ((numRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, numRead);
        }
        in.close();
        out.close();
    }

    public static void loadLibraries(File libFile) {
        libFile.mkdirs();
        for (File file : Objects.requireNonNull(libFile.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                try {
                    if (!addClassPath(file)) {
                        throw new IllegalStateException("Cannot load file " + libFile.getName());
                    }
                } catch (Exception e) {
                    jarLoaderlog("§cOcorreu um erro ao carregar o jar " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        jarLoaderlog("§aCarregamento de libs finalizado!");
    }

    private static boolean addClassPath(final File file) throws Exception {
        URL url = new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
        final Object systemClassLoader = ClassLoader
                .getSystemClassLoader();
        final Method method = URLClassLoader.class
                .getDeclaredMethod("addURL",
                        URL.class);
        method.setAccessible(true);
        if (URLClassLoader.class.isAssignableFrom(systemClassLoader.getClass())) {
            method.invoke(systemClassLoader, url);
            return true;
        } else {
            jarLoaderlog("§cO Java instalado não é compatível com o carregamento das libs.");
            return false;
        }
    }

    public static void jarLoaderlog(String msg) {
        if (isProxyServer()) {
            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent("§b[mkAPIProxy JarLoader] §f" + msg));
        } else {
            Bukkit.getConsoleSender().sendMessage("§b[mkAPI JarLoader] §f" + msg);
        }
    }

}

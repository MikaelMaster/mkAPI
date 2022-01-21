package com.mikael.mkAPI.java;

import net.md_5.bungee.api.ProxyServer;
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
            ProxyServer.getInstance().getConsole().sendMessage("§b[mkAPIProxy] §f" + msg);
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

    public static void downloadFile(String fromUrl, String localFileName) throws IOException {
        File localFile = new File(localFileName);
        if (localFile.exists()) {
            return;
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
        jarLoaderlog("§eIniciando carregamento de libs...");
        for (File file : Objects.requireNonNull(libFile.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                try {
                    addClassPath(file);
                } catch (Exception e) {
                    jarLoaderlog("§cOcorreu um erro ao carregar a jar " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        jarLoaderlog("§aCarregamento de libs finalizado!");
    }

    private static void addClassPath(final File file) throws Exception {
        URL url = new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
        final Object systemClassLoader = ClassLoader
                .getSystemClassLoader();
        final Method method = URLClassLoader.class
                .getDeclaredMethod("addURL",
                        URL.class);
        method.setAccessible(true);
        if (URLClassLoader.class.isAssignableFrom(systemClassLoader.getClass())) {
            method.invoke(systemClassLoader, url);
        } else {
            jarLoaderlog("§cO Java instalado não é compatível com o carregamento das libs.");
        }
    }

    public static void jarLoaderlog(String msg) {
        Bukkit.getConsoleSender().sendMessage("§b[mkAPI JarLoader] §f" + msg);
    }

}

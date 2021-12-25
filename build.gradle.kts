plugins {
    java
    kotlin("jvm") version "1.4.21"
    `maven-publish`
}

group = "com.mikael"
version = "1.0"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
    maven("https://m2.dv8tion.net/releases")
    // maven("https://mvnrepository.com/artifact/me.lucko.luckperms/luckperms-api")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(files("libs/EduardAPI-1.0-all.jar"))
    // compileOnly("org.bukkit:spigot:1.8.9")
    // compileOnly("me.lucko.luckperms:luckperms-api:4.4")
}

tasks {
    jar {
        destinationDirectory
            .set(file("D:\\MK Plugins"))
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
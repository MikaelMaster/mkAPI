plugins {
    java
    kotlin("jvm") version "1.4.21"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.mikael"
version = "1.3"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly(files("libs/spigot-1.8.8.jar"))
    compileOnly(files("libs/BungeeCord.jar"))
    compileOnly(files("libs/EduardAPI-1.0-all.jar"))
    api(files("libs/EduardAPI-1.0-all.jar"))
}

tasks {
    jar {
        destinationDirectory
            .set(file("D:\\MK Plugins\\"))
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
    shadowJar {
        archiveVersion.set("1.3")
        archiveBaseName.set("mkAPI")
        destinationDirectory.set(
            file("D:\\Servidor para testes\\plugins\\")
        )
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

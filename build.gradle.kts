import kr.entree.spigradle.kotlin.luckPerms
import kr.entree.spigradle.kotlin.paper
import kr.entree.spigradle.kotlin.papermc
import kr.entree.spigradle.kotlin.protocolLib

apply(plugin = "maven-publish")
apply(plugin = "java-library")

plugins {
    `maven-publish`
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2" apply true
    id("io.papermc.paperweight.userdev") version "1.4.1"
    id("kr.entree.spigradle") version "2.4.3"
}

spigot {
    website = "https://github.com/AetherFamily"
    authors = listOf("Magnetite", "Willow")
    apiVersion = "1.19"
    softDepends = listOf("ProtocolLib")
    name = "AetherLib"
}

version = "1.1"
description = "A library for all Aether related plugins"
java.sourceCompatibility = JavaVersion.VERSION_17
group = "com.github.aetherfamily"

repositories {
    mavenLocal()
    papermc()
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    compileOnly(luckPerms("5.4"))
    compileOnly(protocolLib("4.8.0"))
    compileOnly(paper("1.19.3-R0.1-SNAPSHOT"))
}

tasks {
    assemble {
        reobfJar
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        options.compilerArgs.add("-parameters")
        jar.get().enabled = false
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        relocate("org.bstats", "com.github.aetherfamily.aetherlib")
    }
}

project.tasks.assemble.get().dependsOn(tasks.shadowJar)
project.tasks.assemble.get().dependsOn(tasks.reobfJar)


publishing {
    publications {
        register<MavenPublication>("jitpack") {
            artifact(tasks.reobfJar) {
                classifier = "";
            }
        }
    }
}


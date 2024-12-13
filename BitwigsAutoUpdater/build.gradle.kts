plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.21"
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

group = "dev.devoirr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.shadowJar {
    dependencies {
        exclude(dependency("org.jetbrains:annotations"))
        exclude(dependency("net.kyori:adventure-api"))
        exclude(dependency("net.kyori:adventure-text-serializer-legacy"))
    }
    archiveFileName.set("BitwigsAutoUpdater.jar")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

tasks.reobfJar {
    outputJar = layout.buildDirectory.file("libs/BitwigsAutoUpdater.jar")
}
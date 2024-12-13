plugins {
    `java-library`
    kotlin("jvm") version "2.0.21"
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

group = "dev.devoirr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

tasks.reobfJar {
    outputJar = layout.buildDirectory.file("libs/Bitwigs.jar")
}
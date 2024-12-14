plugins {
    `java-library`
    kotlin("jvm") version "2.0.21"
    id("io.papermc.paperweight.userdev") version "1.7.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.devoirr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

tasks.shadowJar {
    relocate("co.aikar.commands", "dev.devoirr.bitwigs.acf")
    relocate("co.aikar.locales", "dev.devoirr.bitwigs.locales")
}

tasks.compileKotlin {
    compilerOptions.javaParameters = true
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.forkOptions.executable = System.getProperty("java.home") + "/bin/javac"
}

tasks.reobfJar {
    outputJar = layout.buildDirectory.file("libs/Bitwigs.jar")
}
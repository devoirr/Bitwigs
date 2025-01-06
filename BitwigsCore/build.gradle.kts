plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "dev.devoirr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.21-R0.1-SNAPSHOT")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("com.github.retrooper:packetevents-spigot:2.7.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

tasks.shadowJar {
    dependencies {
        exclude(dependency("org.jetbrains:annotations"))
        exclude(dependency("net.kyori:adventure-api"))
        exclude(dependency("net.kyori:adventure-text-serializer-legacy"))
    }

    relocate("co.aikar.commands", "dev.devoirr.bitwigs.acf")
//    relocate("co.aikar.locales", "dev.devoirr.bitwigs.locales")

    archiveFileName.set("Bitwigs.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
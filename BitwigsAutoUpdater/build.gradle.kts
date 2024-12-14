plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.21"
}

group = "dev.devoirr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
}

tasks.shadowJar {
    dependencies {
        exclude(dependency("org.jetbrains:annotations"))
        exclude(dependency("net.kyori:adventure-api"))
        exclude(dependency("net.kyori:adventure-text-serializer-legacy"))
    }
    archiveFileName.set("BitwigsAutoUpdater.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.21-R0.1-SNAPSHOT")
    implementation("commons-io:commons-io:2.18.0")
}

kotlin {
    jvmToolchain(21)
}

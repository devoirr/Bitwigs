plugins {
    kotlin("jvm") version "2.0.21"
}

group = "dev.devoirr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
}

dependencies {
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.21-R0.1-SNAPSHOT")
}

tasks.build {
    base.archivesName = "Bitwigs"
}

kotlin {
    jvmToolchain(21)
}
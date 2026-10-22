plugins {
    kotlin("jvm") version "2.2.20"
}

group = "dk.maxkandersen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.ejml:ejml-all:0.46.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}
plugins {
    kotlin("jvm") version "2.2.20"
    application
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

application {
    mainClass.set("dk.maxkandersen.MainKt")
}

tasks.withType<JavaExec>().configureEach {
    standardInput = System.`in`
}

// Make the default jar a self-contained, runnable fat jar:
// Main-Class manifest + all runtime dependencies unpacked into it.
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to "dk.maxkandersen.MainKt")
    }
    from({ configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } })
}
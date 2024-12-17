plugins {
    id("java")
    id("application")
    kotlin("jvm") version "2.0.20"
}

group = "com.volodya"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "com.volodya.MainKt"
}

dependencies {
    implementation("org.jfree:jfreechart:1.5.4")
}

kotlin {
    jvmToolchain(21)
}
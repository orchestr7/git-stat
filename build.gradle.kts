import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.akuleshov7"
version = "0.1.0"

val kotlinVersion = "1.4.21"
val ktorVersion = "1.5.0"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

plugins {
    id("com.github.ben-manes.versions") version "0.38.0"
    java
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    application
    id("org.cqfn.diktat.diktat-gradle-plugin") version "0.4.2"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-auth-basic:$ktorVersion")
    implementation("io.github.microutils:kotlin-logging:1.12.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.akuleshov7.stat.StatMainKt")
}

diktat {
    debug = true
    inputs = files("src/**/*.kt")
}

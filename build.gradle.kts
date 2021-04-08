import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.akuleshov7"
version = "0.1.0"

val kotlinVersion = "1.4.31"
val ktorVersion = "1.5.2"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    application
    id("org.cqfn.diktat.diktat-gradle-plugin") version "0.4.2"
    id("com.github.ben-manes.versions") version "0.38.0"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
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

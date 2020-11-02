import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.akuleshov7"
version = "0.1.0"

val kotlinVersion = "1.4.10"
val ktorVersion = "1.4.1"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

plugins {
    java
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    application
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks
compileKotlin.run {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin.run {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
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

// code style
val ktlint by configurations.creating {
    // temporary enable maven local to run diktat snapshot
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
dependencies {
    ktlint("com.pinterest:ktlint:0.39.0") {
        exclude("com.pinterest.ktlint", "ktlint-ruleset-standard")
    }
    ktlint("org.cqfn.diktat:diktat-rules:0.1.3")
}
task<JavaExec>("diktat") {
    setGroup("verification")
    setDescription("Check Kotlin code style.")
    mainClass.set("com.pinterest.ktlint.Main")
    classpath = ktlint
    args("src/**/*.kt")
}

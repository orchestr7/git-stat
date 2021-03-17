import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

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
    kotlin("multiplatform") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    // application
    id("org.cqfn.diktat.diktat-gradle-plugin") version "0.4.2"
    id("com.github.ben-manes.versions") version "0.38.0"
}

kotlin {
    jvm()
    val os = getCurrentOperatingSystem()
    // Create a target for the host platform.
    val hostTarget = when {
        os.isLinux -> linuxX64(project.name)
        os.isWindows -> mingwX64(project.name)
        os.isMacOsX -> macosX64(project.name)
        else -> throw GradleException("Host OS '${os.name}' is not supported in Kotlin/Native $project.")
    }
    configure(listOf(hostTarget)) {
        binaries.executable {
            entryPoint = "org.akuleshov7.stat.main"
        }
    }
    sourceSets {
        // dependencies can be shared among JVM and native, but not for all other targets
        // so cannot use `common` here
        val applicationMain by creating {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-auth-basic:$ktorVersion")
            }
        }
        val applicationTest by creating {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        getByName("${hostTarget.name}Main").dependsOn(applicationMain)
        getByName("${hostTarget.name}Main").dependencies {
            implementation("io.ktor:ktor-client-curl:$ktorVersion")
        }
        getByName("${hostTarget.name}Test").dependsOn(applicationTest)
        val jvmMain by getting {
            dependsOn(applicationMain)
            dependencies {
                implementation("io.ktor:ktor-client-apache:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependsOn(applicationTest)
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
            }
        }
    }

}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<KotlinJvmTest> {
    useJUnitPlatform()
}

diktat {
    inputs = files("src/**/*.kt")
}

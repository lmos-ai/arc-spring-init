/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

dependencies {
    val kotlinXVersion = "1.8.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // ktor
    val ktorVersion = "2.3.10"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
}

repositories {
    mavenLocal()
    mavenCentral()
}

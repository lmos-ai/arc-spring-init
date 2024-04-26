/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */
import arc.ArcChatTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.lang.System.getenv
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        freeCompilerArgs += "-Xcontext-receivers"
        jvmTarget = "17"
    }
}

dependencies {
    val arcVersion = "0.19.0"
    kotlinScriptDef("io.github.lmos-ai.arc:arc-scripting:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-scripting:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-azure-client:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-ollama-client:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-gemini-client:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-spring-boot-starter:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-reader-pdf:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-reader-html:$arcVersion")

    val kotlinXVersion = "1.8.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "github"
        url = URI("https://maven.pkg.github.com/lmos-ai/arc")
        credentials {
            username = findProperty("GITHUB_USER")?.toString() ?: getenv("GITHUB_USER")
            password = findProperty("GITHUB_TOKEN")?.toString() ?: getenv("GITHUB_TOKEN")
        }
    }
}

tasks.register<ArcChatTask>("arc") {
    agent = findProperty("agent")?.toString() ?: error("The property 'agent' is missing!")
}

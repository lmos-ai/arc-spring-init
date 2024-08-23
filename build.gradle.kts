// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.2"
}

version = "1.0.0"
group = "io.github.lmos-ai.arc.init"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xcontext-receivers")
    }
}

dependencies {
    val arcVersion = "0.70.0"
    val kotlinXVersion = "1.8.0"
    val kotlinSerialization = "1.7.1"

    kotlinScriptDef("io.github.lmos-ai.arc:arc-scripting:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-scripting:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-azure-client:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-ollama-client:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-gemini-client:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-spring-boot-starter:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-reader-pdf:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-reader-html:$arcVersion")
    implementation("io.github.lmos-ai.arc:arc-graphql-spring-boot-starter:$arcVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-server:7.1.4")
    implementation("com.graphql-java:graphql-java:21.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinXVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerialization")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Test
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:mongodb:1.19.7")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

repositories {
    mavenLocal()
    mavenCentral()
}

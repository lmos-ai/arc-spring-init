// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.2"
    // id("ai.ancf.lmos.arc.gradle.plugin") version "0.110.0"
}

version = "1.0.0"
group = "ai.ancf.lmos.app"

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
    val arcVersion = "0.116.0"
    val langchain4jVersion = "0.36.2"

    // Arc
    implementation("ai.ancf.lmos:arc-azure-client:$arcVersion")
    implementation("ai.ancf.lmos:arc-spring-boot-starter:$arcVersion")
    implementation("ai.ancf.lmos:arc-reader-pdf:$arcVersion")
    implementation("ai.ancf.lmos:arc-reader-html:$arcVersion")
    implementation("ai.ancf.lmos:arc-assistants:$arcVersion")
    implementation("ai.ancf.lmos:arc-reader-html:$arcVersion")
    implementation("ai.ancf.lmos:arc-api:$arcVersion")
    implementation("ai.ancf.lmos:arc-graphql-spring-boot-starter:$arcVersion")

    // Azure
    implementation("com.azure:azure-identity:1.13.1")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Langchain4j
    implementation("dev.langchain4j:langchain4j-bedrock:$langchain4jVersion")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:$langchain4jVersion")
    implementation("dev.langchain4j:langchain4j-ollama:$langchain4jVersion")

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

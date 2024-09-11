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
    id("org.jreleaser") version "1.14.0"
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
    val arcVersion = "0.73.0"

    implementation("ai.ancf.lmos:arc-scripting:$arcVersion")
    implementation("ai.ancf.lmos:arc-azure-client:$arcVersion")
    implementation("com.azure:azure-identity:1.13.1")
    implementation("ai.ancf.lmos:arc-ollama-client:$arcVersion")
    implementation("ai.ancf.lmos:arc-gemini-client:$arcVersion")
    implementation("ai.ancf.lmos:arc-spring-boot-starter:$arcVersion")
    implementation("ai.ancf.lmos:arc-reader-pdf:$arcVersion")
    implementation("ai.ancf.lmos:arc-reader-html:$arcVersion")
    implementation("ai.ancf.lmos:arc-graphql-spring-boot-starter:$arcVersion")
    implementation("com.graphql-java:graphql-java:21.5") // Workaround for java.lang.NoSuchMethodError

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

// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.JavadocJar

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.2"
    id("com.vanniktech.maven.publish") version "0.29.0"
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name.set("My Library")
        description.set("A description of what my library does.")
        inceptionYear.set("2024")
        url.set("https://github.com/lmos-ai/arc-spring-init/")
        licenses {
            license {
                name = "Apache-2.0"
                distribution = "repo"
                url = "https://github.com/lmos-ai/arc/blob/main/LICENSES/Apache-2.0.txt"
            }
        }
        developers {
            developer {
                id = "opensource@telekom.de"
                name = "opensource@telekom.de"
                email = "opensource@telekom.de"
            }
        }
        scm {
            url.set("https://github.com/lmos-ai/arc-spring-init/")
            connection.set("scm:git:git://github.com/lmos-ai/arc-spring-init.git")
            developerConnection.set("scm:git:ssh://git@github.com/lmos-ai/arc-spring-init.git")
        }
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

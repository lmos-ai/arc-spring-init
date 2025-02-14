// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0


package org.eclipse.lmos.arc.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.RouterFunctions

/**
 * Simple Spring Boot application that demonstrates how to use the Arc Agents.
 */
@SpringBootApplication
class ArcAIApplication {

    /**
     * Enable ArcView.
     */
    @Bean
    fun chatResourceRouter() = RouterFunctions.resources("/chat/**", ClassPathResource("chat/"))
}

fun main(args: Array<String>) {
    runApplication<ArcAIApplication>(*args)
}

// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0


package ai.ancf.lmos.arc.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Simple Spring Boot application that demonstrates how to use the Arc Agents.
 */
@SpringBootApplication
class ArcAIApplication

fun main(args: Array<String>) {
    runApplication<ArcAIApplication>(*args)
}

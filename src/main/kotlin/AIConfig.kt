// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0


package io.github.lmos.arc.runner

import org.springframework.boot.context.properties.ConfigurationProperties


/**
 * Simple setup for loading configuration properties.
 */
@ConfigurationProperties(prefix = "ai")
data class AIConfig(val clients: List<AIClientConfig>)

data class AIClientConfig(
    val client: String,
    val modelName: String,
    val url: String,
    val apiKey: String? = null
)

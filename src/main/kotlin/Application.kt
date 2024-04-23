/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */

package io.github.lmos.arc.runner

import com.azure.ai.openai.OpenAIAsyncClient
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.core.credential.KeyCredential
import io.github.lmos.arc.agents.events.EventPublisher
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompleterProvider
import io.github.lmos.arc.client.azure.AzureAIClient
import io.github.lmos.arc.client.azure.AzureClientConfig
import io.github.lmos.arc.client.ollama.OllamaClient
import io.github.lmos.arc.client.ollama.OllamaClientConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

/**
 * Simple Spring Boot application that demonstrates how to use the Arc Agents.
 */
@SpringBootApplication
@EnableConfigurationProperties(AIConfig::class)
class DemoApplication {

    /**
     * The Azure OpenAI client setup to connect to the OpenAI API.
     * See https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/openai/azure-ai-openai#support-for-non-azure-openai
     */
    fun openAIAsyncClient(config: AzureClientConfig): OpenAIAsyncClient {
        return OpenAIClientBuilder()
            .credential(KeyCredential(config.apiKey))
            .buildAsyncClient()
    }

    @Bean
    fun aiClient(config: AIConfig, eventPublisher: EventPublisher) = config.clients.associate {
        it.modelName to when (it.client) {
            "ollama" -> OllamaClient(OllamaClientConfig(it.modelName, it.url), eventPublisher)
            else -> AzureAIClient(
                AzureClientConfig(it.modelName, it.url, it.apiKey ?: ""),
                openAIAsyncClient(AzureClientConfig(it.modelName, it.url, it.apiKey ?: "")),
                eventPublisher
            )
        }
    }

    @Bean
    fun chatCompleterProvider(clients: Map<String, ChatCompleter>) =
        ChatCompleterProvider { model -> model?.let { clients[it] } ?: clients.values.first() }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

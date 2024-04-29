/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */

package io.github.lmos.arc.runner

import io.github.lmos.arc.agents.AgentFailedException
import io.github.lmos.arc.agents.AgentProvider
import io.github.lmos.arc.agents.AuthenticationException
import io.github.lmos.arc.agents.ChatAgent
import io.github.lmos.arc.agents.User
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.Conversation
import io.github.lmos.arc.agents.conversation.UserMessage
import io.github.lmos.arc.agents.conversation.latest
import io.github.lmos.arc.agents.getAgentByName
import io.github.lmos.arc.core.getOrThrow
import kotlinx.serialization.Serializable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.atomic.AtomicReference

/**
 * Simple example of a REST endpoint that communicates with Agent.
 */
@RestController
@CrossOrigin(
    origins = ["*"],
    allowedHeaders = ["*"],
    exposedHeaders = ["*"],
    methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS],
)
class AgentController(
    private val agentProvider: AgentProvider
) {

    private val conversation = AtomicReference(Conversation(User("anonymous")))

    @PostMapping("/chat/{agentName}")
    suspend fun chat(
        @RequestBody input: AgentInput,
        @RequestParam(name = "user", required = false) userId: String? = null,
        @PathVariable agentName: String,
    ): AgentResponse {
        val agent = agentProvider.getAgentByName(agentName) as ChatAgent? ?: throw ResponseStatusException(
            NOT_FOUND,
            "Agent not found!"
        )
        val updatedConversation = conversation.get() + UserMessage(input.message)
        val result = agent.execute(updatedConversation).getOrThrow()
        conversation.set(result)
        return AgentResponse(result.latest<AssistantMessage>()?.content ?: "")
    }

    @DeleteMapping("/chat")
    suspend fun resetChat() {
        conversation.set(Conversation(user = User("anonymous")))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<String> {
        return when (ex) {
            is AuthenticationException -> ResponseEntity("Validate API-KEY!", HttpHeaders(), UNAUTHORIZED)
            is AgentFailedException -> ResponseEntity(ex.cause?.message, HttpHeaders(), BAD_REQUEST)
            else -> ResponseEntity(ex.message, INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class AgentResponse(val result: String)

@Serializable
data class AgentInput(val message: String)

// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.app.tools

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.eclipse.lmos.arc.agents.FunctionNotFoundException
import org.eclipse.lmos.arc.agents.functions.LLMFunction
import org.eclipse.lmos.arc.agents.functions.LLMFunctionException
import org.eclipse.lmos.arc.agents.functions.LLMFunctionProvider
import org.eclipse.lmos.arc.agents.functions.ParametersSchema
import org.eclipse.lmos.arc.api.AgentRequest
import org.eclipse.lmos.arc.core.failWith
import org.eclipse.lmos.arc.core.result
import org.eclipse.lmos.arc.graphql.ContextHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Enables the injection of functions from the Arc View.
 * In the Arc View LLMFunctions can be defined that return a static value.
 * The ContextHandler is used to intercept the request
 * and inject the functions into the context via a custom LLMFunctionProvider.
 */
@Component
class AgentContextHandler(private val functionProvider: LLMFunctionProvider) : ContextHandler {

    override suspend fun <T> inject(request: AgentRequest, block: suspend (Set<Any>) -> T): T {
        return block(setOf(RequestFunctionProvider(request, functionProvider)))
    }
}

class RequestFunctionProvider(private val request: AgentRequest, private val functionProvider: LLMFunctionProvider) :
    LLMFunctionProvider {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun provide(functionName: String) = result<LLMFunction, FunctionNotFoundException> {
        provideAll().firstOrNull { it.name == functionName }
            ?: failWith { FunctionNotFoundException(functionName) }
    }

    override fun provideAll(): List<LLMFunction> {
        log.info("Providing functions")
        return request.systemContext.filter { it.key.startsWith("function") }.mapNotNull {
            log.info("Function: ${it.key}")
            try {
                val fn = Json.parseToJsonElement(it.value).jsonObject
                object : LLMFunction {
                    override val name: String = fn["name"]!!.jsonPrimitive.content
                    override val description: String = fn["description"]!!.jsonPrimitive.content
                    override val group: String? = null
                    override val isSensitive: Boolean = false
                    override val parameters: ParametersSchema = ParametersSchema(emptyList(), emptyList())
                    override suspend fun execute(input: Map<String, Any?>) = result<String, LLMFunctionException> {
                        fn["value"]!!.jsonPrimitive.content
                    }
                }
            } catch (e: Exception) {
                log.error("Error parsing function: ${it.key}", e)
                null
            }
        } + functionProvider.provideAll()
    }
}

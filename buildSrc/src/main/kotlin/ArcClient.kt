/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */
package arc

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture


class ArcClient(private val url: String) {
    private val httpClient: HttpClient = HttpClient.newHttpClient()

    private val json = Json

    fun ask(agent: String, question: String?): CompletableFuture<AgentResponse> {
        val requestUri: URI = URI.create("$url/chat/$agent")

        val request: HttpRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(json.encodeToString(AgentInput(question ?: ""))))
            .uri(requestUri)
            .header("Content-Type", "application/json")
            .build()

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply { response ->
                if (response.statusCode() in 200..299) {
                    return@thenApply json.decodeFromString(response.body())
                } else {
                    return@thenApply AgentResponse(response.body())
                }
            }
    }

    fun clearChat(): CompletableFuture<HttpResponse<String>>? {
        val requestUri: URI = URI.create("$url/chat")

        val request: HttpRequest = HttpRequest.newBuilder()
            .DELETE()
            .uri(requestUri)
            .header("Content-Type", "application/json")
            .build()

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    }
}

@Serializable
data class AgentResponse(val result: String)

@Serializable
data class AgentInput(val message: String)
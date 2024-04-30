// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package arc

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.util.*

open class ArcChatTask : DefaultTask() {

    @get:Input
    lateinit var agent: String

    @TaskAction
    fun chat() {
        println("\u001B[35m*******************")
        println("Welcome to ARC")
        println("*******************\u001B[0m")
        println("How can i help you today? (Type 'exit' to quit)")
        val client = ArcClient("http://localhost:8080")
        val scanner = Scanner(System.`in`)
        client.clearChat()?.get()
        while (true) {
            print("\u001B[32mAgent>> ")
            val input = scanner.nextLine()
            if (input == "exit") {
                break
            }
            val response = client.ask(agent, input)
            println("${response.get().result}\u001B[0m")
        }
    }
}
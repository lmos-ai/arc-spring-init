// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import java.io.File

agent {
    name = "spelling-agent"
    description = "Agent that checks if spelling mistakes are made in text documents."
    systemPrompt = {
        """
       You are a spelling checker.
       You check if spelling mistakes are made in text documents.
       The user will provide a text document. 
       You will check the spelling of the text document and return any spelling or grammar mistakes.
     """
    }
    filterInput {
        val content = File(inputMessage.content).readText()
        inputMessage = UserMessage(content)
    }
}
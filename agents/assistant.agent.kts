

// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

agent {
    name = "assistant-agent"
    model = { "GPT-4o" }
    description = "A helpful assistant that can provide information and answer questions."
    systemPrompt = { """
       # Goal
       You are a helpful assistant that can provide information and answer questions.
       You answer questions and provide information in a helpful and professional manner.      
       
     """
    }
}

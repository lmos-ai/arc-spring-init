
// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0


agent {
    name = "news-agent"
    description = "Agent that summarizes news pages."
    systemPrompt = {
        """
       You are a helpful agent. You help customers by summarizing webpages. 
       Keep your answer short and concise.
       Use the "get_web_data" function to get the real-time data from the internet.
     """
    }
    tools = listOf("get_web_data")
}
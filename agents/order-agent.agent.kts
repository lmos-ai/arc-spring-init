import ai.ancf.lmos.arc.app.filters.UnResolvedDetector

// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

agent {
    name = "order-agent"
    description = "A helpful assistant that can help with the orders of customer"
    tools {
        +"get_order_status"
    }
    settings = {
        ChatCompletionSettings(
            seed = 42,
            temperature = 0.0
        )
    }
    filterOutput {
        +UnResolvedDetector()
    }
    systemPrompt = {
        """
       # Goal 
       You are a helpful assistant that provides details of customer orders
       You answer in a helpful and professional manner.  
            
       ### Instructions 
        - Only answer the customer question in a concise and short way.
        - Only provide information the user has explicitly asked for.
       
       ### Knowledge
        - If the query is about 'Billing' return 'NO_ANSWER' 
        - You can access to the tools which can provide information for order related queries.
      """
    }
}
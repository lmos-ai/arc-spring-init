<!--
SPDX-FileCopyrightText: 2023 Deutsche Telekom AG

SPDX-License-Identifier: CC0-1.0    
-->
# Welcome to the Arc Agent Init Project

The following project is a demo project for the Arc Agent Framework. 
It can also be used to kickstart a new Spring Boot project that uses the Arc Agent Framework.

## How to run

#### 1. Add language model configuration

Add an OpenAI API-KEY to `config/application.yml` or as the environment variable, `OPENAI_API_KEY`.

```
arc:
  ai:
    clients:
      - id: GPT-4o
        model-name: GPT-4o
        api-key: ${OPENAI_API_KEY}
        client: openai
      - id: llama3:8b
        modelName: llama3:8b
        client: ollama
```

Alternatively, you can run an LLM locally with the `ollama` client, see https://ollama.com/,
and change the model in the agent `assistant.agent.kts` file accordingly.

```kts
agent {
    name = "assistant-agent"
    model = { "llama3:8b" }

```

#### 2. Start the Application

Start the Demo Application like a normal Spring Boot application.
This requires the port 8080 to be available.

```bash
  ./gradlew bootRun
```


#### 3. Access the Agent

You can chat with the Arc Agents using the [Arc View](https://github.com/lmos-ai/arc-view).
Simply open http://localhost:8080/chat/index.html in your browser.

Alternatively, the Graphiql interface is also available, under http://localhost:8080/graphiql?path=/graphql.

Example Request:

```graphql
subscription {
    agent(
        agentName: "assistant-agent"
        request: {
            conversationContext: {
                conversationId: "1"
            }
            systemContext: [],
            userContext: {
                userId: "1234",
                profile: [{
                    key: "name",
                    value: "Pat"
                }]
            },
            messages: [
                {
                    role: "user",
                    content: "Hi",
                    format: "text",
                }
            ]
        }
    ) {
        messages {
            content
        }
    }
}
```


#### 4. Add new Agents

New agents can be added to the `agents` folder located at the root of the project.
The folder contains a default agent `assistant-agent` that can be used as a template.


## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.1 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Licensing

This project follows the [REUSE standard for software licensing](https://reuse.software/).    
Each file contains copyright and license information, and license texts can be found in the [./LICENSES](./LICENSES) folder. For more information visit https://reuse.software/.    
You can find a guide for developers at https://telekom.github.io/reuse-template/.   
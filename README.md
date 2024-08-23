<!--
SPDX-FileCopyrightText: 2023 Deutsche Telekom AG

SPDX-License-Identifier: CC0-1.0    
-->
# Welcome to the Arc Agent Init Project

The following project is a demo project for the Arc Agent Framework. 
It can also be used to kickstart a new Spring Boot project that uses the Arc Agent Framework.

## How to run

#### 1. Add language model configuration

Add language model configuration and API-KEY to `config/application.yml`.

```
ai:
  clients:
    - model-name: GPT-4o
      api-key: [YOUR_OPENAI_API_KEY]
      client: azure
```

**Hint**: If you want to use a different language model, 
the example agent under agents/assistant-agent.kts must be adjusted accordingly.

#### 2. Start the Application

Start the Demo Application like a normal Spring Boot application.
This requires the port 8080 to be available.

```bash
  ./gradlew bootRun
```


#### 3. Access the Agent

Arc Agents can be accessed via a Graphiql interface.
Open http://localhost:8080/graphiql?path=/graphql

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
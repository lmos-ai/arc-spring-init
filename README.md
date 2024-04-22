# Welcome to the Arc Agent Demo Project

The following project is a demo project for the Arc Agent Framework. 
It can also be used to kickstart a new Spring Boot project that uses the Arc Agent Framework.

## How to run

#### 1. Add language model configuration

Add language model configuration and API-KEY to `config/application.yml`.

```
ai:
  clients:
    - model-name: gpt-35
      url: [YOUR_AZURE_ENDPOINT]
      api-key: [YOUR_API_KEY]
      client: azure
```

#### 2. Start the Application

Start the Demo Application like a normal Spring Boot application.
This requires the port 8080 to be available.

```bash
  ./gradlew bootRun
```


#### 3. Chat with the Application

Chatting with the application can be done by sending a POST request to the `/chat` endpoint.

```
curl -X POST --location "http://localhost:8080/chat/[AGENT_NAME]" \
-H "Content-Type: application/json" \
-d '{
"message" : "Ask question here"
}'
```

Or using the "arc" command line tool.

```bash
  ./arc news-agent
```
or

```bash
  ./gradlew --console=plain arc -Pagent="news-agent" 
```

#### 4. Add new Agents

New agents can be added to the `agents` folder located at the root of the project.
The folder contains some default agents that can be used as a template.
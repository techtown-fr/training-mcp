# MCP Server Demo

This is a simple MCP server implemented in Java using Spring Boot and the Model Context Protocol SDK.
It exposes two tools: `sum` and `subtract`.

## Technical Details

- **MCP SDK Version**: 0.16.0
- **Java Version**: 21
- **Spring Boot Version**: 3.4.0
- **Transport**: Server-Sent Events (SSE) via WebMVC

## Prerequisites

- Docker (recommended)
- OR: Java 21+ and Maven 3.9+

## Build and Run

### With Docker (Recommended)

Docker handles all dependencies including Maven and Java:

```bash
docker build -t mcp-server-demo .
docker run -p 8080:8080 mcp-server-demo
```

### Locally (if you have Java 21 and Maven)

```bash
mvn clean package
java -jar target/mcp-server-demo-0.0.1-SNAPSHOT.jar
```

The server will start on port 8080.

## Usage

The process of calling an MCP server over HTTP/SSE is:

1. Obtain an MCP session token
2. Initialize the session
3. Make calls to the MCP server

### Obtain an MCP session token

First open a new terminal session, and run the following command:

```bash
export MCP_SERVER="http://localhost:8080"
curl "${MCP_SERVER}/sse"
```

You should get a result like this:

```
event: endpoint
data: http://localhost:8080/messages?sessionId=...
```

The curl program instead of exiting immediately, will instead keep running (SSE stream). **Do not terminate the running curl program!**

### Initialize MCP session

Run the following commands in another terminal session/window. Replace `MCP_ENDPOINT` with the full URL returned in the `data` field above (including the `sessionId`).

```bash
export MCP_ENDPOINT="http://localhost:8080/messages?sessionId=YOUR_SESSION_ID"

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2025-06-18",
    "clientInfo": {
        "name": "mcp-client",
        "version": "1.0.0"
    }
  }
}'
```

You should see a response with `serverInfo` and `capabilities`.

### Send initialized notification (REQUIRED!)

After receiving the initialize response, you **must** send the `initialized` notification before using other MCP methods:

```bash
curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "method": "notifications/initialized"
}'
```

This notification tells the server that the client is ready to receive requests.

### List MCP tools

To obtain a list of tools:

```bash
curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/list"
}'
```

You should see `sum` and `subtract` tools.

### Calling MCP tools

#### Call `sum`

```bash
curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
        "name": "sum",
        "arguments": {
            "a": 10,
            "b": 5
        }
    }
}'
```

Response:
```json
{
    "jsonrpc": "2.0",
    "id": 3,
    "result": {
        "content": [
            {
                "type": "text",
                "text": "15.0"
            }
        ],
        "isError": false
    }
}
```

#### Call `subtract`

```bash
curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
    "jsonrpc": "2.0",
    "id": 4,
    "method": "tools/call",
    "params": {
        "name": "subtract",
        "arguments": {
            "a": 10,
            "b": 5
        }
    }
}'
```

Response:
```json
{
    "jsonrpc": "2.0",
    "id": 4,
    "result": {
        "content": [
            {
                "type": "text",
                "text": "5.0"
            }
        ],
        "isError": false
    }
}
```

### MCP Ping

```bash
curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "id": 5,
  "method": "ping"
}'
```


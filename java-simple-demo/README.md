# MCP Server Demo

Serveur MCP Java bas niveau : Spring Boot 4.1.1 + MCP Java SDK 2.0.1 + transport Spring AI WebMVC.

Il expose deux outils : `sum` et `subtract`.

## Technical Details

- **MCP Java SDK**: 2.0.1 (révision protocolaire maximale : **2025-11-25**)
- **Spring Boot**: 4.1.1
- **Transport WebMVC**: `org.springframework.ai:mcp-spring-webmvc` 2.0.1
- **Transport**: Streamable HTTP stateless (`POST /mcp`)
- **Java**: 21

> Le SDK Java stable ne parle pas encore **2026-07-28**. Les clients modernes négocient `2025-11-25` (handshake `initialize` encore requis).

## Prerequisites

- Docker (recommended)
- OR: Java 21+ and Maven 3.9+

## Build and Run

### With Docker (Recommended)

```bash
docker build -t mcp-server-demo .
docker run -p 8080:8080 mcp-server-demo
```

### Locally (if you have Java 21 and Maven)

```bash
mvn clean package
java -jar target/mcp-server-demo-0.0.1-SNAPSHOT.jar
```

The server starts on port 8080. Endpoint MCP : `http://localhost:8080/mcp`.

## MCP Inspector

UI :

```bash
npx --yes @modelcontextprotocol/inspector@2 --transport http --server-url http://localhost:8080/mcp
```

Liste des outils en CLI :

```bash
npx --yes @modelcontextprotocol/inspector@2 --cli http://localhost:8080/mcp --transport http --method tools/list
```

Appel d'un outil :

```bash
npx --yes @modelcontextprotocol/inspector@2 --cli http://localhost:8080/mcp --transport http \
  --method tools/call --tool-name sum --tool-args-json '{"a":10,"b":5}'
```

Ou lancez le script :

```bash
./call_mcp.sh
```

## Usage manuel (legacy 2025-11-25)

Le SDK Java 2.0.1 reste en ère `initialize`. Exemple :

```bash
export MCP_ENDPOINT="http://localhost:8080/mcp"

curl -X POST "${MCP_ENDPOINT}" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "initialize",
    "params": {
      "protocolVersion": "2025-11-25",
      "capabilities": {},
      "clientInfo": { "name": "mcp-client", "version": "1.0.0" }
    }
  }'
```

Puis `notifications/initialized`, `tools/list` et `tools/call` sur le même endpoint.

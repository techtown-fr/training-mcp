# Guide pour les Agents IA - MCP Server Demo

## Vue d'ensemble du projet

Serveur MCP démonstratif en Java (SDK bas niveau) avec Spring Boot. Il expose `sum` et `subtract` via Streamable HTTP stateless (`POST /mcp`).

## Versions

### Environnement
- **Java**: 21 (LTS)
- **Maven**: 3.9+
- **Docker**: `maven:3.9-eclipse-temurin-21` / `eclipse-temurin:21-jre-alpine`

### Dépendances
- **Spring Boot**: 4.1.1
- **MCP Java SDK**: 2.0.1 (`io.modelcontextprotocol.sdk:mcp`)
- **Transport WebMVC**: `org.springframework.ai:mcp-spring-webmvc` 2.0.1
- **Spring Boot Starter WebMVC** + **Starter Test**

### Protocole MCP
- **Révision maximale supportée par le SDK Java**: 2025-11-25
- **Pas encore 2026-07-28** (aucune release Java stable)
- **Transport**: Streamable HTTP stateless
- **Port**: 8080, endpoint `/mcp`

## Architecture

- `com.example.mcp.server`: package racine
- `CalculatorService`: métier (`@Service`)
- `McpConfig`: transport `WebMvcStatelessServerTransport` + tools via `McpStatelessServerFeatures.SyncToolSpecification`
- `McpServerApplication`: `@SpringBootApplication`

## Ajouter un outil

1. Étendre `CalculatorService` ou un nouveau service
2. Déclarer l'outil dans `McpConfig` avec `Tool.builder()` et `SyncToolSpecification.builder()`
3. Lire les arguments via `request.arguments()`
4. Retourner `CallToolResult.builder()...build()`

## Tests

```bash
mvn clean package
./call_mcp.sh
npx --yes @modelcontextprotocol/inspector@2 --cli http://localhost:8080/mcp --transport http --method tools/list
```

## Docker

Conserver le build multi-stage et le `VOLUME /tmp`. Le serveur écoute sur 8080.

## Points d'attention

- Ne pas réintroduire `io.modelcontextprotocol.sdk:mcp-spring-webmvc` (artefact déplacé dans Spring AI)
- Ne pas réactiver HTTP+SSE (`/sse` + `/messages`) : transport déprécié
- Ne pas ajouter la capability MCP logging (dépréciée en 2026-07-28)
- Le handshake `initialize` reste requis pour les clients Java 2025-11-25

## Ressources

- [MCP Specification](https://spec.modelcontextprotocol.io/)
- [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk)
- [Spring AI MCP overview](https://docs.spring.io/spring-ai/reference/2.0/api/mcp/mcp-overview.html)

**Dernière mise à jour**: septembre 2026 (MCP SDK 2.0.1 / Spring Boot 4.1.1)

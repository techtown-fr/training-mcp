# Spring Simple Demo — Serveur MCP GéoCalc

Serveur MCP métier GéoCalc en **Spring Boot 4.1** + **Spring AI 2.0**, transport **STDIO**.

Pour les agents IA : voir [`AGENTS.md`](AGENTS.md) (référencé aussi par [`CLAUDE.md`](CLAUDE.md)).

## Outils exposés

| Outil | Description |
|-------|-------------|
| `list_shapes` | Liste les formes supportées |
| `get_formula` | Formule et dimensions d'une forme |
| `validate_calculation` | Vérifie un résultat calculé |

## Prérequis

- JDK 21+
- Maven 3.9+

## Build

```bash
mvn test
mvn package
```

JAR attendu : `target/geocalc-mcp-server.jar`

## MCP Inspector

```bash
npm exec --yes -- @modelcontextprotocol/inspector java -jar target/geocalc-mcp-server.jar
```

Liste des outils en CLI :

```bash
npm exec --yes -- @modelcontextprotocol/inspector --cli java -jar target/geocalc-mcp-server.jar --method tools/list
```

> Si `npx @modelcontextprotocol/inspector …` échoue localement, utiliser `npm exec` comme ci-dessus.

## Configuration STDIO

- `spring.main.web-application-type=none`
- `spring.main.banner-mode=off`
- `spring.ai.mcp.server.stdio=true`
- logs Logback dirigés vers **stderr** (`logback-spring.xml`)

Ne jamais écrire sur stdout hors protocole JSON-RPC.

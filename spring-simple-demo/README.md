# Spring Simple Demo — Serveur MCP GéoCalc

Serveur MCP métier GéoCalc en **Spring Boot 4.1.1** + **Spring AI 2.0.1**, transport **STDIO**.

Le SDK Java sous-jacent (2.0.1) parle au maximum **MCP 2025-11-25**. Aucune release Java stable ne supporte encore **2026-07-28**.

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
npx --yes @modelcontextprotocol/inspector@2 ./run-stdio.sh
```

Liste des outils en CLI :

```bash
npx --yes @modelcontextprotocol/inspector@2 --cli ./run-stdio.sh --method tools/list
```

> Inspector 2 traite `-jar` comme un flag : utiliser le wrapper `run-stdio.sh`.

## Configuration STDIO

- `spring.main.web-application-type=none`
- `spring.main.banner-mode=off`
- `spring.ai.mcp.server.stdio=true`
- logs Logback dirigés vers **stderr** (`logback-spring.xml`)

Ne jamais écrire sur stdout hors protocole JSON-RPC.

# Guide pour les Agents IA — Serveur MCP GéoCalc (Spring AI)

## Vue d'ensemble

Serveur MCP métier **GéoCalc** en Java : Spring Boot **4.1** + Spring AI **2.0**, transport **STDIO**.
Il expose trois outils (`list_shapes`, `get_formula`, `validate_calculation`) pour piloter / valider le calcul de formes géométriques.

Package racine : `fr.techtown.geocalc.mcp`

## Versions

| Composant | Version |
|-----------|---------|
| Java | 21+ (`java.version` dans le POM) |
| Maven | 3.9+ |
| Spring Boot | 4.1.0 |
| Spring AI | 2.0.0 (`spring-ai-bom`) |
| Starter MCP | `org.springframework.ai:spring-ai-starter-mcp-server` |
| Transport | STDIO (`spring.ai.mcp.server.stdio=true`) |

Ne pas réintroduire `org.springframework.experimental` ni l'ancien artefact `spring-ai-mcp`.

## Structure du code

| Fichier | Rôle |
|---------|------|
| `GeoCalcMcpApplication.java` | Point d'entrée Spring Boot |
| `ShapeCatalog.java` | Métier : formes, formules, validation |
| `GeoCalcTools.java` | Bean `@Component` exposant les outils via `@McpTool` |
| `application.properties` | Config serveur MCP + STDIO |
| `logback-spring.xml` | Logs sur **stderr** uniquement |
| `ShapeCatalogTest.java` | Tests unitaires du catalogue |

## Outils MCP

| Nom | Description | Retour |
|-----|-------------|--------|
| `list_shapes` | Liste les formes supportées | `ShapeList` (`{ shapes: [...] }`) |
| `get_formula` | Formule + dimensions requises | `ShapeDefinition` |
| `validate_calculation` | Compare un résultat au calcul attendu | `ValidationResult` |

Formes actuelles : `circle`, `rectangle`, `triangle`.

## Règles critiques STDIO

- **stdout** = JSON-RPC uniquement. Interdit : banner, `System.out.println`, logs console.
- **stderr** = logs humains (Logback `ConsoleAppender` → `System.err`).
- Garder `spring.main.banner-mode=off` et `spring.main.web-application-type=none`.
- `outputSchema` MCP doit être un **object** racine (pas un array) — d'où le wrapper `ShapeList` pour `list_shapes`.

## Ajouter une forme ou un outil

1. Étendre `ShapeCatalog` (définition + branche `validate`) et les tests.
2. Réutiliser les outils existants si possible (pas de 4ᵉ outil générique type `execute_code`).
3. Nouvel outil : méthode dans `GeoCalcTools` annotée `@McpTool` + `@McpToolParam`, retour object/record, `generateOutputSchema = true` si pertinent.
4. Relancer `mvn test` puis `mvn package`.

Les hints `readOnlyHint` / `destructiveHint` / `openWorldHint` informent le client ; ils ne remplacent pas la validation serveur.

## Build & validation

```bash
mvn test
mvn package
```

JAR : `target/geocalc-mcp-server.jar`

Contrôle pollution stdout :

```bash
java -jar target/geocalc-mcp-server.jar 1>mcp-stdout.log 2>mcp-stderr.log
# Ctrl+C — mcp-stdout.log doit rester vide de banner/logs
```

Inspector :

```bash
npm exec --yes -- @modelcontextprotocol/inspector java -jar target/geocalc-mcp-server.jar
npm exec --yes -- @modelcontextprotocol/inspector --cli java -jar target/geocalc-mcp-server.jar --method tools/list
```

Si le miroir Maven entreprise est injoignable, utiliser un `settings.xml` pointant vers Maven Central (`mvn -s …`).

## Ce qu'il ne faut pas faire

- Écrire sur stdout hors protocole
- Exposer un outil générique d'exécution de code
- Changer de transport (SSE/HTTP) sans demande explicite — ce projet est STDIO
- Committer `target/` ou des logs `mcp-*.log`

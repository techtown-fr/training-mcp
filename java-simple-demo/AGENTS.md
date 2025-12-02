# Guide pour les Agents IA - MCP Server Demo

## Vue d'ensemble du projet

Ce projet est un serveur MCP (Model Context Protocol) démonstratif implémenté en Java avec Spring Boot. Il expose deux outils calculatoires (`sum` et `subtract`) via le protocole MCP sur HTTP/SSE.

## Versions des programmes

### Environnement de développement
- **Java**: 21 (LTS)
- **Maven**: 3.9+
- **Docker**: Compatible avec les images suivantes
  - Build: `maven:3.9-eclipse-temurin-21`
  - Runtime: `eclipse-temurin:21-jre-alpine`

### Dépendances principales
- **Spring Boot**: 3.4.0
- **MCP SDK**: 0.16.0
  - `io.modelcontextprotocol.sdk:mcp` (module parapluie incluant mcp-core et mcp-json-jackson)
  - `io.modelcontextprotocol.sdk:mcp-spring-webmvc`
- **Lombok**: Version gérée par Spring Boot (scope: compile optionnel)
- **Spring Boot Starter Web**: Version gérée par Spring Boot
- **Spring Boot Starter Test**: Version gérée par Spring Boot (scope: test)

### Protocole MCP
- **Version du protocole**: 2025-06-18
- **Transport**: Server-Sent Events (SSE) via Spring WebMVC
- **Port par défaut**: 8080

## Bonnes pratiques pour les modifications

### 1. Architecture et structure du code

**Respecter l'organisation actuelle:**
- `com.example.mcp.server`: Package racine pour tous les composants
- Services métier: Annoter avec `@Service` (exemple: `CalculatorService`)
- Configuration: Annoter avec `@Configuration` (exemple: `McpConfig`)
- Application principale: `@SpringBootApplication` dans `McpServerApplication`

**Ajout de nouveaux outils MCP:**
1. Créer ou étendre le service métier dans `CalculatorService` ou un nouveau service
2. Déclarer l'outil dans `McpConfig` avec `Tool.Builder` et `McpServerFeatures.SyncToolSpecification`
3. Spécifier clairement:
   - Le nom de l'outil (`.name()`)
   - La description (`.description()`)
   - Le schéma des paramètres (`.inputSchema(jsonMapper, schemaJsonString)`)
   - Le handler qui implémente la logique (lambda `(exchange, args) -> CallToolResult`)

### 2. Gestion des dépendances

**Lors de l'ajout de dépendances:**
- Privilégier les starters Spring Boot quand disponibles
- Utiliser les propriétés Maven pour les versions custom (comme `<mcp.version>`)
- Vérifier la compatibilité avec Java 21
- Éviter les dépendances redondantes (Spring Boot gère beaucoup de versions)

**Ne pas:**
- Modifier la version de Java (le projet est conçu pour Java 21)
- Modifier la version de Spring Boot sans tester l'intégralité du projet
- Ajouter des dépendances sans les déclarer dans le `pom.xml`

### 3. Tests et validation

**Avant toute modification:**
1. Compiler le projet: `mvn clean package`
2. Tester avec Docker pour garantir la reproductibilité
3. Vérifier les endpoints MCP:
   - `/sse` pour obtenir un session
   - `/messages?sessionId=...` pour les requêtes MCP

**Tests de régression:**
- Tester `tools/list` pour vérifier que tous les outils sont visibles
- Tester `tools/call` pour chaque outil (sum, subtract)
- Vérifier que le JSON-RPC 2.0 est respecté

### 4. Convention de code

**Style Java:**
- Utiliser Lombok pour réduire le boilerplate (`@Service`, `@RequiredArgsConstructor`, etc.)
- Suivre les conventions Spring Boot
- Nommer les méthodes de manière explicite et descriptive
- Commenter les logiques complexes

**Configuration:**
- Utiliser `application.properties` pour les configurations externalisables
- Éviter les valeurs hardcodées dans le code Java
- Documenter les propriétés custom dans `application.properties`

### 5. Docker et déploiement

**Build multi-stage:**
- Stage 1: Build avec Maven
- Stage 2: Runtime avec JRE Alpine (image légère)

**Si vous modifiez le Dockerfile:**
- Conserver le build multi-stage pour optimiser la taille
- Utiliser les images officielles Eclipse Temurin
- Maintenir le `VOLUME /tmp` pour Spring Boot
- Exposer explicitement le port 8080 si ajouté: `EXPOSE 8080`

### 6. Protocole MCP

**Comprendre le flux:**
1. Client ouvre une connexion SSE sur `/sse`
2. Serveur retourne un endpoint avec `sessionId`
3. Client initialise la session avec `initialize`
4. **Client envoie la notification `notifications/initialized` (OBLIGATOIRE!)**
5. Client peut ensuite appeler `tools/list`, `tools/call`, `ping`, etc.

**Important:** Sans la notification `initialized`, le serveur ne traitera pas les requêtes autres que `initialize`.

**Lors de l'ajout de fonctionnalités MCP:**
- Respecter le format JSON-RPC 2.0
- Utiliser les types de contenu MCP appropriés (ex: `text`)
- Gérer les erreurs avec `isError: true`
- Documenter les nouveaux outils dans le README.md

### 7. Gestion des erreurs

**Bonnes pratiques:**
- Capturer les exceptions dans les handlers MCP
- Retourner des messages d'erreur clairs et exploitables
- Logger les erreurs pour le débogage
- Utiliser `isError: true` dans les réponses MCP en cas d'échec

### 8. Documentation

**Maintenir à jour:**
- `README.md`: Guide utilisateur avec exemples curl
- `AGENTS.md` (ce fichier): Guide pour les développeurs/agents
- Commentaires Javadoc pour les méthodes publiques
- Schémas MCP avec descriptions claires

**Lors de l'ajout de fonctionnalités:**
1. Mettre à jour le README avec des exemples d'utilisation
2. Ajouter des tests si pertinent
3. Documenter les nouvelles propriétés de configuration

## Commandes utiles

```bash
# Compilation
mvn clean package

# Exécution locale
java -jar target/mcp-server-demo-0.0.1-SNAPSHOT.jar

# Build Docker
docker build -t mcp-server-demo .

# Run Docker
docker run -p 8080:8080 mcp-server-demo

# Tests uniquement
mvn test

# Vérification du style (si checkstyle est configuré)
mvn checkstyle:check
```

## Points d'attention

### Sécurité
- Ce projet est une **démonstration** et ne doit pas être utilisé en production sans sécurisation
- Implémenter une authentification/autorisation avant exposition publique
- Valider toutes les entrées utilisateur
- Considérer les limites de ressources (rate limiting, timeout)

### Performance
- SSE maintient des connexions ouvertes : gérer correctement le cycle de vie des sessions
- Considérer un nettoyage des sessions expirées
- Pour un usage en production, évaluer la scalabilité horizontale

### Compatibilité
- Le MCP SDK doit respecter la spécification MCP 2025-06-18
- Vérifier la compatibilité lors de la mise à jour du SDK
- Tester avec différents clients MCP

## Ressources

- [Model Context Protocol Specification](https://spec.modelcontextprotocol.io/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/3.4.0/reference/)
- [MCP SDK Java Repository](https://github.com/modelcontextprotocol/java-sdk)

---

**Date de création**: Décembre 2024  
**Dernière mise à jour**: Décembre 2024 (MCP SDK 0.16.0)  
**Version du guide**: 1.1


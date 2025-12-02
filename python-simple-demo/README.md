# Serveur MCP Python - Démo Simple

Ce projet démontre comment créer un serveur MCP (Model Context Protocol) en Python avec [FastMCP](https://github.com/jlowin/fastmcp).

## 🌐 Transport : Streamable HTTP (Recommandé)

Ce serveur utilise le transport **Streamable HTTP**, qui est le transport recommandé pour les déploiements web selon la spécification MCP.

| Transport | Utilisation | Avantages |
|-----------|-------------|-----------|
| **Streamable HTTP** ✅ | Déploiements web, production | Multi-clients, HTTP standard, scalable |
| SSE | Legacy, compatibilité | Support anciens clients |
| STDIO | Développement local, CLI | Simple, pas de réseau |

> **📝 Référence** : [FastMCP - Running Server Documentation](https://github.com/jlowin/fastmcp)

## 🎯 Fonctionnalités

Le serveur expose les fonctionnalités MCP suivantes :

### 🔧 Tools (Outils - Actions)
1. **`health_check`** - Vérifie l'état de santé du serveur MCP
2. **`get_weather`** - Récupère les informations météo pour une localisation

### 📦 Resources (Ressources - Données)
- **Ressources statiques** (visibles dans la liste) :
  - `README.md` - Documentation du projet
  - `resource://config` - Configuration système en JSON
- **Resource template** (dynamique) : `file://{path}` - Lit le contenu de n'importe quel fichier local

### 💬 Prompts (Modèles de conversation)
- **`code_review`** - Fournit un template pour la revue de code dans un langage donné

### 🏓 Ping (Vérification de disponibilité)
- **Géré automatiquement** par le protocole MCP - aucune implémentation nécessaire
- Permet de vérifier que le serveur est actif et mesurer la latence

> **💡 Notes importantes** : 
> - Les ressources statiques apparaissent dans `list_resources()`, tandis que le resource template `file://{path}` ne s'affiche pas dans la liste mais peut être appelé directement
> - Le ping est une fonctionnalité native du protocole MCP, gérée automatiquement par FastMCP

## ✅ Résumé des fonctionnalités MCP complètes

Le serveur démontre maintenant **toutes les fonctionnalités principales du protocole MCP** :

| Fonctionnalité | Implémentation | Status |
|----------------|----------------|--------|
| **Ping** | Automatique (protocole MCP) | ✅ Actif |
| **Tools** | `health_check`, `get_weather` | ✅ 2 outils |
| **Resources** | Statiques + Template | ✅ 2 statiques + 1 template |
| **Prompts** | `code_review` | ✅ 1 prompt |

🚀 **Votre serveur MCP est maintenant complet et démontre toutes les capacités du protocole !**

## 📦 Installation

### Prérequis
- Python 3.11 ou supérieur
- pip

### Installation des dépendances

```bash
pip install fastmcp
```

Ou avec le fichier requirements.txt :

```bash
pip install -r requirements.txt
```

## 🚀 Comment l'utiliser ?

### Démarrer le serveur HTTP

```bash
cd python-simple-demo
python3 server.py
```

Le serveur démarre sur `http://0.0.0.0:8000/mcp` par défaut.

**Configuration via variables d'environnement :**

| Variable | Défaut | Description |
|----------|--------|-------------|
| `MCP_TRANSPORT` | `http` | Transport à utiliser (`http` ou `stdio`) |
| `MCP_HOST` | `0.0.0.0` | Adresse d'écoute (mode HTTP) |
| `MCP_PORT` | `8000` | Port HTTP (mode HTTP) |
| `MCP_PATH` | `/mcp` | Chemin de l'endpoint MCP (mode HTTP) |

```bash
# Exemple : changer le port
MCP_PORT=9000 python3 server.py

# Exemple : mode STDIO (pour l'inspecteur MCP)
MCP_TRANSPORT=stdio python3 server.py
```

### Option A : Connexion via Client Python

```python
from fastmcp import Client

async def main():
    # Connexion via Streamable HTTP
    async with Client("http://127.0.0.1:8000/mcp") as client:
        # Lister les outils
        tools = await client.list_tools()
        print(f"Tools: {tools}")
        
        # Appeler un outil
        result = await client.call_tool("get_weather", {"location": "Paris"})
        print(f"Result: {result}")

import asyncio
asyncio.run(main())
```

### Option B : Via l'Inspecteur MCP (Mode STDIO)

**Méthode classique** - L'inspecteur lance le serveur en mode STDIO :

```bash
# Lance directement le serveur avec l'inspecteur (mode STDIO)
MCP_TRANSPORT=stdio npx @modelcontextprotocol/inspector python3 server.py
```

L'inspecteur s'ouvre automatiquement et vous permet de tester tous les outils, ressources et prompts.

### Option C : Via l'Inspecteur MCP (Mode HTTP)

**Méthode HTTP** - Connexion à un serveur HTTP déjà démarré :

```bash
# 1. Démarrez le serveur HTTP dans un terminal
python3 server.py

# 2. Dans un autre terminal, lancez l'inspecteur avec l'URL
npx @modelcontextprotocol/inspector http://localhost:8000/mcp
```

Ou via l'interface web :
1. Lancez `npx @modelcontextprotocol/inspector` sans argument
2. Sélectionnez **"Streamable HTTP"** comme type de connexion
3. Entrez l'URL : `http://127.0.0.1:8000/mcp`
4. Cliquez sur **"Connect"**

> **🐳 Alternative : Lancer l'Inspecteur via Docker**
> 
> Si vous ne souhaitez pas utiliser `npx`, vous pouvez lancer l'Inspecteur MCP via Docker :
> 
> ```bash
> docker run --rm --network host -p 6274:6274 -p 6277:6277 ghcr.io/modelcontextprotocol/inspector:latest
> ```
> 
> L'interface sera accessible sur `http://localhost:6274`. Connectez-vous ensuite à votre serveur MCP sur `http://localhost:8000/mcp`.
> 
> - **Port 6274** : Interface web de l'Inspecteur
> - **Port 6277** : Proxy MCP
> 
> Voir la [documentation officielle de l'Inspecteur](https://github.com/modelcontextprotocol/inspector) pour plus de détails.

Cela vous permettra de :
- Cliquer sur **"Ping"** pour vérifier que le serveur répond
- Cliquer sur **"Tools"** pour tester `health_check` et `get_weather`
- Cliquer sur **"Resources"** pour voir les ressources statiques
- Cliquer sur **"Prompts"** pour tester `code_review`

### Option D : Dans Claude Desktop

**Emplacement du fichier de configuration :**
- **macOS** : `~/Library/Application Support/Claude/claude_desktop_config.json`
- **Windows** : `%APPDATA%/Claude/claude_desktop_config.json`
- **Linux** : `~/.config/Claude/claude_desktop_config.json`

**Mode STDIO (recommandé pour Claude Desktop)** - Claude lance le serveur :

```json
{
  "mcpServers": {
    "python-demo": {
      "command": "python3",
      "args": ["/chemin/absolu/vers/server.py"],
      "env": {
        "MCP_TRANSPORT": "stdio"
      }
    }
  }
}
```

**Mode HTTP** - Connexion à un serveur déjà démarré :

```json
{
  "mcpServers": {
    "python-demo-http": {
      "url": "http://127.0.0.1:8000/mcp"
    }
  }
}
```

> **⚠️ Note** : Assurez-vous que le serveur est démarré avant de lancer Claude Desktop.

### Option D : Test avec curl

Vous pouvez tester le serveur directement avec curl :

```bash
# Test de l'endpoint MCP (initialize)
curl -X POST http://127.0.0.1:8000/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"test","version":"1.0"}},"id":1}'
```

## 🐳 Utilisation avec Docker

### Construire l'image

```bash
docker build -t mcp-python-demo .
```

### Option A : Exécuter le conteneur HTTP

```bash
docker run --rm -p 8000:8000 mcp-python-demo
```

Le serveur sera accessible sur `http://localhost:8000/mcp`.

**Flags Docker importants :**
- `-p 8000:8000` : Expose le port HTTP du conteneur
- `--rm` : Nettoie automatiquement le conteneur après l'arrêt

### Option B : Tester avec l'Inspecteur MCP via Docker

```bash
# 1. Démarrer le conteneur
docker run --rm -p 8000:8000 mcp-python-demo

# 2. Dans un autre terminal, lancer l'inspecteur
npx @modelcontextprotocol/inspector
# Puis connectez-vous à http://localhost:8000/mcp
```

### Option C : Configuration Docker personnalisée

```bash
# Changer le port
docker run --rm -p 9000:9000 -e MCP_PORT=9000 mcp-python-demo

# Changer le path
docker run --rm -p 8000:8000 -e MCP_PATH=/api/mcp mcp-python-demo
```

### Option D : Utiliser avec Claude Desktop via Docker

```json
{
  "mcpServers": {
    "python-demo-docker": {
      "url": "http://localhost:8000/mcp"
    }
  }
}
```

> **⚠️ Important** : Le conteneur doit être démarré avant Claude Desktop.

## 📝 Exemples d'utilisation

### Ping (Vérification de disponibilité)

```python
from fastmcp import Client

async with Client("http://127.0.0.1:8000/mcp") as client:
    is_alive = await client.ping()  # Retourne True si le serveur répond
```

### Tool : health_check

```python
from fastmcp import Client

async with Client("http://127.0.0.1:8000/mcp") as client:
    result = await client.call_tool("health_check", {})
    # Résultat : {"status": "healthy", "server": "python-simple-demo", ...}
```

### Tool : get_weather

```python
from fastmcp import Client

async with Client("http://127.0.0.1:8000/mcp") as client:
    result = await client.call_tool("get_weather", {"location": "Paris"})
    # Résultat : {"location": "Paris", "temperature": 72, ...}
```

### Resources

```python
from fastmcp import Client

async with Client("http://127.0.0.1:8000/mcp") as client:
    # Lister les ressources statiques
    resources = await client.list_resources()
    
    # Lire une ressource
    content = await client.read_resource("resource://config")
```

### Prompt : code_review

```python
from fastmcp import Client

async with Client("http://127.0.0.1:8000/mcp") as client:
    prompt = await client.get_prompt("code_review", {"language": "Python"})
```

## 🔧 Structure du projet

```
python-simple-demo/
├── server.py           # Code du serveur MCP (Streamable HTTP)
├── test_server.py      # Client de test MCP (legacy STDIO)
├── requirements.txt    # Dépendances Python
├── Dockerfile          # Configuration Docker
├── AGENTS.md           # Guide pour les agents IA
└── README.md           # Cette documentation
```

## 🐛 Dépannage

### Erreur : "Address already in use"

**Problème** : Le port 8000 est déjà utilisé.

**Solution** :

```bash
# Changer le port
MCP_PORT=9000 python3 server.py

# Ou trouver et arrêter le processus utilisant le port
lsof -i :8000
kill <PID>
```

### Erreur : "No module named 'fastmcp'"

**Problème** : Le package FastMCP n'est pas installé.

**Solution** :

```bash
pip3 install fastmcp
# ou
pip3 install -r requirements.txt
```

### Le serveur ne répond pas

**Solutions** :

1. Vérifiez que le serveur est bien démarré avec `python3 server.py`
2. Vérifiez l'URL (par défaut : `http://127.0.0.1:8000/mcp`)
3. Testez avec curl :
   ```bash
   curl http://127.0.0.1:8000/mcp
   ```
4. Vérifiez les logs du serveur pour les erreurs

### Connexion refusée depuis Docker

**Problème** : Le client ne peut pas se connecter au serveur dans Docker.

**Solution** :

```bash
# Assurez-vous d'exposer le port
docker run --rm -p 8000:8000 mcp-python-demo

# Utilisez localhost ou 127.0.0.1 depuis l'hôte
curl http://localhost:8000/mcp
```

## 📚 Ressources

- [Documentation MCP officielle](https://modelcontextprotocol.io)
- [FastMCP sur GitHub (jlowin)](https://github.com/jlowin/fastmcp)
- [Spécification du protocole](https://spec.modelcontextprotocol.io)
- [FastMCP Documentation](https://gofastmcp.com)

# Serveur MCP Python - Démo Simple

Ce projet démontre comment créer un serveur MCP (Model Context Protocol) en Python avec FastMCP.

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
pip install mcp
```

Ou avec le fichier requirements.txt :

```bash
pip install -r requirements.txt
```

## 🚀 Comment l'utiliser ?

Il y a deux façons principales de tester ce serveur :

### Option A : Via l'Inspecteur MCP (Recommandé pour tester)

L'équipe MCP fournit un outil web pour visualiser vos outils sans configurer un client IA complet.

Dans votre terminal :

```bash
# Sur macOS/Linux, utilisez python3
npx @modelcontextprotocol/inspector python3 server.py

# Sur Windows ou si 'python' pointe vers Python 3
npx @modelcontextprotocol/inspector python server.py
```

Cela ouvrira une page web où vous pourrez :
- Cliquer sur **"Ping"** pour vérifier que le serveur répond (ping automatique du protocole MCP)
- Cliquer sur **"Tools"** pour tester `health_check` et `get_weather`
- Cliquer sur **"Resources"** pour voir les ressources statiques (`README.md`, `resource://config`)
  - Les ressources statiques apparaissent dans la liste avec **"List Resources"**
  - Le resource template `file://{path}` ne s'affiche pas mais peut être lu directement en fournissant un URI
- Cliquer sur **"Prompts"** pour tester `code_review`

### Option B : Dans Claude Desktop

Pour l'utiliser directement dans l'application Claude :

1. Ouvrez votre fichier de config `claude_desktop_config.json`

   - **macOS** : `~/Library/Application Support/Claude/claude_desktop_config.json`
   - **Windows** : `%APPDATA%/Claude/claude_desktop_config.json`
   - **Linux** : `~/.config/Claude/claude_desktop_config.json`

2. Ajoutez votre serveur :

```json
{
  "mcpServers": {
    "my-demo": {
      "command": "python3",
      "args": ["/Users/cubz/git/training-mcp/python-simple-demo/server.py"]
    }
  }
}
```

   **Note** : Remplacez le chemin par le chemin absolu vers votre `server.py`

3. Redémarrez Claude Desktop

4. Vérifiez que le serveur est connecté en regardant l'icône 🔌 dans l'interface

## 🐳 Utilisation avec Docker

### Construire l'image

```bash
docker build -t mcp-python-demo .
```

### Option A : Tester avec l'Inspecteur MCP via Docker (Recommandé)

L'inspecteur MCP peut lancer directement votre conteneur Docker :

```bash
npx @modelcontextprotocol/inspector docker run -i --rm mcp-python-demo
```

**Comment ça marche ?**
- L'inspecteur exécute la commande `docker run -i --rm mcp-python-demo`
- Il communique avec le serveur MCP via stdin/stdout
- L'interface web s'ouvre pour tester tous les outils, ressources et prompts

### Option B : Exécuter le conteneur directement

```bash
docker run --rm -i mcp-python-demo
```

**Notes importantes** : 
- Le flag `-i` (interactif) est **CRITIQUE** car MCP communique via stdio (stdin/stdout)
- Le flag `--rm` nettoie automatiquement le conteneur après l'arrêt
- Sans `-i`, le serveur MCP ne pourra pas recevoir les commandes

### Option C : Utiliser avec Claude Desktop via Docker

Modifiez votre `claude_desktop_config.json` :

```json
{
  "mcpServers": {
    "my-demo-docker": {
      "command": "docker",
      "args": ["run", "-i", "--rm", "mcp-python-demo"]
    }
  }
}
```

## 📝 Exemples d'utilisation

### Ping (Vérification de disponibilité)

```python
# Dans l'inspecteur MCP :
# Cliquez sur "Ping" pour vérifier que le serveur répond
# Le ping est géré automatiquement par le protocole MCP

# Avec un client Python :
async with client:
    is_alive = await client.ping()  # Retourne True si le serveur répond
```

### Tool : health_check

```python
# Dans l'inspecteur MCP :
# 1. Allez dans "Tools"
# 2. Sélectionnez "health_check"
# 3. Cliquez sur "Call Tool"
# Résultat : {"status": "healthy", "server": "demo_full_server", ...}

# Demandez à Claude :
"Vérifie l'état de santé du serveur"
```

### Tool : get_weather

```python
# Demandez à Claude :
"Quel temps fait-il à Paris ?"
```

### Resources

#### Ressources statiques (visibles dans la liste)

```python
# Dans l'inspecteur MCP :
# 1. Cliquez sur "Resources" > "List Resources"
# 2. Vous verrez : README.md et resource://config
# 3. Sélectionnez une ressource et cliquez sur "Read Resource"

# Demandez à Claude :
"Montre-moi le contenu de resource://config"
"Lis le README du projet"
```

#### Resource template (dynamique)

```python
# Le resource template file://{path} ne s'affiche PAS dans la liste
# mais peut être appelé directement :

# Demandez à Claude :
"Lis le contenu du fichier via file:///Users/cubz/git/training-mcp/python-simple-demo/server.py"
```

### Prompt : code_review

```python
# Demandez à Claude :
"Utilise le prompt code_review pour Python et analyse ce code : [votre code]"
```

## 🔧 Structure du projet

```
python-simple-demo/
├── server.py           # Code du serveur MCP
├── requirements.txt    # Dépendances Python
├── Dockerfile          # Configuration Docker
└── README.md          # Cette documentation
```

## 🐛 Dépannage

### Erreur : "spawn python ENOENT"

**Problème** : La commande `python` n'est pas trouvée.

**Solution** : Sur macOS/Linux, utilisez `python3` au lieu de `python` :

```bash
# Vérifiez votre version de Python
which python3
python3 --version

# Utilisez python3 dans vos commandes
npx @modelcontextprotocol/inspector python3 server.py
```

### Erreur : "No module named 'mcp'"

**Problème** : Le package MCP n'est pas installé.

**Solution** :

```bash
pip3 install mcp
# ou
pip3 install -r requirements.txt
```

### Le serveur ne répond pas dans Claude Desktop

**Solutions** :

1. Vérifiez que le chemin dans `claude_desktop_config.json` est **absolu**
2. Vérifiez les logs de Claude Desktop
3. Redémarrez complètement Claude Desktop
4. Assurez-vous d'utiliser `python3` dans la commande

## 📚 Ressources

- [Documentation MCP officielle](https://modelcontextprotocol.io)
- [FastMCP sur GitHub](https://github.com/modelcontextprotocol/python-sdk)
- [Spécification du protocole](https://spec.modelcontextprotocol.io)


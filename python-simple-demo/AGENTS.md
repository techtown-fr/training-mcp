# Guide pour les Agents IA - MCP Python Demo

## Vue d'ensemble du projet

Ce projet est un serveur MCP démonstratif en Python avec **FastMCP 4**. Il expose `health_check`, `get_weather`, des ressources (statiques et dynamiques) et le prompt `code_review`. Révision cible : **2026-07-28**.

## Versions des programmes

### Environnement de développement
- **Python**: 3.11+ (minimum recommandé)
- **pip**: Version fournie avec Python 3.11+
- **Node.js/npx**: Pour l'inspecteur MCP (`@modelcontextprotocol/inspector@2`)
- **Docker**: Compatible avec l'image `python:3.11-slim`

### Dépendances principales
- **fastmcp==4.0.2**: Framework FastMCP 4
  - Installation: `pip install -r requirements.txt`
  - Fournit: `FastMCP`, `Client`, transports STDIO / Streamable HTTP

### Protocole MCP
- **Révision**: 2026-07-28 (sessionless) avec compatibilité legacy
- **Communication**: Streamable HTTP (`/mcp`) par défaut, STDIO en option
- **Framework**: FastMCP 4 (négociation dual-era automatique)

### Configuration Docker
- **Image de base**: `python:3.11-slim`
- **Variable d'environnement critique**: `PYTHONUNBUFFERED=1`
- **Utilisateur**: Non-root (`appuser`) pour la sécurité

## Bonnes pratiques pour les modifications

### 1. Architecture et structure du code

**Organisation actuelle:**
- `server.py`: Fichier unique contenant toute la logique
- Approche "All-in-One" avec FastMCP
- Trois types de fonctionnalités:
  1. **Tools** (@mcp.tool): Actions/fonctions appelables
  2. **Resources** (@mcp.resource / add_resource): Données/fichiers exposés
  3. **Prompts** (@mcp.prompt): Templates de conversation

**Pour ajouter de nouvelles fonctionnalités:**

**Tool (Outil):**
```python
@mcp.tool
def nom_fonction(param: type) -> type_retour:
    """Description claire de ce que fait l'outil."""
    return resultat
```

**Ressource statique:**
```python
@mcp.resource("resource://nom-unique")
def get_resource() -> dict:
    """Description de la ressource."""
    return {"key": "value"}
```

**Ressource dynamique (template):**
```python
@mcp.resource("pattern://{param}")
def handler_resource(param: str) -> str:
    """Description de la ressource dynamique."""
    return contenu
```

**Prompt:**
```python
@mcp.prompt
def nom_prompt(param: str) -> str:
    """Description du prompt."""
    return f"Contexte pour {param}"
```

### 2. Gestion des dépendances

**Lors de l'ajout de dépendances:**
1. Ajouter dans `requirements.txt` avec une version spécifique si nécessaire
2. Tester la compatibilité avec Python 3.11+
3. Mettre à jour le Dockerfile si des dépendances système sont nécessaires
4. Documenter les nouvelles dépendances dans le README

**Format requirements.txt:**
```txt
fastmcp==4.0.2
nouvelle-dependance==1.2.3
```

**Ne pas:**
- Installer des packages globalement sans les ajouter au requirements.txt
- Utiliser des versions incompatibles avec Python 3.11
- Ajouter des dépendances lourdes sans justification

### 3. Tests et validation

**Méthodes de test:**

1. **Avec l'inspecteur MCP (Recommandé):**
```bash
npx --yes @modelcontextprotocol/inspector@2 -e MCP_TRANSPORT=stdio python3 server.py
npx --yes @modelcontextprotocol/inspector@2 --cli python3 server.py --method tools/list -e MCP_TRANSPORT=stdio
```
- Tester chaque tool dans l'onglet "Tools"
- Vérifier les ressources dans "Resources" > "List Resources"
- Tester les prompts dans "Prompts"

2. **Client de test local:**
```bash
python3 test_server.py
```

3. **Exécution directe:**
```bash
python3 server.py
# Défaut: Streamable HTTP sur http://0.0.0.0:8000/mcp
MCP_TRANSPORT=stdio python3 server.py
```

**Tests de régression:**
- Vérifier que `get_weather` retourne un dictionnaire valide
- Confirmer que les ressources statiques (README.md, config) sont listées
- Tester le resource template avec un chemin valide
- Valider que le prompt `code_review` retourne des Messages

### 4. Convention de code Python

**Style:**
- Suivre PEP 8 pour le formatage
- Utiliser des type hints (annotations de types)
- Docstrings pour toutes les fonctions publiques
- Noms de fonctions en snake_case
- Noms de classes en PascalCase

**Exemple de fonction bien documentée:**
```python
@mcp.tool()
def calculate_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    Calculate the distance between two GPS coordinates.
    
    Args:
        lat1: Latitude of the first point
        lon1: Longitude of the first point
        lat2: Latitude of the second point
        lon2: Longitude of the second point
    
    Returns:
        Distance in kilometers
    """
    # Implementation
    return distance
```

**Formatage automatique (optionnel mais recommandé):**
```bash
pip install black isort
black server.py
isort server.py
```

### 5. Docker et déploiement

**Variables d'environnement critiques:**
```dockerfile
ENV PYTHONUNBUFFERED=1  # OBLIGATOIRE pour MCP via stdio
```

**Sans `PYTHONUNBUFFERED=1`, le serveur MCP ne fonctionnera pas correctement car:**
- Python bufferise les sorties par défaut
- MCP nécessite une communication immédiate via stdout/stdin
- Les messages resteraient bloqués dans le buffer

**Build et exécution:**
```bash
# Build
docker build -t mcp-python-demo .

# Run (le flag -i est CRITIQUE)
docker run --rm -i mcp-python-demo
```

**Flags Docker importants:**
- `-i` (interactive): Permet la communication stdin/stdout (CRITIQUE pour MCP)
- `--rm`: Nettoie automatiquement le conteneur après l'arrêt
- `-v` (volumes): Si vous devez monter des fichiers locaux pour les resources

**Sécurité:**
- Le Dockerfile utilise un utilisateur non-root (`appuser`)
- Maintenir cette pratique pour la sécurité en production

### 6. Protocole MCP et FastMCP

**Comprendre FastMCP:**
- FastMCP est une abstraction haut niveau du SDK MCP Python
- Gère automatiquement le JSON-RPC et la communication stdio
- Simplifie la création de serveurs MCP avec des décorateurs

**Types de fonctionnalités:**

1. **Tools**: Actions que l'IA peut exécuter
   - Apparaissent dans la liste des tools
   - Peuvent avoir des effets de bord
   - Exemples: get_weather, send_email, create_file

2. **Resources**: 
   - **Statiques** (via `add_resource`): Apparaissent dans `list_resources()`
   - **Templates** (via `@mcp.resource`): Dynamiques, ne s'affichent pas dans la liste
   - Fournissent des données à l'IA
   - Exemples: documentation, configuration, contenu de fichiers

3. **Prompts**: Templates de conversation
   - Fournissent un contexte structuré à l'IA
   - Retournent une liste de Messages (system, user, assistant)
   - Exemples: code_review, translate, summarize

**Distinction Resource statique vs template:**
```python
# Statique: visible dans list_resources()
readme = FileResource(uri="file://readme.md", ...)
mcp.add_resource(readme)

# Template: dynamique, invisible dans la liste
@mcp.resource("file://{path}")
def read_any_file(path: str) -> str:
    return Path(path).read_text()
```

### 7. Gestion des erreurs

**Bonnes pratiques:**
```python
@mcp.tool()
def fonction_robuste(param: str) -> dict:
    """Fonction avec gestion d'erreur appropriée."""
    try:
        # Validation des entrées
        if not param:
            return {"error": "Parameter cannot be empty"}
        
        # Logique métier
        result = traitement(param)
        
        return {"success": True, "data": result}
    
    except FileNotFoundError as e:
        return {"error": f"File not found: {str(e)}"}
    except ValueError as e:
        return {"error": f"Invalid value: {str(e)}"}
    except Exception as e:
        # Logger l'erreur pour le débogage
        print(f"Unexpected error: {str(e)}", file=sys.stderr)
        return {"error": "An unexpected error occurred"}
```

**Pour les ressources:**
```python
@mcp.resource("file://{path}")
def read_file(path: str) -> str:
    """Read file with proper error handling."""
    if not os.path.exists(path):
        return "Error: File not found."
    
    try:
        with open(path, 'r', encoding='utf-8') as f:
            return f.read()
    except PermissionError:
        return "Error: Permission denied."
    except UnicodeDecodeError:
        return "Error: File is not a text file."
    except Exception as e:
        return f"Error reading file: {str(e)}"
```

### 8. Logging et debugging

**Logging dans un serveur MCP:**
```python
import sys

# IMPORTANT: Logger sur stderr, pas stdout
# stdout est réservé à la communication MCP
def log(message: str):
    print(f"[LOG] {message}", file=sys.stderr)

@mcp.tool()
def debug_tool(param: str) -> str:
    log(f"Tool called with param: {param}")
    result = process(param)
    log(f"Result: {result}")
    return result
```

**Debugging:**
1. Vérifier les logs de Claude Desktop
2. Utiliser l'inspecteur MCP pour tester individuellement
3. Exécuter le serveur directement et envoyer du JSON-RPC manuel
4. Ajouter des logs sur stderr (pas stdout!)

### 9. Documentation

**Maintenir à jour:**
- `README.md`: Guide utilisateur avec exemples
- `AGENTS.md` (ce fichier): Guide développeur
- Docstrings dans le code pour chaque fonction
- Commentaires pour la logique complexe

**Lors de l'ajout de fonctionnalités:**
1. Documenter le tool/resource/prompt avec une docstring claire
2. Ajouter un exemple d'utilisation dans le README
3. Préciser les types de paramètres et retours
4. Expliquer les erreurs possibles

### 10. Structuration pour projets plus complexes

**Si le projet grandit, envisager cette structure:**
```
python-simple-demo/
├── server.py              # Point d'entrée, initialisation FastMCP
├── requirements.txt
├── Dockerfile
├── README.md
├── AGENTS.md
├── tools/                 # Outils MCP
│   ├── __init__.py
│   ├── weather.py
│   └── calculations.py
├── resources/             # Ressources MCP
│   ├── __init__.py
│   ├── files.py
│   └── config.py
└── prompts/              # Prompts MCP
    ├── __init__.py
    └── code_review.py
```

**Exemple de refactoring:**
```python
# tools/weather.py
def get_weather(location: str) -> dict:
    """Get weather for location."""
    return {...}

# server.py
from tools.weather import get_weather

mcp = FastMCP("demo")
mcp.tool()(get_weather)  # Enregistre le tool
```

## Commandes utiles

```bash
# Installation
pip install -r requirements.txt

# Exécution locale (HTTP)
python3 server.py

# Tests
python3 test_server.py

# Inspecteur STDIO
npx --yes @modelcontextprotocol/inspector@2 -e MCP_TRANSPORT=stdio python3 server.py
npx --yes @modelcontextprotocol/inspector@2 --cli python3 server.py --method tools/list -e MCP_TRANSPORT=stdio

# Docker (HTTP)
docker build -t mcp-python-demo .
docker run --rm -p 8000:8000 mcp-python-demo

# Formatage (optionnel)
pip install black isort
black server.py
isort server.py

# Vérification de type (optionnel)
pip install mypy
mypy server.py

# Linting (optionnel)
pip install pylint
pylint server.py
```

## Points d'attention

### Communication STDIO
- **CRITIQUE**: MCP communique via stdin/stdout
- Ne jamais utiliser `print()` sans redirection vers stderr
- Toujours utiliser `PYTHONUNBUFFERED=1` dans Docker
- Le flag `-i` est obligatoire avec Docker

### Sécurité
- Ce projet est une **démonstration**
- Le resource template `file://{path}` permet de lire n'importe quel fichier
- En production:
  - Valider et filtrer les chemins de fichiers
  - Implémenter des permissions et authentification
  - Limiter l'accès aux ressources sensibles
  - Sandboxer les opérations dangereuses

### Chemins de fichiers
- Les ressources FileResource nécessitent des chemins absolus
- Utiliser `Path.resolve()` pour obtenir le chemin absolu
- Dans Claude Desktop config: toujours utiliser des chemins absolus

### Performance
- Les tools sont synchrones par défaut
- Pour des opérations longues, considérer l'async:
```python
import asyncio

@mcp.tool()
async def long_operation(param: str) -> dict:
    result = await async_process(param)
    return result
```

### Compatibilité Python
- Minimum: Python 3.11+
- Utiliser les type hints modernes (list[str] au lieu de List[str])
- Tester avec plusieurs versions de Python si critique

## Exemples avancés

### Tool avec validation
```python
from typing import Literal

@mcp.tool()
def get_weather_advanced(
    location: str, 
    unit: Literal["celsius", "fahrenheit"] = "celsius"
) -> dict:
    """
    Get weather with unit preference.
    
    Args:
        location: City name or coordinates
        unit: Temperature unit (celsius or fahrenheit)
    """
    if not location.strip():
        return {"error": "Location cannot be empty"}
    
    # Implementation
    return {"location": location, "unit": unit, ...}
```

### Ressource avec métadonnées riches
```python
from mcp.server.fastmcp.resources import TextResource
import json

config_data = {
    "server": "demo",
    "version": "2.0",
    "features": ["tools", "resources", "prompts"]
}

config = TextResource(
    uri="resource://config/full",
    name="Configuration complète",
    text=json.dumps(config_data, indent=2),
    mime_type="application/json",
    tags={"config", "metadata"},
    description="Configuration détaillée du serveur MCP"
)
mcp.add_resource(config)
```

### Prompt avec paramètres
```python
@mcp.prompt()
def code_review_detailed(language: str, focus: str = "all") -> list[Message]:
    """
    Detailed code review prompt with focus area.
    
    Args:
        language: Programming language
        focus: Review focus (security, performance, style, all)
    """
    focus_map = {
        "security": "security vulnerabilities and best practices",
        "performance": "performance optimizations",
        "style": "code style and readability",
        "all": "all aspects"
    }
    
    focus_text = focus_map.get(focus, focus_map["all"])
    
    return [
        Message(
            content=f"You are an expert {language} code reviewer.",
            role="system"
        ),
        Message(
            content=f"Review the code focusing on {focus_text}.",
            role="user"
        )
    ]
```

## Ressources

- [Documentation MCP officielle](https://modelcontextprotocol.io)
- [FastMCP 4](https://gofastmcp.com)
- [Spécification MCP 2026-07-28](https://modelcontextprotocol.io/specification/2026-07-28)
- [PEP 8 - Style Guide for Python](https://peps.python.org/pep-0008/)

## Dépannage rapide

| Problème | Solution |
|----------|----------|
| `spawn python ENOENT` | Utiliser `python3` au lieu de `python` |
| `No module named 'fastmcp'` | `pip install -r requirements.txt` |
| Serveur ne répond pas dans Claude | Vérifier chemin absolu dans config |
| Docker ne communique pas | Ajouter flag `-i` à `docker run` |
| Buffering des messages | Définir `PYTHONUNBUFFERED=1` |
| Resource template invisible | Normal, les templates ne s'affichent pas dans list |

---

**Date de création**: Décembre 2024  
**Version du guide**: 2.0  
**Dernière mise à jour**: FastMCP 4.0.2 / MCP 2026-07-28


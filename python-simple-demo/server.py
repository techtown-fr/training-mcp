from fastmcp import FastMCP
from pathlib import Path
import os

# Configuration du serveur
# Transport: "http" (défaut, recommandé pour production) ou "stdio" (pour l'inspecteur MCP)
TRANSPORT = os.getenv("MCP_TRANSPORT", "http")
HOST = os.getenv("MCP_HOST", "0.0.0.0")
PORT = int(os.getenv("MCP_PORT", "8000"))
PATH = os.getenv("MCP_PATH", "/mcp")

# 1. Initialisation du serveur "All-in-One"
mcp = FastMCP("python-simple-demo")

# --- A. TOOLS (Outils - Actions) ---

# Note: Le PING est géré AUTOMATIQUEMENT par le protocole MCP
# Aucune implémentation nécessaire - le serveur répond automatiquement aux requêtes ping
# pour vérifier sa disponibilité et sa latence.

@mcp.tool
def health_check() -> dict:
    """Check the health status of the MCP server."""
    return {
        "status": "healthy",
        "server": "python-simple-demo",
        "version": "1.0",
        "uptime": "running"
    }

@mcp.tool
def get_weather(location: str) -> dict:
    """Get the current weather for a specified location."""
    # Simulation d'un appel API externe
    return {
        "location": location,
        "temperature": 72,
        "conditions": "Sunny",
        "humidity": 45
    }

# --- B. RESOURCES (Ressources - Données) ---

# B.1 - Ressources STATIQUES (visibles dans list_resources)
# Exemple 1: Exposer le fichier README
@mcp.resource("resource://readme")
def get_readme() -> str:
    """Documentation du serveur MCP de démonstration."""
    readme_path = Path("./README.md").resolve()
    if readme_path.exists():
        return readme_path.read_text(encoding="utf-8")
    return "README.md not found"

# Exemple 2: Ressource JSON de configuration
@mcp.resource("resource://config")
def get_config() -> dict:
    """Configuration système du serveur."""
    return {
        "server": "python-simple-demo",
        "version": "1.0",
        "mode": "development"
    }

# B.2 - Resource TEMPLATE (dynamique)
# Permet de lire n'importe quel fichier via "file:///{chemin}"
@mcp.resource("file://{path}")
def read_file(path: str) -> str:
    """Read the contents of a local file at the given path."""
    if not os.path.exists(path):
        return "Error: File not found."
    
    try:
        with open(path, 'r', encoding='utf-8') as f:
            return f.read()
    except Exception as e:
        return f"Error reading file: {str(e)}"

# --- C. PROMPT (Modèle de conversation) ---
@mcp.prompt
def code_review(language: str) -> str:
    """Provide a structured prompt for reviewing code in the given language."""
    return f"""You are a meticulous {language} code reviewer.
Focus on performance, security, and testing standards.

Please review the following {language} code and suggest improvements:"""

# 3. Point d'entrée pour l'exécution
if __name__ == "__main__":
    # Documentation: https://github.com/jlowin/fastmcp
    if TRANSPORT == "stdio":
        # Mode STDIO - Pour l'inspecteur MCP et Claude Desktop (subprocess)
        mcp.run(transport="stdio")
    else:
        # Mode HTTP (défaut) - Recommandé pour les déploiements web
        # Utiliser "sse" pour la compatibilité avec l'inspecteur MCP et non "http"
        mcp.run(transport="sse", host=HOST, port=PORT, path=PATH)
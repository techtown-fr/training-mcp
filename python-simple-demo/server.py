from mcp.server.fastmcp import FastMCP
from mcp.server.fastmcp.resources import FileResource, TextResource
from mcp.server.fastmcp.prompts.base import Message
from pathlib import Path
import os

# 1. Initialisation du serveur "All-in-One"
mcp = FastMCP("python-simple-demo")

# --- A. TOOLS (Outils - Actions) ---

# Note: Le PING est géré AUTOMATIQUEMENT par le protocole MCP
# Aucune implémentation nécessaire - le serveur répond automatiquement aux requêtes ping
# pour vérifier sa disponibilité et sa latence.

@mcp.tool()
def health_check() -> dict:
    """Check the health status of the MCP server."""
    return {
        "status": "healthy",
        "server": "python-simple-demo",
        "version": "1.0",
        "uptime": "running"
    }

@mcp.tool()
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
# Exemple 1: Exposer un fichier README
readme_path = Path("./README.md").resolve()
if readme_path.exists():
    readme_resource = FileResource(
        uri=f"file://{readme_path.as_posix()}",
        path=readme_path,
        name="README du projet",
        description="Documentation du serveur MCP de démonstration",
        mime_type="text/markdown",
        tags={"documentation"}
    )
    mcp.add_resource(readme_resource)

# Exemple 2: Ressource texte statique
config_resource = TextResource(
    uri="resource://config",
    name="Configuration système",
    text='{"server": "demo_full_server", "version": "1.0", "mode": "development"}',
    mime_type="application/json",
    tags={"config"}
)
mcp.add_resource(config_resource)

# B.2 - Resource TEMPLATE (dynamique, ne s'affiche PAS dans la liste)
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
@mcp.prompt()
def code_review(language: str) -> list[Message]:
    """Provide a structured prompt for reviewing code in the given language."""
    return [
        Message(
            content=f"You are a meticulous {language} code reviewer. "
                   f"Focus on performance, security, and testing standards.\n\n"
                   f"Please review the following {language} code and suggest improvements:",
            role="user"
        )
    ]

# 3. Point d'entrée pour l'exécution
if __name__ == "__main__":
    mcp.run()
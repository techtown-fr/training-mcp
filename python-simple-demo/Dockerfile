# Utiliser une image Python légère
FROM python:3.11-slim

# Variables d'environnement
ENV PYTHONUNBUFFERED=1
ENV MCP_TRANSPORT=http
ENV MCP_HOST=0.0.0.0
ENV MCP_PORT=8000
ENV MCP_PATH=/mcp

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier des dépendances et installer
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copier le code du serveur dans le conteneur
COPY server.py .

# Créer un utilisateur non-root pour la sécurité (bonne pratique)
RUN useradd -m appuser && chown -R appuser /app
USER appuser

# Exposer le port HTTP pour Streamable HTTP transport
EXPOSE 8000

# Commande de lancement - Serveur HTTP MCP
CMD ["python", "server.py"]


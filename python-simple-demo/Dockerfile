# Utiliser une image Python légère
FROM python:3.11-slim

# IMPORTANT : Empêcher Python de mettre en cache les sorties (buffering)
# C'est CRITIQUE pour que le protocole MCP (basé sur stdio) fonctionne via Docker
ENV PYTHONUNBUFFERED=1

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

# Commande de lancement
CMD ["python", "server.py"]


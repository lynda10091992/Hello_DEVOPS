# Fournit Java 21 pour exécuter le JAR
FROM eclipse-temurin:21-jre

# Définit le dossier de travail dans l'image
WORKDIR /app

# Copie le JAR construit par Maven dans l'image
COPY target/Hello_DEVOPS-1.0-SNAPSHOT.jar app.jar

# Indique le port utilisé par Spring Boot dans le conteneur
EXPOSE 8080

# Démarre l'application et son Tomcat embarqué
ENTRYPOINT ["java", "-jar", "app.jar"]

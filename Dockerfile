# Étape de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape d'exécution
# Correction ici : jre au lieu de jr
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Création du dossier pour la base H2
RUN mkdir -p /data

# Copie du jar depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Utilisation du port dynamique Render
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]
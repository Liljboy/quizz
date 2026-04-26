# Étape de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape d'exécution
FROM eclipse-temurin:21-jr-jammy
WORKDIR /app

# Création du dossier de données pour H2
RUN mkdir -p /app/data && chmod 777 /app/data

COPY --from=build /app/target/*.jar app.jar

# Port dynamique
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# Hello DevOps

## Présentation

**Hello DevOps** est un projet Java utilisé dans le cadre d'un TP DevOps.

L'objectif est de mettre en place une chaîne d'**Intégration Continue (CI)** qui automatise :

- la compilation du code,
- son exécution,
- les tests,
- l'analyse de la qualité du code.

## Technologies utilisées

- Java 21
- Git / GitHub
- Jenkins
- SonarQube
- JUnit

## Fichiers du projet

- `devops.java` : le programme Java (affiche un message de bienvenue)
- `Jenkinsfile` : les étapes exécutées par Jenkins
- `sonar-project.properties` : les réglages de l'analyse SonarQube

## Fonctionnement

Le programme affiche un message de bienvenue. À chaque build, Jenkins récupère le projet sur GitHub, le compile avec `javac`, le lance, puis envoie le code à SonarQube pour l'analyser.

Les tests sont écrits avec JUnit et lancés directement avec Java, sans Maven.

## Pipeline CI

```
GitHub
   ↓
Jenkins
   ↓
Compilation (javac)
   ↓
Exécution
   ↓
Tests (JUnit)
   ↓
Analyse SonarQube
   ↓
Quality Gate
```

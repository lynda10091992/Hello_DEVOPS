Présentation
Hello DevOps est un projet Java utilisé dans le cadre d'un TP DevOps.

L'objectif du projet est de mettre en place une chaîne d'Intégration Continue (CI) permettant d'automatiser les étapes de compilation, d'exécution et d'analyse du code source.

Technologies utilisées
Java
Git
GitHub
Jenkins
SonarQube
Fonctionnement
L'application exécute actuellement un programme Java simple affichant un message de bienvenue.

Le projet sera progressivement amélioré et intégré dans une chaîne CI permettant :

le versionnement du code avec GitHub ;
l'automatisation des traitements avec Jenkins ;
l'analyse de la qualité du code avec SonarQube ;
l'ajout futur de Maven et JUnit pour la compilation et les tests.
Pipeline CI
GitHub
   ↓
Jenkins
   ↓
Compilation
   ↓
Tests
   ↓
Analyse SonarQube

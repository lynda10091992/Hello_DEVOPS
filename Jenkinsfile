pipeline {
agent any   // Jenkins peut utiliser n'importe quelle machine disponible
    stages {

        stage('clone') {
            steps {
                // Efface tout le contenu du dossier de travail pour repartir à zéro
                sh "rm -rf *"
                // Télécharge le projet depuis GitHub (le projet doit être public)
                sh "git clone https://github.com/lynda10091992/Hello_DEVOPS.git"
            }
        }

        stage('build') {
            steps {
                // Entre dans le dossier du projet puis compile devops.java
                // (la compilation transforme le texte du programme en programme exécutable)
                sh "cd Hello_DEVOPS/ && javac --release 21 devops.java"
            }
        }

        stage('run') {
            steps {
                // Lance le programme Java : il affiche "Hello World! lynda from group DevOps"
                sh "cd Hello_DEVOPS/ && java devops"
            }
        }

        // ---- AJOUT SONARQUBE ----
        stage('Analyse SonarQube') {
            steps {
                script {
                    // 'SonarScanner' = nom de l'outil déclaré dans Manage Jenkins > Tools
                    def scannerHome = tool 'SonarScanner'
                    // 'SonarQube' = nom du serveur déclaré dans Manage Jenkins > System
                    withSonarQubeEnv('SonarQube') {
                        // Lance le scanner dans le dossier du projet, avec les réglages -D :
                        // projectKey/projectName = identifiant et nom du projet dans SonarQube
                        // sources = dossier à analyser (. = le dossier courant)
                        // java.binaries = où se trouvent les fichiers Java compilés
                        // exclusions = fichiers ignorés (les .class)
                        sh """
                            cd Hello_DEVOPS/ && ${scannerHome}/bin/sonar-scanner \
                              -Dsonar.projectKey=hello-devops \
                              -Dsonar.projectName=hello-devops \
                              -Dsonar.sources=. \
                              -Dsonar.java.binaries=. \
                              -Dsonar.exclusions=**/*.class
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                // Attend le verdict de SonarQube pendant 10 minutes maximum
                timeout(time: 10, unit: 'MINUTES') {
                    // Si le verdict est "refusé", la chaîne s'arrête
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        // ---- FIN AJOUT ----

        stage('Affichage') {
            steps {
                // Affiche un message final dans la console Jenkins
                sh "echo 'Hello World! lynda from group DevOps'"
            }
        }
    }
}

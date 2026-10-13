pipeline {
    agent any   // Jenkins peut utiliser n'importe quelle machine disponible
    stages {

        stage('clone') {
            steps {
                // Efface le contenu du dossier de travail pour repartir à zéro
                sh "rm -rf *"
                // Télécharge le projet depuis GitHub (le projet doit être public)
                sh "git clone https://github.com/lynda10091992/Hello_DEVOPS.git"
            }
        }

        stage('build') {
            steps {
                // Compile devops.java
                sh "cd Hello_DEVOPS/ && javac --release 21 devops.java"
            }
        }

        stage('Tests') {
            steps {
                // Télécharge JUnit (version 1.10.2, un seul fichier .jar)
                sh "curl -sSL -o junit.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar"
                // Compile le programme et le test
                sh "cd Hello_DEVOPS/ && javac --release 21 -cp ../junit.jar devops.java DevopsTest.java"
                // Lance les tests : si un test échoue, la chaîne s'arrête
                sh "cd Hello_DEVOPS/ && java -jar ../junit.jar execute --class-path . --scan-class-path"
            }
        }

        stage('run') {
            steps {
                // Lance le programme Java
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
                        // Lance le scanner dans le dossier du projet, avec les réglages -D
                        sh """
                            cd Hello_DEVOPS/
                            ${scannerHome}/bin/sonar-scanner
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

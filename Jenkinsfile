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
                // Compile le code placé dans src/main/java
                sh "cd Hello_DEVOPS/ && mvn compile"
            }
        }

        stage('Tests') {
            steps {
                // Maven compile et exécute les tests JUnit
               // Si un test échoue, cette étape échoue et le pipeline s'arrête
               sh "cd Hello_DEVOPS/ && mvn test"
            }
        }
        stage('packaging') {
            steps {
              // Crée le JAR dans target/ après compilation et tests
              sh "cd Hello_DEVOPS/ && mvn package"
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
        // Construit l'image uniquement après validation du Quality Gate
        stage('Docker Build') {
            steps {
                // Entre dans le dossier du dépôt cloné par le pipeline
                dir('Hello_DEVOPS') {
                    // Le point indique que Docker utilise ce dossier pour trouver le Dockerfile et le JAR dans target/
                    // Le tag ci identifie l'image construite par Jenkins
                    sh 'docker build -t hello-devops:ci .'
                }
            }
        }
        stage('Docker Push') {
            steps {
                // Récupère le compte et le token stockés dans Jenkins
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    sh '''
                        set +x
                        set -eu

                        # Utilise une configuration Docker temporaire
                        export DOCKER_CONFIG="$(mktemp -d)"
                        trap 'rm -rf "$DOCKER_CONFIG"' EXIT

                        # Se connecte sans afficher le token
                        printf '%s' "$DOCKERHUB_TOKEN" | docker login \
                            --username "$DOCKERHUB_USER" --password-stdin

                        # Publie l'image avec un tag propre à ce build Jenkins
                        IMAGE="$DOCKERHUB_USER/hello-devops:build-$BUILD_NUMBER"
                        docker tag hello-devops:ci "$IMAGE"
                        docker push "$IMAGE"
                    '''
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

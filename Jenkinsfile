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
                dir('Hello_DEVOPS') {
                    // Exécute les tests et génère le rapport grâce au plugin JaCoCo.
                    sh 'mvn clean test'

                    // Arrête immédiatement le build si le rapport manque.
                    sh 'test -s target/site/jacoco/jacoco.xml'

                }
            }
            post {
                always {
                    // Affiche les résultats des tests dans Jenkins.
                    junit 'Hello_DEVOPS/target/surefire-reports/TEST-*.xml'
                    
                    // Conserve les rapports comme artefacts du build.
                    archiveArtifacts(
                        artifacts: 'Hello_DEVOPS/target/site/jacoco/**',
                        allowEmptyArchive: true
                    )
                }
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
        stage('Déploiement backend') {
            steps {
                // Charge la clé SSH du backend pour Ansible
                sshagent(credentials: ['backend-ssh-key']) {
                    // Fournit à Ansible le mot de passe sudo sans saisie manuelle
                    withCredentials([string(
                        credentialsId: 'backend-sudo-password',
                        variable: 'ANSIBLE_BECOME_PASS'
                    ),
                    string(
                        credentialsId: 'postgres-app-password',
                        variable: 'POSTGRES_APP_PASSWORD'
                   ) 
                   ]) {
                        dir('Hello_DEVOPS') {
                            // Déploie le tag publié par ce même build
                            sh '''
                                ansible-playbook \
                                  -i ansible/inventory.ini \
                                  ansible/deploy-playbook.yml \
                                  --limit backend \
                                  -e "image_tag=build-$BUILD_NUMBER"
                            '''
                        }
                    }
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

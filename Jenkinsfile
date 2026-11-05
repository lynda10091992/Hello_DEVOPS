pipeline {
    agent any

    stages {
        stage('clone') {
            steps {
                // Prépare le dossier de travail Jenkins.
                deleteDir()
                sh 'git clone --branch main https://github.com/lynda10091992/Hello_DEVOPS.git'
            }
        }

        stage('build') {
            steps {
                sh 'cd Hello_DEVOPS && mvn compile'
            }
        }

        stage('Tests') {
            steps {
                dir('Hello_DEVOPS') {
                    // Exécute les tests et génère le rapport JaCoCo.
                    sh 'mvn clean test'
                    sh 'test -s target/site/jacoco/jacoco.xml'
                }
            }
            post {
                always {
                    junit 'Hello_DEVOPS/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts(
                        artifacts: 'Hello_DEVOPS/target/site/jacoco/**',
                        allowEmptyArchive: true
                    )
                }
            }
        }

        stage('packaging') {
            steps {
                sh 'cd Hello_DEVOPS && mvn package'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                script {
                    def scannerHome = tool 'SonarScanner'
                    withSonarQubeEnv('SonarQube') {
                        sh """
                            cd Hello_DEVOPS
                            "${scannerHome}/bin/sonar-scanner"
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
            post {
                failure {
                    withSonarQubeEnv(installationName: 'SonarQube', envOnly: true) {
                        sh '''
                            set +x
                            echo "Détail des conditions du Quality Gate :"
                            curl --silent --show-error \
                              --connect-timeout 5 --max-time 15 \
                              --write-out '\\nStatut HTTP : %{http_code}\\n' \
                              -H "Authorization: Bearer $SONAR_AUTH_TOKEN" \
                              "$SONAR_HOST_URL/api/qualitygates/project_status?projectKey=Hello_DEVOPS"
                        '''
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir('Hello_DEVOPS') {
                    sh 'docker build -t hello-devops:ci .'
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x
                        set -eu

                        export DOCKER_CONFIG="$(mktemp -d)"
                        trap 'rm -rf "$DOCKER_CONFIG"' EXIT

                        printf '%s' "$DOCKERHUB_TOKEN" | docker login \
                          --username "$DOCKERHUB_USER" --password-stdin

                        IMAGE="$DOCKERHUB_USER/hello-devops:build-$BUILD_NUMBER"
                        docker tag hello-devops:ci "$IMAGE"
                        docker push "$IMAGE"
                    '''
                }
            }
        }

        stage('Déploiement backend') {
            steps {
                sshagent(credentials: ['backend-ssh-key']) {
                    withCredentials([
                        string(
                            credentialsId: 'backend-sudo-password',
                            variable: 'ANSIBLE_BECOME_PASS'
                        ),
                        string(
                            credentialsId: 'postgres-app-password',
                            variable: 'POSTGRES_APP_PASSWORD'
                        )
                    ]) {
                        dir('Hello_DEVOPS') {
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

        stage('Affichage') {
            steps {
                echo 'Hello World! lynda from group DevOps'
            }
        }
    }
}

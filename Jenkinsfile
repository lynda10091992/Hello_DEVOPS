pipeline {
    agent any 
    stages {
        stage('clone') { 
            steps {
                sh "rm -rf *"
                sh "git clone https://github.com/lynda10091992/Hello_DEVOPS.git"
            }
        }
        stage('build') { 
            steps {
                sh "cd hello/ && javac --release 21 devops.java"
            }
        }
        stage('run') { 
            steps {
                sh "cd Hello_DEVOPS/ && java devops"
            }
        }
        stage('Affichage') { 
            steps {
                sh "echo 'Hello devops'"
            }
        }
    }
}

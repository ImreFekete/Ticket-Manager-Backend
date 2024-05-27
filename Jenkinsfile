pipeline {
    agent {
        node {
            label 'built_in_node'
        }
    }

    tools {
        jdk "jdk21"
        maven "maven"
    }
    stages {
        stage('Git Checkout') {
            steps {
                script {
                    git branch: env.BRANCH_NAME,
                        credentialsId: 'github_token_ImreFekete',
                        url: 'https://github.com/ImreFekete/Ticket-Manager-Backend.git'
                }
            }
        }
    stage('Compile') {
        steps {
            script {
                try {
                    sh 'mvn clean'
                    sh 'mvn compile'
                } catch (Exception e) {
                    handleFailure("Compile", e)
                }
            }
        }
    }

    stage('Test') {
        steps {
            script {
                try {
                    sh 'mvn test'
                    sh 'mvn verify'
                } catch (Exception e) {
                    handleFailure('Test', e)
                }
            }
        }
    }
}

def handleFailure(stage, exception) {
    currentBuild.result = 'FAILURE'
    error "Stage '${stage}' failed with exception: ${exception.message}"
}

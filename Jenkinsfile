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

        stage('Merge Development to Feature Branch') {
            when {
                expression { env.BRANCH_NAME ==~ /^feature\/.*$/ }
            }
            steps {
                script {
                    try {
                        sh """
                            git fetch origin
                            git checkout ${env.BRANCH_NAME}
                            git merge origin/development
                        """
                    } catch (Exception e) {
                        handleFailure("Merge Development to Feature", e)
                    }
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

        stage('Code Quality') {
            steps {
                script {
                    try {
                        withSonarQubeEnv('sonarqube') {
                            sh 'mvn sonar:sonar'
                        }
                    } catch (Exception e) {
                        handleFailure("Code Quality", e)
                    }
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}

def handleFailure(stage, exception) {
    currentBuild.result = 'FAILURE'
    error "Stage '${stage}' failed with exception: ${exception.message}"
}

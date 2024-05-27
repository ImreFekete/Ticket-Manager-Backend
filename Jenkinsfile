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

        stage('Merge Feature to Dev') {
            when {
                expression { env.BRANCH_NAME ==~ /^feature\/.*$/ }
            }
            steps {
                script {
                    try {
                        sh """
                            git fetch origin
                            git checkout development || git checkout -b development origin/development
                            git pull origin development
                            git merge ${env.BRANCH_NAME}
                            mvn test
                        """
                        withCredentials([usernamePassword(credentialsId: 'github_token_ImreFekete', usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
                            sh """
                                git push https://${USERNAME}:${TOKEN}@github.com/ImreFekete/Ticket-Manager-Backend.git development
                            """
                        }
                    } catch (Exception e) {
                        handleFailure("Merge feature to dev", e)
                    }
                }
            }
        }
    }

    post {
        failure {
            script {
                def recipients = emailextrecipients([[$class: 'DevelopersRecipientProvider']])
                def subject = "Pipeline Failed: ${currentBuild.fullDisplayName}"
                def buildUrl = "${env.JENKINS_URL}/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/"
                def errorLines = currentBuild.rawBuild.getLog().findAll { it.contains("ERROR") }.join('\n')
                def body = """
                    The pipeline ${currentBuild.fullDisplayName} has failed.
                    Job URL: ${buildUrl}
                    Build Number: ${env.BUILD_NUMBER}
                    Job Name: ${env.JOB_NAME}
                    Node: ${env.NODE_NAME}
                    Duration: ${currentBuild.durationString}
                    Error log:
                    ${errorLines}
                    Please check the job for more details.
                """
                mail to: recipients,
                    subject: subject,
                    body: body
            }
        }
    }
}

def handleFailure(stage, exception) {
    currentBuild.result = 'FAILURE'
    error "Stage '${stage}' failed with exception: ${exception.message}"
}

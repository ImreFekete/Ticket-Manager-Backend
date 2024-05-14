pipeline {
    agent { node { label 'built_in_node' } }

    tools {
        jdk "jdk21"
        maven "maven"
    }

    environment {
        SCANNER_HOME=tool 'sonar-scanner'
    }

    stages {
        stage('Git Checkout') {
            steps {
                git scm
            }
        }
        stage("Sonarqube Analysis"){
            steps{
                withSonarQubeEnv('sonarqube') {
                    sh ''' mvn clean package sonar:sonar'''

                }
            }
        }
    }
}

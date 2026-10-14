pipeline {
    agent any

    environment {
        registry = "tesnim123/docker-spring-boot"
        registryCredential = 'dockerHub'
        dockerImage = ''
        SONAR_URL = 'http://host.docker.internal:9000'
        NEXUS_URL = 'http://host.docker.internal:8081/repository/docker-spring-boot-releases/'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Unit Tests') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389:sonar -Dsonar.projectKey=docker-spring-boot -Dsonar.host.url=$SONAR_URL -Dsonar.token=$SONAR_TOKEN'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials',
                                                  usernameVariable: 'NEXUS_USER',
                                                  passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn -s settings-ci.xml deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::$NEXUS_URL'
                }
            }
        }

        stage('Building Docker Image') {
            steps {
                script {
                    dockerImage = docker.build registry + ":$BUILD_NUMBER"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }
    }
}
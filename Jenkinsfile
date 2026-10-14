pipeline {
    agent any

    parameters {
        choice(name: 'AWS_ACTION', choices: ['none', 'deploy', 'destroy'],
               description: 'none = local Kubernetes | deploy = Terraform creates EKS and deploys | destroy = Terraform destroys EKS')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        registry = "tesnim123/docker-spring-boot"
        registryCredential = 'dockerHub'
        dockerImage = ''
        SONAR_URL = 'http://host.docker.internal:9000'
        NEXUS_URL = 'http://host.docker.internal:8081/repository/docker-spring-boot-releases/'
        AWS_DEFAULT_REGION = 'us-east-1'
    }

    stages {
        stage('Checkout Git') {
            steps {
                checkout scm
            }
        }

        stage('Mvn Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Artifact Construction') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Mvn SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389:sonar -Dsonar.projectKey=docker-spring-boot -Dsonar.host.url=$SONAR_URL -Dsonar.token=$SONAR_TOKEN'
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials',
                                                  usernameVariable: 'NEXUS_USER',
                                                  passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn -s settings-ci.xml deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::$NEXUS_URL'
                }
            }
        }

        stage('Building our image') {
            steps {
                script {
                    dockerImage = docker.build registry + ":$BUILD_NUMBER"
                }
            }
        }

        stage('Deploy our image') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Deploy to local Kubernetes') {
            when { expression { params.AWS_ACTION == 'none' } }
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    sh 'sed "s|image: .*|image: $registry:$BUILD_NUMBER|" deployment.yaml | kubectl apply -f -'
                    sh 'kubectl apply -f service.yaml'
                    sh 'kubectl rollout status deployment/my-spring-boot-app --timeout=180s'
                }
            }
        }

        stage('AWS Delivery') {
            when { expression { params.AWS_ACTION != 'none' } }
            environment {
                AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
                AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
                AWS_SESSION_TOKEN     = credentials('aws-session-token')
            }
            stages {
                stage('Test AWS Credentials') {
                    steps {
                        sh 'aws sts get-caller-identity'
                    }
                }

                stage('Setup Terraform') {
                    steps {
                        dir('terraform') {
                            sh 'terraform init -input=false'
                            sh 'terraform validate'
                            script {
                                if (params.AWS_ACTION == 'destroy') {
                                    sh 'terraform destroy -auto-approve -input=false'
                                } else {
                                    sh 'terraform apply -auto-approve -input=false'
                                }
                            }
                        }
                    }
                }

                stage('Update Kubeconfig') {
                    when { expression { params.AWS_ACTION == 'deploy' } }
                    steps {
                        sh 'aws eks update-kubeconfig --name mykubernetes --region $AWS_DEFAULT_REGION --kubeconfig $WORKSPACE/kubeconfig-aws'
                    }
                }

                stage('Deploy to AWS Kubernetes') {
                    when { expression { params.AWS_ACTION == 'deploy' } }
                    steps {
                        sh '''
                            export KUBECONFIG=$WORKSPACE/kubeconfig-aws
                            sed "s|image: .*|image: $registry:$BUILD_NUMBER|" deployment.yaml | kubectl apply -f -
                            kubectl apply -f service.yaml
                            kubectl rollout status deployment/my-spring-boot-app --timeout=300s
                            kubectl get nodes -o wide
                            kubectl get svc spring-boot-service
                        '''
                    }
                }
            }
        }
    }
}
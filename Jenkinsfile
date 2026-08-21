pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }
    environment {
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '674650726975'
        ECR_REPOSITORY = 'bookmyshow-app'
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        IMAGE_TAG = "build-${BUILD_NUMBER}"
        DEPLOYMENT_NAME = 'bookmyshow-app'
        CONTAINER_NAME = 'bookmyshow-app'
    }

    stages {

        stage('AWS Identity') {
            steps {
                sh '''
                    set -e
                    echo "===== AWS IDENTITY ====="
                    aws sts get-caller-identity
                '''
            }
        }

        stage('EKS Connection') {
            steps {
                sh '''
                    set -e
                    echo "===== EKS CONNECTION ====="

                    aws eks update-kubeconfig \
                      --region ${AWS_REGION} \
                      --name bookmyshow-eks

                    kubectl get nodes
                '''
            }
        }

        stage('ECR Login') {
            steps {
                sh '''
                    set -e
                    echo "===== ECR LOGIN ====="

                    aws ecr get-login-password \
                      --region ${AWS_REGION} | \
                    docker login \
                      --username AWS \
                      --password-stdin ${ECR_REGISTRY}
                '''
            }
        }


        stage('Build & Test') {
    steps {
        sh '''
            set -e

            echo "===== BUILD & TEST ====="

            mvn clean test
        '''
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
        
        
        stage('Build Docker Image') {
            steps {
                sh '''
                    set -e
                    echo "===== BUILDING DOCKER IMAGE ====="

                    docker build \
                      -t ${ECR_REPOSITORY}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Tag Docker Image') {
            steps {
                sh '''
                    set -e
                    echo "===== TAGGING IMAGE ====="

                    docker tag \
                      ${ECR_REPOSITORY}:${IMAGE_TAG} \
                      ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                    set -e
                    echo "===== PUSHING IMAGE TO ECR ====="

                    docker push \
                      ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy to EKS using Ansible') {
            steps {
                sh '''
                    set -e

                    echo "===== ANSIBLE EKS DEPLOYMENT ====="

                    ansible-playbook \
                      ansible/deploy-bookmyshow.yml \
                      -i ansible/inventory.ini \
                      -e "image=${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    set -e

                    echo "===== DEPLOYMENT STATUS ====="

                    kubectl get deployment ${DEPLOYMENT_NAME}

                    echo "===== PODS ====="

                    kubectl get pods -A -o wide

                    echo "===== SERVICE ====="

                    kubectl get svc -n default bookmyshow-service
                '''
            }
        }
    }

    post {
        success {
            echo 'BookMyShow CI/CD pipeline completed successfully!'
        }

        failure {
            echo 'BookMyShow CI/CD pipeline failed.'
        }
    }
}

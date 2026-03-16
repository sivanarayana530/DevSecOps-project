pipeline {
    agent any
    stages {
        stage('Checkout git') {
            steps {
               git branch: 'main', url: 'https://github.com/sivanarayana530/DevSecOps-project.git'
            }
        }
        
        stage ('Build & JUnit Test') {
            steps {
                sh 'mvn install' 
            }
            post {
               success {
                    junit 'target/surefire-reports/**/*.xml'
                }   
            }
        }
        stage('SonarQube Analysis'){
            steps{
                withSonarQubeEnv('SonarQube-server') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=devsecops-project-key -Dsonar.login=$SONAR_TOKEN'
                    }
                }
            }
        }
        stage("Quality Gate") {
            steps {
              timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
              }
            }
        }
        
        stage('Docker  Build') {
            steps {
      	        sh 'docker build -t praveensirvi/sprint-boot-app:v1.$BUILD_ID .'
                sh 'docker image tag praveensirvi/sprint-boot-app:v1.$BUILD_ID praveensirvi/sprint-boot-app:latest'
            }
        }
        stage('Image Scan') {
            steps {
      	        sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image --format table praveensirvi/sprint-boot-app:latest > report.txt || true'
            }
        }
        stage('Store Scan report locally') {
              steps {
                  sh 'mkdir -p reports && cp report.txt reports/'
              }
         }
        stage('Docker  Push') {
            steps {
                sh 'echo "Skipping Docker push to avoid needing credentials"'
                // sh 'docker push praveensirvi/sprint-boot-app:v1.$BUILD_ID'
                // sh 'docker push praveensirvi/sprint-boot-app:latest'
                // sh 'docker rmi praveensirvi/sprint-boot-app:v1.$BUILD_ID praveensirvi/sprint-boot-app:latest'
            }
        }
        stage('Deploy to k8s') {
            steps {
                sh 'kind create cluster --name devsecops-cluster --config kind-config.yaml || true'
                sh 'kind load docker-image praveensirvi/sprint-boot-app:latest --name devsecops-cluster'
                sh 'kubectl apply -f spring-boot-deployment.yaml'
                sh 'kubectl rollout status deployment/spring-app-deployment'
            }
        }
        
 
    }
    post{
        always{
            echo "Pipeline completed"
            // sendSlackNotifcation()
            }
        }
}

    

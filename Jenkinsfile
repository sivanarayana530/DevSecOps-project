pipeline {
    agent any
    stages {
        stage('Checkout git') {
            steps {
               git branch: 'main', url: 'https://github.com/praveensirvi1212/DevSecOps-project'
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
                        sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=devsecops-project-key'
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
      	        sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/workspace aquasec/trivy:latest image --format json -o /workspace/report.json praveensirvi/sprint-boot-app:latest || true'
            }
        }
        stage('Store Scan report locally') {
              steps {
                  sh 'mkdir -p reports && cp report.json reports/'
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
        stage('Deploy locally') {
            steps {
                sh 'docker run -d -p 8080:8080 --name spring-app praveensirvi/sprint-boot-app:latest'
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

    

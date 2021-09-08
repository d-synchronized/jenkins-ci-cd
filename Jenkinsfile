#!/usr/bin/env groovy

node () { //node('worker_node')
   
   try {
      stage('Checkout Source Code') { 
          def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
          checkout([$class: 'GitSCM', 
                    branches: [[name: "*/${params.BRANCH}"]], 
                    extensions: [], 
                    userRemoteConfigs: [[credentialsId: 'github-credentials', url: "${repoUrl}"]]])
      }
      
      
      stage('Drop SNAPSHOT') {
          def externalMethod = load("gitMethods.groovy")
          externalMethod.exampleMethod()
      }
      
      stage('Create TAG'){
          
      }
      
   
     stage('Build & Deploy Artifact') {
     }
     
     stage('Deploy Artifact') {
     }
     
     stage('Increment Development Version'){
       }
     
       currentBuild.result = 'SUCCESS'
       error("Build failed because of this and that..")
   } catch(Exception err) {
      echo "Error occurred while running the job '${env.JOB_NAME}'"
   } finally {
       //deleteDir()
       echo '***************************************************'
       echo '***************************************************'
       echo '****POST******BUILD*****ACTION*********START*******'
       echo '****POST******BUILD*****ACTION*********END*********'
       echo '***************************************************'
       echo '***************************************************'
   }
   
}

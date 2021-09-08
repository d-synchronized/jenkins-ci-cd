#!/usr/bin/env groovy

def checkoutSourceCode(String repositoryUrl, String credentials, String branch){
    checkout([$class: 'GitSCM', 
                    branches: [[name: "*/${branch}"]], 
                    extensions: [], 
                    userRemoteConfigs: [[credentialsId: 'github-credentials', url: repositoryUrl]]])
}


node () { //node('worker_node')
   def externalMethod
   def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
   try {
      stage('Checkout Source Code') { 
          checkoutSourceCode(repoUrl , 'github-credentials', 'master');
          externalMethod = load("scripts/gitMethods.groovy")
      }
      
      
      stage('Drop SNAPSHOT') {
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

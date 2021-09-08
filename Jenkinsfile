#!/usr/bin/env groovy

node () { //node('worker_node')
   
   try {
      stage('Checkout Source Code') { 
      }
      
      
      stage('Drop SNAPSHOT') {
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

def deleteTag(String tagVersionCreated) { 
}
   
def revertParentPOM(String previousPomVersion) {
}

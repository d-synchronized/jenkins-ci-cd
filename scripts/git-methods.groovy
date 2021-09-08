def checkourSourceCode(String branchName, String credentialsId, String repositoryUrl) {
   checkout([$class: 'GitSCM', 
               branches: [[name: "${branchName}"]], 
               extensions: [], 
               userRemoteConfigs: [[credentialsId: "${credentialsId}", url: "${repositoryUrl}"]]])
}

return this;
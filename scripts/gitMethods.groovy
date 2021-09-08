def checkourSourceCode(String branchName, String credentialsId, String repositoryUrl) {
   echo 'inside the checkout source code method'
   checkout([$class: 'GitSCM', 
               branches: [[name: "${branchName}"]], 
               extensions: [], 
               userRemoteConfigs: [[credentialsId: "${credentialsId}", url: "${repositoryUrl}"]]])
}

return this;
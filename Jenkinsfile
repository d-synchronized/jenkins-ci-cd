//Groovy Pipeline
node () { //node('worker_node')
   properties([
      parameters([
           gitParameter(branchFilter: 'origin/(.*)', defaultValue: 'development', name: 'BRANCH', type: 'PT_BRANCH'),
           choice(choices: ['DEV', 'QA' , 'PROD'], name: 'ENVIRONMENT'),
           string(defaultValue: 'http://localhost:8082/', name: 'SERVER', trim: true),
           string(defaultValue: '', name: 'VERSION', trim: true, description: 'If VERSION is specified, artifact will be downloaded from Repository'),
           booleanParam(defaultValue: false,  name: 'DEPLOY_FROM_REPO', description: 'If DEPLOY_FROM_REPO is specified and version is not specified, most recent artifact will be downloaded from Repository')
      ]),
      disableConcurrentBuilds()
   ])
   
   def server
   def rtMaven = Artifactory.newMavenBuild()
   def buildInfo
   def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
   
   def artifactId
   def devPomVersion
   def qaPomVersion
   
   def targetFolder
   def pattern
   def failNoOp
   try {
      stage('Clone') { 
         echo "***Checking out source code from repo url ${repoUrl},branchName ${params.BRANCH}, deploy from repo ${params.DEPLOY_FROM_REPO}***"
         checkout([$class: 'GitSCM', 
               branches: [[name: "*/${params.BRANCH}"]], 
               extensions: [], 
               userRemoteConfigs: [[credentialsId: 'github-credentials', url: "${repoUrl}"]]])
               
         def externalMethod = load("scripts/git-methods.groovy")
         externalMethod("*/${branchName}", 'github-credentials', 'https://github.com/d-synchronized/spring-boot-ci-cd-demo.git')
      }
      
      stage ('Artifactory Configuration') {
        // Obtain an Artifactory server instance, defined in Jenkins --> Manage Jenkins --> Configure System:
        server = Artifactory.server 'DSYNC_JFROG_INSTANCE'

        // Tool name from Jenkins configuration
        rtMaven.tool = 'MAVEN_BUILD_TOOL'
        rtMaven.deployer releaseRepo: 'cetera-maven-releases', snapshotRepo: 'cetera-maven-snapshots', server: server
        //rtMaven.resolver releaseRepo: 'cetera-maven-virtual-releases', snapshotRepo: 'cetera-maven-virtual-snapshots', server: server
        buildInfo = Artifactory.newBuildInfo()
      }
      
      
      stage('Build & Deploy') {
         DEPLOY_TO_PROD = "${params.ENVIRONMENT}"  == 'PROD' ? true : false
         DEPLOY_TO_QA = "${params.ENVIRONMENT}" == 'QA' ? true : false
         DEPLOY_TO_DEV = "${params.ENVIRONMENT}"  == 'DEV' ? true : false
         
         DEPLOY_FROM_REPO = "${params.DEPLOY_FROM_REPO}" == 'false' ? false : true
         
         VERSION_REQUESTED = "${params.VERSION}"  != '' ? true : false
         
         if(VERSION_REQUESTED 
            && (!DEPLOY_TO_DEV && "${params.VERSION}".contains("SNAPSHOT"))){
           error("Invalid Version! , Reason - Version should not contain SNAPSHOT, for all the build required other than ${params.ENVIRONMENT} Environment")
         }
         
         if(VERSION_REQUESTED 
            && (DEPLOY_TO_DEV && !"${params.VERSION}".contains("SNAPSHOT"))){
           error("Invalid Version! , Reason - Version should contain SNAPSHOT, for all the build required for ${params.ENVIRONMENT} Environment")
         }
         
         pom = readMavenPom file: 'pom.xml'
         devPomVersion = "${pom.version}"
         artifactId = "${pom.artifactId}"
         if("${params.BRANCH}" == 'development' && !DEPLOY_FROM_REPO && !VERSION_REQUESTED){
            if(DEPLOY_TO_DEV){
               echo "Building SNAPSHOT Artifact"
               rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
               server.publishBuildInfo buildInfo
               
               echo "****Dropping SNAPSHOT from the version***"
               bat "mvn versions:set -DremoveSnapshot -DgenerateBackupPoms=false"
               echo "Building RELEASE Artifact"
               rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
               server.publishBuildInfo buildInfo
               pom = readMavenPom file: 'pom.xml'
            } else if(DEPLOY_TO_QA || DEPLOY_TO_PROD){
               echo "Dropping SNAPSHOT from the version"
               bat "mvn versions:set -DremoveSnapshot -DgenerateBackupPoms=false"
               
               pom = readMavenPom file: 'pom.xml'
            }  
            qaPomVersion = "${pom.version}"
         }//if development branch ends here
         else if ("${params.BRANCH}" != 'development' && (VERSION_REQUESTED || DEPLOY_FROM_REPO)){
            echo "Artifacts are not uploaded for branches other than Development Branch, Please try again!"
            currentBuild.result = 'FALIURE'
            error("Can not select DEPLOY_FROM_REPO / VERSION for branches other than Development Branch, Please try again without VERSION and DEPLOY_FROM_REPO")
         } else if (!VERSION_REQUESTED){
            if(DEPLOY_TO_QA || DEPLOY_TO_PROD){
               echo "Dropping SNAPSHOT from the version"
               bat "mvn versions:set -DremoveSnapshot -DgenerateBackupPoms=false"
               
               pom = readMavenPom file: 'pom.xml'
               qaPomVersion = "${pom.version}"
            }
            if(!DEPLOY_FROM_REPO){
               rtMaven.deployer.deployArtifacts = false
               rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
            }
         }
     }
     
     
     stage('Download Artifact') {
         if("${params.BRANCH}" == 'development'){
             VERSION_REQUESTED = "${params.VERSION}"  != '' ? true : false
             VERSION_STRING = VERSION_REQUESTED ? "${params.VERSION}" : DEPLOY_TO_DEV ? "${devPomVersion}" : "${qaPomVersion}"
             build =   VERSION_REQUESTED ? "${env.JOB_NAME}" : "${env.JOB_NAME}/LATEST"
             
             if(DEPLOY_TO_DEV) {
                targetFolder = "${artifactId}/SNAPSHOTS/${VERSION_STRING}/"
                pattern = "cetera-maven-snapshots/com/example/${artifactId}/${VERSION_STRING}/${artifactId}-*.war"
             }else{
                targetFolder = "${artifactId}/RELEASES/${VERSION_STRING}/"
                pattern = "cetera-maven-releases/com/example/${artifactId}/${VERSION_STRING}/${artifactId}-*.war"
             }
             
             dir ("${targetFolder}") {
               echo "deleting the target folder ${targetFolder}"
               deleteDir()
             }
             
             echo "Downloading ARTIFACT Version ${VERSION_STRING} , Artitfactory Pattern ${pattern}  ,Target Loction  ${targetFolder},BuildName  ${env.JOB_NAME}"
             def downloadSpec = """{
                                  "files": [
                                              {
                                                "pattern": "${pattern}",
                                                "target": "${targetFolder}",
                                                "recursive": "true",
                                                "flat" : "true",
                                                "sortBy" : [ "created" ],
                                                "sortOrder" : "desc",
                                                "limit": "1"
                                              }
                                           ]
                               }"""
            buildInfo = server.download spec: downloadSpec, failNoOp: true                      
         }else {
             echo "*****SKIPPING 'Download Artifact Step' for Branch - ${params.BRANCH}, Branch - ${params.BRANCH} *******"
         }
     }//Download Artifact ends here
      
     
     stage('Publish To Application/Web Server') {
         pom = readMavenPom file: 'pom.xml'
         if("${params.BRANCH}" != 'development'){
            deploy adapters: [tomcat8(url: "${SERVER}", credentialsId: 'tomcat')], war: "target/*.war", contextPath: "${artifactId}"
         }else{
            if(!failNoOp){
               deploy adapters: [tomcat8(url: "${SERVER}", credentialsId: 'tomcat')], war: "${targetFolder}/*.war", contextPath: "${artifactId}"
            }
         }
      }//Publish To Application/Web Server stage ends here
     
      currentBuild.result = 'SUCCESS'
   } catch(Exception err) {
      echo "Error occurred while running the job '${env.JOB_NAME}' , $err"
      currentBuild.result = 'FALIURE'
      //revertParentPOM("${previousPomVersion}")
      if("${params.RELEASE}" == 'true'){
         deleteTag("${tagVersionCreated}")
      }
   } finally {
       //deleteDir()
   }
   
}
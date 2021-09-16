//DSL Pipeline / Declarative Pipeline
node () { 
    properties([
       parameters([
         [
           $class: 'ChoiceParameter', 
           choiceType: 'PT_CHECKBOX', 
           //description: 'Is this a release?', 
           filterLength: 1, 
           filterable: false, 
           name: 'release', 
           script: [
              $class: 'GroovyScript', 
              fallbackScript: [
                  classpath: [], 
                  sandbox: false, 
                  script: 
                     "return['Could not get release']"
              ], 
              script: [
                  classpath: [], 
                  sandbox: false, 
                  script: 
                    "return['Yes']"
              ]
           ]
         ],//Choice Parameters ends here
         [
           $class: 'CascadeChoiceParameter', 
           choiceType: 'PT_SINGLE_SELECT', 
           description: 'Select the Environemnt from the Dropdown List', 
           filterLength: 1, 
           filterable: false, 
           name: 'Env', 
           referencedParameters: 'release', 
           script: [
              $class: 'GroovyScript', 
              fallbackScript: [
                  classpath: [], 
                  sandbox: false, 
                  script: 
                     "return['Could not get The environemnts']"
              ], 
              script: [
                  classpath: [], 
                  sandbox: false, 
                  script: '''
                              if (release.equals("Yes")){
                                 return["PROD"]
                              } else {
                                 return["DEV","QA"]
                              }
                           '''
              ]
           ]
        ],//Choice Parameters ends here
        [
           $class: 'CascadeChoiceParameter', 
           choiceType: 'PT_MULTI_SELECT', 
           description: 'Select the server from the Dropdown List',
           name: 'SERVER_LIST', 
           referencedParameters: 'Env', 
           script: [
               $class: 'GroovyScript', 
               fallbackScript: [
                   classpath: [], 
                   sandbox: false, 
                   script: "return['Could not get Environment from Env Param']"
               ], 
               script: [
                   classpath: [], 
                   sandbox: false, 
                   script: '''
                              if (Env.equals("DEV")){
                                 return["dweblnl025", "dapplnl052"]
                              } else if(Env.equals("QA")){
                                 return[ "QWEBLNL001" , "QWEBLNL022", "QWEBLNL023"]
                              } else if(Env.equals("PROD")){
                                 return["PWEBLSL087", "PWEBLSL088", "PWEBLSL30a" ,  "PWEBLSL30b"]
                              }
                           '''
               ] 
           ]
        ],//Cascade Choide Parameter ends here
        gitParameter(branchFilter: 'origin/(.*)', defaultValue: 'development', name: 'BRANCH', type: 'PT_BRANCH'),
        gitParameter(branchFilter: 'origin/(.*)', defaultValue: '', name: 'TAG', type: 'PT_TAG'),
        string(defaultValue: '', name: 'version', trim: true, description: 'Which version to deploy?')
      ])//parameters ends here
   ])//properties ends here
   
   def rtMaven = Artifactory.newMavenBuild()
   
   def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
   def pom
   def commonUtils
   
   def buildInfo
   def TAG_SELECTED = false
   try {
     stage('Clone') { 
       IS_RELEASE = "${params.release}" == 'Yes' ? true : false
       TAG_SELECTED = "${params.TAG}" != '' ? true : false
       echo "${params.TAG_SELECTED}"
       if(TAG_SELECTED){
         echo "***Checking out source code from repo url ${repoUrl},tagName ${params.TAG}***"
         checkout([
                  $class: 'GitSCM', 
                  branches: [[name: "*/${params.BRANCH}"]], 
                  extensions: [], 
                  userRemoteConfigs: [[credentialsId: 'github-dsync-token-mb', url: "${repoUrl}" , branches: [[name: 'refs/tags/${params.TAG}']]]]
         ])//checkout ends here
       }else {
         echo "***Checking out source code from repo url ${repoUrl},branchName ${params.BRANCH}***"
         checkout([
                  $class: 'GitSCM', 
                  branches: [[name: "*/${params.BRANCH}"]], 
                  extensions: [], 
                  userRemoteConfigs: [[credentialsId: 'github-dsync-token-mb', url: "${repoUrl}" ]]
         ])//checkout ends here
       }
             
             
       pom = readMavenPom file: 'pom.xml'
       commonUtils = load("scripts/CommonUtils.groovy")
       
       bat "git config user.name 'Dishant Anand'"
       bat "git config user.email d.synchronized@gmail.com"
     }//clone stage ends here
      
     stage ('Artifactory Configuration') {
       // Obtain an Artifactory server instance, defined in Jenkins --> Manage Jenkins --> Configure System:
       server = Artifactory.server 'DSYNC_JFROG_INSTANCE'

       // Tool name from Jenkins configuration
       rtMaven.tool = 'MAVEN_BUILD_TOOL'
       rtMaven.deployer releaseRepo: 'cetera-maven-releases', snapshotRepo: 'cetera-maven-snapshots', server: server
       //rtMaven.resolver releaseRepo: 'cetera-maven-virtual-releases', snapshotRepo: 'cetera-maven-virtual-snapshots', server: server
       buildInfo = Artifactory.newBuildInfo()
     }
     
     def TAG_CREATED = false
     stage ('Create TAG') {
       IS_RELEASE = "${params.release}" == 'Yes' ? true : false
       if(IS_RELEASE && !TAG_SELECTED){
       
         ARTIFACT_ALREADY_PRESENT = commonUtils.checkIfArtifactAlreadyExistInRepo("${pom.artifactId}" , "${pom.version}" , false)
         if(ARTIFACT_ALREADY_PRESENT){
            echo "**RELEASE : Create TAG stage will be skipped, Reason - Release artifact with version ${pom.version} already available in JFROG!**"
         }//if ends here 
         else {
           echo "**RELEASE : Creating TAG for  artifact ${pom.artifactId} against version ${pom.version}**"
           NEW_TAG = "RELEASE-${pom.version}"
           withCredentials([usernamePassword(credentialsId: 'github-dsync-token-mb', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
             bat "git tag -a ${NEW_TAG} -m \"pushing TAG VERSION ${NEW_TAG}\""
             bat "git push https://${env.GIT_USERNAME}:${env.GIT_PASSWORD}@github.com/d-synchronized/jenkins-ci-cd.git --tags"
           }//with credentials ends here
           
           TAG_CREATED = true
           echo "**RELEASE : Created TAG for artifact ${pom.artifactId} against version ${pom.version}**"
         }//else ends here
       }else if(IS_RELEASE && TAG_SELECTED){
         echo "*****************************************************************************"
         echo "*********************[ ENVIRONMENT-${params.Env} ]****************************"
         echo "**Create TAG stage will be skipped, Reason TAG ${params.TAG} selected********"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
       }else{
         echo "*****************************************************************************"
         echo "*********************[ ENVIRONMENT-${params.Env} ]****************************"
         echo "**Create TAG stage will be skipped, Reason IS_RELEASE set to ${release}*******"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
       }
     }
     
     def SNAPSHOT_CREATED = false
     stage("Drop SNAPSHOT"){
       IS_RELEASE = "${params.release}" == 'Yes' ? true : false
       if(IS_RELEASE){
         if(TAG_CREATED || TAG_SELECTED){
           echo "RELEASE : Dropping '-SNAPSHOT' from the artifact version against artifactId '${pom.artifactId}' and version '${pom.version}'"
           bat "mvn versions:set -DremoveSnapshot -DgenerateBackupPoms=false"
           pom = readMavenPom file: 'pom.xml'
           SNAPSHOT_CREATED = true
           echo "**RELEASE : Completed Drop SNAPSHOT stage, ArtifactId - ${pom.artifactId}, Version - ${pom.version}**"
         } else {
           echo "**RELEASE FAILED : TAG Creation Failed**"
         }
       } 
       else {
         echo "*****************************************************************************"
         echo "*********************[ ENVIRONMENT-${params.Env} ]****************************"
         echo "**Drop SNAPSHOT stage will be skipped, Reason IS_RELEASE set to ${release}****"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
       }
     }
     
     
   
     def uploadArtifact = false
     stage('Build & Publish To Artifactory') {
       IS_RELEASE = "${params.release}" == 'Yes' ? true : false
       DEPLOY_TO_QA = "${params.Env}" == 'QA' ? true : false
       DEPLOY_TO_DEV = "${params.Env}"  == 'DEV' ? true : false
       DEPLOY_TO_PROD = "${params.Env}"  == 'PROD' ? true : false
       VERSION_REQUESTED = "${params.version}"  != '' ? true : false
       echo "${DEPLOY_TO_QA} , ${DEPLOY_TO_DEV}, ${VERSION_REQUESTED}"
       /**
       * Donot Build if
       * 1. We are doing a release, in that case we will promote the artifact
       * 2. We are deploying to QA and version is not requested
       * 3. We are deploying to Prod and version is not requested
       **/
       if(IS_RELEASE){
          if( TAG_CREATED && SNAPSHOT_CREATED) {
            echo "**RELEASE : Building artifact ${pom.artifactId} against version ${pom.version}**"
            rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
            server.publishBuildInfo buildInfo
            uploadArtifact = true
            echo "**RELEASE : Successfully Build artifact ${pom.artifactId} against version ${pom.version}**"
          } else if(TAG_SELECTED){
            echo "**Build from TAG : Building Artifact ${pom.artifactId} with Version ${pom.version}**"
          bat([script: 'mvn clean install']) 
            echo "**Build from TAG : Building Artifact ${pom.artifactId} with Version ${pom.version}**"
          } else{
            echo "**RELEASE FAILED : Previous Stages(Create TAG / Drop SNAPSHOt ) Failed!!**"
          }
       }else if(VERSION_REQUESTED){
          echo "*****************************************************************************"
          echo "*****************************************************************************"
          echo "**Build Artifact stage will be skipped, Reason VERSION_REQUESTED set to ${release}**"
          echo "*****************************************************************************"
          echo "*****************************************************************************"
          echo "*****************************************************************************"
       } else if(TAG_SELECTED){
          echo "**Build from TAG : Building Artifact ${pom.artifactId} with Version ${pom.version}**"
          bat([script: 'mvn clean install']) 
          echo "**Build from TAG : Building Artifact ${pom.artifactId} with Version ${pom.version}**"
       } else{
          //def downloadSnapshot = DEPLOY_TO_DEV ? true : false
          //It is assumed the same snapshot will be depoyed to both dev and QA, promotion will happen in prod
          def downloadSnapshot = true
          ARTIFACT_ALREADY_PRESENT = commonUtils.checkIfArtifactAlreadyExistInRepo("${pom.artifactId}" , "${pom.version}" , true)
          if(ARTIFACT_ALREADY_PRESENT && DEPLOY_TO_DEV){
            echo "**artifact ${pom.artifactId} against version ${pom.version} already available in the repository**"
            def userInput = true
            try{
              timeout(time: 30, unit: 'SECONDS') {
                userInput = input(id: 'rebuild', message: 'Artifact Already exist! Do you wish to rebuild?', 
                                    parameters: [
                                                  [  
                                                    $class: 'BooleanParameterDefinition', 
                                                    defaultValue: false, 
                                                    description: '', 
                                                    name: 'Please confirm you agree with this'
                                                  ]
                                                ]
                                    )
              }
            }catch(err) {
              def user = err.getCauses()[0].getUser()
              if('SYSTEM' == user.toString()) { // SYSTEM means timeout.
                echo "**Artifact already exists!, No response received from the user for Re - Build"
                userInput = false
              } else {
                echo "**Artifact already exists!, Re - Build request aborted by the user"
                userInput = false
              }
            }
            
            if (userInput == true) {
              echo "**Building artifact ${pom.artifactId} against version ${pom.version}**"
              rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
              server.publishBuildInfo buildInfo
              uploadArtifact = true
              echo "**Successfully Build artifact ${pom.artifactId} against version ${pom.version}**"
            } 
            
          } // if block ends here 
          else if(ARTIFACT_ALREADY_PRESENT && DEPLOY_TO_QA){
            echo "*****************************************************************************"
            echo "********************[ ENVIRONMENT-${params.Env} ]****************************"
            echo "**Build Artifact stage will be skipped, Reason -Artifact was found in JFROG!**"
            echo "*****************************************************************************"
            echo "*****************************************************************************"
            echo "*****************************************************************************"
          } //else if ends here 
          else {
            if(DEPLOY_TO_DEV){
              //it is assumed code will only come here when the deploy to DEV is selected and the artifact doesnot exist in the JFROG
              echo "**Building artifact ${pom.artifactId} against version ${pom.version}**"
              rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
              server.publishBuildInfo buildInfo
              uploadArtifact = true
              echo "**Successfully Build artifact ${pom.artifactId} against version ${pom.version}**"
            } else{
              //it is assumed code will only come here when the deploy to QA is selected and the artifact doesnot exist in the JFROG
              //at this point promotion should take place
            }
          } //else ends here
          
       }//main block else ends here
     }
     
     def artifactDownloaded = false
     stage ("Download Artifact From JFROG"){
       def targetWarDirectory = "${pom.artifactId}/${pom.version}/"
       
       dir("${targetWarDirectory}"){
         deleteDir()
       }
       
       if(TAG_SELECTED){
         dir("target") {
           fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: '*.war', targetLocation: "${targetWarDirectory}")])
         }
       } else {
         IS_RELEASE = "${params.release}" == 'Yes' ? true : false
         artifactDownloaded = commonUtils.checkIfArtifactAlreadyExistInRepo("${pom.artifactId}" , "${pom.version}" , IS_RELEASE)
       }
     }
     
     stage ('Deploy') {
       if(artifactDownloaded){
         def sourceWarDirectory = "${pom.artifactId}/${pom.version}"
         deploy adapters: [tomcat8(url: "http://localhost:8082/", credentialsId: 'tomcat')], war: "${sourceWarDirectory}/*.war", contextPath: "${artifactId}"
       }
     }
     
   }//try ends here
   catch(Exception err) {
      echo "Error occurred while running the job '${env.JOB_NAME}' , $err"
      currentBuild.result = 'FALIURE'
   } finally {
   
   }
}

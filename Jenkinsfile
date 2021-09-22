//Groovy Pipeline
node () { //node('worker_node')

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
                                 return["UAT"]
                              } else {
                                 return["DEV","QA"]
                              }
                           '''
              ]
           ]
        ],//Choice Parameters ends here
        gitParameter(branchFilter: 'origin/(.*)', defaultValue: 'development', name: 'BRANCH', type: 'PT_BRANCH')
      ]),
      disableConcurrentBuilds()
   ])
   
   
   def repoUrl = 'https://github.com/d-synchronized/common-services.git'
   def pom
   def commonUtils
   
   def server
   def rtMaven = Artifactory.newMavenBuild()
   def buildInfo
   
   try{
     stage ('Clone') { 
       echo "***Checking out source code from repo url ${repoUrl},branchName ${params.BRANCH}***"
       checkout([
                  $class: 'GitSCM', 
                  branches: [[name: "*/${params.BRANCH}"]], 
                  extensions: [], 
                  userRemoteConfigs: [[credentialsId: 'github-dsync-token-mb', url: "${repoUrl}" ]]
         ])//checkout ends here
       pom = readMavenPom file: 'pom.xml'
       commonUtils = load("scripts/commonUtils.groovy")
       
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
     }// Configure artifactory
     
     def TAG_CREATED = false
     stage ('Create TAG') {
       IS_RELEASE = "${params.release}" == 'Yes' ? true : false
       if(IS_RELEASE){
         ARTIFACT_ALREADY_PRESENT = commonUtils.checkIfArtifactAlreadyExistInRepo("${pom.artifactId}" ,
                                                                                 "${pom.version}" , 
                                                                                  IS_RELEASE ? false : true,
                                                                                  server)
         if(ARTIFACT_ALREADY_PRESENT){
            echo "**RELEASE : Create TAG stage will be skipped, Reason - Release artifact with version ${pom.version} already available in JFROG!**"
         }//if ends here 
         else {
           echo "**RELEASE : Creating TAG for  artifact ${pom.artifactId} against version ${pom.version}**"
           def NEW_TAG = "RELEASE-${pom.version}"
           withCredentials([usernamePassword(credentialsId: 'github-dsync-token-mb', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
             bat "git tag -a ${NEW_TAG} -m \"pushing TAG VERSION ${NEW_TAG}\""
             bat "git push https://${env.GIT_USERNAME}:${env.GIT_PASSWORD}@github.com/d-synchronized/jenkins-ci-cd.git --tags"
           }//with credentials ends here
           
           TAG_CREATED = true
           echo "**RELEASE : Created TAG for artifact ${pom.artifactId} against version ${pom.version}**"
         }//else ends here
       } else {
         echo "*****************************************************************************"
         echo "*********************[ ENVIRONMENT-${params.Env} ]****************************"
         echo "**Create TAG stage will be skipped, Reason IS_RELEASE set to ${release}*******"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
         echo "*****************************************************************************"
       }
     }//create tag stage ends here
     
     def SNAPSHOT_CREATED = false
     stage("Drop SNAPSHOT"){
       IS_RELEASE = "${params.release}" == 'Yes' ? true : false
       if(IS_RELEASE && TAG_CREATED){
         echo "RELEASE : Dropping '-SNAPSHOT' from the artifact version against artifactId '${pom.artifactId}' and version '${pom.version}'"
         bat "mvn versions:set -DremoveSnapshot -DgenerateBackupPoms=false"
         pom = readMavenPom file: 'pom.xml'
         SNAPSHOT_CREATED = true
         echo "**RELEASE : Completed Drop SNAPSHOT stage, ArtifactId - ${pom.artifactId}, Version - ${pom.version}**"
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
       /**
       * Donot Build if
       * 1. We are doing a release, in that case we will promote the artifact
       * 2. We are deploying to QA and version is not requested
       * 3. We are deploying to Prod and version is not requested
       **/
       ARTIFACT_ALREADY_PRESENT = commonUtils.checkIfArtifactAlreadyExistInRepo("${pom.artifactId}" ,
                                                                                 "${pom.version}" , 
                                                                                  IS_RELEASE ? false : true,
                                                                                  server)
       if(ARTIFACT_ALREADY_PRESENT){
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
           uploadArtifact = commonUtils.buildAndPublish("${pom.artifactId}" , "${pom.version}", rtMaven , buildInfo, server)
           echo "**Successfully Build artifact ${pom.artifactId} against version ${pom.version}**"
         }
          //build job: 'RunArtInTest', parameters: [[$class: 'StringParameterValue', name: 'systemname', value: systemname]]
       } else{
         uploadArtifact = commonUtils.buildAndPublish("${pom.artifactId}" , "${pom.version}", rtMaven, buildInfo, server)
       }
     }
     
   } catch(Exception exception){
     echo "Error occurred while running the job '${env.JOB_NAME}' , $exception"
     currentBuild.result = 'FALIURE'
   }
   
}

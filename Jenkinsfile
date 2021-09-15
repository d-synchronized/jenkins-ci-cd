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
        string(defaultValue: '', name: 'version', trim: true, description: 'Which version to deploy?')
      ])//parameters ends here
   ])//properties ends here
   
   def rtMaven = Artifactory.newMavenBuild()
   
   def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
   def pom
   def commonUtils
   
   def buildInfo
   try {
     stage('Clone') { 
       echo "***Checking out source code from repo url ${repoUrl},branchName ${params.BRANCH}, deploy from repo ${params.DEPLOY_FROM_REPO}***"
       checkout([
                  $class: 'GitSCM', 
                  branches: [[name: "*/${params.BRANCH}"]], 
                  extensions: [], 
                  userRemoteConfigs: [[credentialsId: 'github-credentials', url: "${repoUrl}"]]
               ])//checkout ends here
             
             
       pom = readMavenPom file: 'pom.xml'
       commonUtils = load("scripts/CommonUtils.groovy")
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
   
     def uploadArtifact = false
     stage('Build Artifact') {
       IS_RELEASE = "${params.release}" == 'true' ? true : false
       DEPLOY_TO_QA = "${params.Env}" == 'QA' ? true : false
       DEPLOY_TO_DEV = "${params.Env}"  == 'DEV' ? true : false
       DEPLOY_TO_PROD = "${params.Env}"  == 'PROD' ? true : false
       VERSION_REQUESTED = "${params.version}"  != '' ? true : false
     
       /**
       * Donot Build if
       * 1. We are doing a release, in that case we will promote the artifact
       * 2. We are deploying to QA and version is not requested
       * 3. We are deploying to Prod and version is not requested
       **/
       if(IS_RELEASE){
          echo "*****************************************************************************"
          echo "*****************************************************************************"
          echo "**Build Artifact stage will be skipped, Reason IS_RELEASE set to ${release}**"
          echo "*****************************************************************************"
          echo "*****************************************************************************"
          echo "*****************************************************************************"
       }else if(VERSION_REQUESTED){
          echo "*****************************************************************************"
          echo "*****************************************************************************"
          echo "**Build Artifact stage will be skipped, Reason VERSION_REQUESTED set to ${release}**"
          echo "*****************************************************************************"
          echo "*****************************************************************************"
          echo "*****************************************************************************"
       } else{
          //def downloadSnapshot = DEPLOY_TO_DEV ? true : false
          //It is assumed the same snapshot will be depoyed to both dev and QA, promotion will happen in prod
          def downloadSnapshot = true
          def artifactBuildInfo = commonUtils.downloadArtifacts( 
                                                                 commonUtils.prepareSearchPattern("${pom.artifactId}" , "${pom.version}" , downloadSnapshot),
                                                                 commonUtils.prepareTargetFolder("${pom.artifactId}" , "${pom.version}" , downloadSnapshot),
                                                               );
                                                           
          if(artifactBuildInfo != null && DEPLOY_TO_DEV){
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
              } else {
                userInput = false
                echo "**Artifact already exists!, Re - Build request aborted by the user"
              }
            }
            
            if (userInput == true) {
              echo "**Building artifact ${pom.artifactId} against version ${pom.version}**"
              rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
              uploadArtifact = true
              echo "**Successfully Build artifact ${pom.artifactId} against version ${pom.version}**"
            } 
            
          } // if block ends here 
          else if(artifactBuildInfo != null && DEPLOY_TO_QA){
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
              uploadArtifact = true
              echo "**Successfully Build artifact ${pom.artifactId} against version ${pom.version}**"
            } else{
              //it is assumed code will only come here when the deploy to QA is selected and the artifact doesnot exist in the JFROG
              //at this point promotion should take place
            }
          } //else ends here
          
       }//main block else ends here
     }
     
     stage("Deploy To JFROG"){
       if(uploadArtifact){
         echo "**Publishing artifact ${pom.artifactId}, version ${pom.version} to JFROG Repository**"
         server.publishBuildInfo buildInfo
       }
     }
     
     stage ('Promote Release Artifact') {
        IS_RELEASE = "${params.release}" == 'true' ? true : false
        
        
        if(IS_RELEASE){
          def downloadSnapshot = false
          def artifactBuildInfo = commonUtils.downloadArtifacts( commonUtils.prepareTargetFolder("${pom.artifactId}" , "${pom.version}" , downloadSnapshot),
                                                                 commonUtils.prepareSearchPattern("${pom.artifactId}" , "${pom.version}" , downloadSnapshot)
                                                                );
          if(artifactBuildInfo != null){
            echo "*****************************************************************************"
            echo "********************[ ENVIRONMENT-${params.Env} ]****************************"
            echo "**Promote Release Artifact Stage skipped,Reason-Artifact was found in JFROG**"
            echo "*****************************************************************************"
            echo "*****************************************************************************"
            echo "*****************************************************************************"
          } else {
            echo "**Executing Promote Release Artifact Stage**"
            //Promote the snapshot artifact from JFROG
            downloadSnapshot = true
            artifactBuildInfo = commonUtils.downloadArtifacts( commonUtils.prepareTargetFolder("${pom.artifactId}" , "${pom.version}" , downloadSnapshot),
                                                                 commonUtils.prepareSearchPattern("${pom.artifactId}" , "${pom.version}" , downloadSnapshot)
                                                                );
                                                                
            promotionConfig = [
              //Mandatory parameters
              'buildName'          : artifactBuildInfo.name,
              'buildNumber'        : artifactBuildInfo.number,
              'targetRepo'         : 'cetera-maven-releases',

              //Optional parameters
              'comment'            : 'Promoting the artifact for production',
              'sourceRepo'         : 'cetera-maven-snapshots',
              'status'             : 'Released',
              'includeDependencies': true,
              'failFast'           : true,
              'copy'               : true
            ]
            // Promote build on the JFROG
            server.promote promotionConfig
            echo "**Completed Promote Release Artifact Stage**"
          }//else ends here
          
        }//stage promotion ends here

    }
     
   }//try ends here
   catch(Exception err) {
      echo "Error occurred while running the job '${env.JOB_NAME}' , $err"
      currentBuild.result = 'FALIURE'
   } finally {
   
   }
}

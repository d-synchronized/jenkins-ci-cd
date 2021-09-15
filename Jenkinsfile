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
   
   def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
   def pom
   def commonUtils
   
   def buildInfo
   
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
   
   stage('Build Artifact') {
     IS_RELEASE = "${params.release}" == 'false' ? false : true
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
     if(IS_RELEASE || VERSION_REQUESTED || (DEPLOY_TO_QA && !VERSION_REQUESTED) || (DEPLOY_TO_PROD && !VERSION_REQUESTED)){
     }
     
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
     }
     
     def downloadSnapshot = DEPLOY_TO_DEV ? true : false
     
     def artifactBuildInfo = commonUtils.downloadArtifacts( downloadSnapshot , 
                                                    commonUtils.prepareTargetFolder("${pom.artifactId}" , "${params.VERSION}" , downloadSnapshot),
                                                    commonUtils.prepareSearchPattern("${pom.artifactId}" , "${params.VERSION}" , downloadSnapshot)
                                                  );
     
     echo artifactBuildInfo 
   }
   
}

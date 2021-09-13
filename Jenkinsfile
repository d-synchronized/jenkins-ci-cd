//Groovy Pipeline
node () { //node('worker_node')
   properties([
      parameters([
           gitParameter(branchFilter: 'origin/(.*)', defaultValue: 'development', name: 'BRANCH', type: 'PT_BRANCH'),
           choice(choices: ['DEV', 'QA' , 'PROD'], name: 'ENVIRONMENT'),
           activeChoiceParam('Service') {
            description('Select service you wan to deploy')
            choiceType('SINGLE_SELECT')
            groovyScript {
                script("return ['web-service', 'proxy-service', 'backend-service']")
                fallbackScript('"fallback choice"')
            }
           },
           string(defaultValue: 'http://localhost:8082/', name: 'SERVER', trim: true),
           string(defaultValue: '', name: 'VERSION', trim: true, description: 'If VERSION is specified, artifact will be downloaded from Repository'),
           //booleanParam(defaultValue: false,  name: 'DEPLOY_FROM_REPO', description: 'If DEPLOY_FROM_REPO is specified and version is not specified, most recent artifact will be downloaded from Repository'),
           booleanParam(defaultValue: false,  name: 'RELEASE', description: 'Deploys to Dev and QA, additionally increments the minor version to 1 until 10 versions are reached')
      ]),
      disableConcurrentBuilds()
   ])
   
   def server
   def rtMaven = Artifactory.newMavenBuild()
   def buildInfo
   def repoUrl = 'https://github.com/d-synchronized/spring-boot-ci-cd-demo.git'
   
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
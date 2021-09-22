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
              $class: 'ScriptlerScript', 
              fallbackScript: [
                  classpath: [], 
                  sandbox: false, 
                  script: 
                     "return['Could not get The environemnts']"
              ],
              parameters: [[$class: 'org.biouno.unochoice.model.ScriptlerScriptParameter', name: 'release', value: '$release']],
              scriptlerScriptId: 'scripts/serverList.groovy' 
           ]
        ],//Choice Parameters ends here
      ]),
      disableConcurrentBuilds()
   ])
   def repoUrl = 'https://github.com/d-synchronized/common-services.git'
   try{
     stage('Clone') { 
       echo "***Checking out source code from repo url ${repoUrl},branchName ${params.BRANCH}***"
         checkout([
                  $class: 'GitSCM', 
                  branches: [[name: "*/${params.BRANCH}"]], 
                  extensions: [], 
                  userRemoteConfigs: [[credentialsId: 'github-dsync-token-mb', url: "${repoUrl}" ]]
         ])//checkout ends here
     }//clone stage ends here
   } catch(Exception exception){
     //
   }
   
}
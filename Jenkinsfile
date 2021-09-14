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
           $class: 'ChoiceParameter', 
           choiceType: 'PT_SINGLE_SELECT', 
           //description: 'Is this a release?', 
           filterLength: 1, 
           filterable: false, 
           name: 'branch', 
           script: [
              $class: 'GroovyScript', 
              fallbackScript: [
                  classpath: [], 
                  sandbox: false, 
                  script: 
                     "return['development']"
              ], 
              script: [
                  classpath: [], 
                  sandbox: false, 
                  script: [   
                          return load('scripts/CommonUtils.groovy').fetchAvailableBranches() 
                         ]
                  
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
        string(defaultValue: '', name: 'VERSION', trim: true, description: 'Manually enter a version')
      ])//parameters ends here
   ])//properties ends here
   
   stage ('Artifactory Configuration') {
        def repoUrl = 'https://github.com/d-synchronized/jenkins-ci-cd.git'
        checkout([$class: 'GitSCM', 
               branches: [[name: "*/${params.BRANCH}"]], 
               extensions: [], 
               userRemoteConfigs: [[credentialsId: 'github-credentials', url: "${repoUrl}"]]])
   
   
        def cu = load('scripts/CommonUtils.groovy')
        def branches = cu.fetchAvailableBranches()   
        echo "${branches}"
      }
   
}

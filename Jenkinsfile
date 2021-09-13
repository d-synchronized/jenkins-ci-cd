//DSL Pipeline / Declarative Pipeline
node () { 
   
    properties([
       parameters([
         [
           $class: 'ChoiceParameter', 
           choiceType: 'PT_SINGLE_SELECT', 
           description: 'Select the Environemnt from the Dropdown List', 
           filterLength: 1, 
           filterable: false, 
           name: 'Env', 
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
                  script: 
                    "return['DEV','QA','PROD']"
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
   
   stage('Access Parameters') {
             echo "Release Type is ${params.SERVER_LIST}"
             echo "Selected Branch is ${params.BRANCH}"
             echo "Build Reason is ${params.VERSION}"
      }
      stage('Source') { 
            bat([script: 'echo ****cloning the code****'])
            //git ([branch: 'day-1', url: 'https://github.com/d-synchronized/ci-cd-demo.git'])
      }
      stage('Build') {
             bat([script: 'echo ****build command goes here****']) 
             //bat([script: 'mvn clean install']) 
      }
}

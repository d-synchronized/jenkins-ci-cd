//DSL Pipeline / Declarative Pipeline
pipeline {
   agent any
   
   parameters{
        gitParameter(branchFilter: 'origin/(.*)', defaultValue: 'development', name: 'BRANCH', type: 'PT_BRANCH')
        choice(choices: ['DEV', 'QA' , 'PROD'], name: 'ENVIRONMENT')
        string(defaultValue: 'http://localhost:8082/', name: 'SERVER', trim: true)
        string(defaultValue: '', name: 'VERSION', trim: true, description: 'If VERSION is specified, artifact will be downloaded from Repository')
        booleanParam(defaultValue: false,  name: 'DEPLOY_FROM_REPO', description: 'If DEPLOY_FROM_REPO is specified and version is not specified, most recent artifact will be downloaded from Repository')
        activeChoiceParam('Service') {
            description('Select service you wan to deploy')
            choiceType('SINGLE_SELECT')
            groovyScript {
                script(return ['web-service', 'proxy-service', 'backend-service'])
                fallbackScript('"fallback choice"')
            }
        }
   }
   
   stages{
      stage('Access Parameters') {
         steps {
             echo "Release Type is ${params.releaseType}"
             echo "Selected Branch is ${params.branchInput}"
             echo "Build Reason is ${params.buildReason}"
         }
      }
      stage('Source') { 
         steps {
            bat([script: 'echo ****cloning the code****'])
            //git ([branch: 'day-1', url: 'https://github.com/d-synchronized/ci-cd-demo.git'])
         }
      }
      stage('Build') {
         steps {
             bat([script: 'echo ****build command goes here****']) 
             //bat([script: 'mvn clean install']) 
         }
      }
   }
}

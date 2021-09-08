//DSL Pipeline / Declarative Pipeline
pipeline {
   agent any
   
   parameters {
       booleanParam(defaultValue: true, description: 'Is Release?', name: 'releaseType')
       choice(choices: ['development', 'master'], description: 'Choose the branch', name: 'branchInput')
       string(description: 'Reason for the Build', name: 'buildReason', trim: true)
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

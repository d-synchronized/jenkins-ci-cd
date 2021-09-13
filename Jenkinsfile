//DSL Pipeline / Declarative Pipeline
pipeline {
   agent any
   
   parameters([
                                gitParameter(branchFilter: 'origin/(.*)', defaultValue: 'development', name: 'BRANCH', type: 'PT_BRANCH'),
                                [$class: 'ChoiceParameter', 
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
                                ],
                                [$class: 'CascadeChoiceParameter', 
                                    choiceType: 'PT_MULTI_SELECT', 
                                    description: 'Select the Servers from the Dropdown List',
                                    name: 'AMI List', 
                                    referencedParameters: 'Env', 
                                    script: 
                                        [$class: 'GroovyScript', 
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
                                                    return["ami-sd2345sd", "ami-asdf245sdf", "ami-asdf3245sd"]
                                                }
                                                else if(Env.equals("QA")){
                                                    return["ami-sd34sdf", "ami-sdf345sdc", "ami-sdf34sdf"]
                                                }
                                                else if(Env.equals("PROD")){
                                                    return["ami-sdf34sdf", "ami-sdf34ds", "ami-sdf3sf3"]
                                                }
                                                '''
                                            ] 
                                    ]
                                ]
    ])
   
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

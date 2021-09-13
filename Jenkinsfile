//DSL Pipeline / Declarative Pipeline
node () { 
   
    properties([
                            parameters([
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
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select the AMI from the Dropdown List',
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
                                                    return["dweblnl025", "dapplnl052"]
                                                }
                                                else if(Env.equals("QA")){
                                                    return[ "QWEBLNL001" , "QWEBLNL022", "QWEBLNL023"]
                                                }
                                                else if(Env.equals("PROD")){
                                                    return["PWEBLSL087", "PWEBLSL088", "PWEBLSL30a" ,  "PWEBLSL30b"]
                                                }
                                                '''
                                            ] 
                                    ]
                                ]
                            ])
                        ])
   
   stage('Access Parameters') {
             echo "Release Type is ${params.releaseType}"
             echo "Selected Branch is ${params.branchInput}"
             echo "Build Reason is ${params.buildReason}"
      }
      stage('Source') { 
            bat([script: 'echo ****cloning the code****'])
            git ([branch: 'day-1', url: 'https://github.com/d-synchronized/ci-cd-demo.git'])
      }
      stage('Build') {
             bat([script: 'echo ****build command goes here****']) 
             bat([script: 'mvn clean install']) 
      }
}

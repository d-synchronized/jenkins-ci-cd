pipeline {
    agent any
    environment {
        ENV_NAME = "${env.BRANCH_NAME}"
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
    }

    // ----------------

    stages {
        stage('Build Container') {
            steps {
                echo "Building Container..${ENVIRONMENT_NAME}"

                script {
                    if (ENVIRONMENT_NAME == 'development') {
                        ENV_NAME = 'Development'
                    } else if (ENVIRONMENT_NAME == 'release') {
                        ENV_NAME = 'Production'
                    }
                }
                echo 'Building Branch: ' + env.BRANCH_NAME
                echo 'Build Number: ' + env.BUILD_NUMBER
                echo 'Building Environment: ' + ENV_NAME

                echo "Running your service with environemnt ${ENV_NAME} now"
            }
        }
    }
}
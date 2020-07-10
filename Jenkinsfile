pipeline {

    agent any

    environment {
        JBOSS_SERVER_SECRET = "sso-extensions-jboss-server"
        JBOSS_SERVER_DEPLOY_PATH = "/home/ec2-user/rh-sso-7.4/standalone/deployments"
    }


    stages {

        stage ('Build') {
            steps {
//                sh 'mvn help:all-profiles'
                sh "mvn clean"
            }
        }

        stage ('Deploy') {
            steps {
                sshagent(credentials : ["nvs-philip"]) {
                    sh "ls -la"
                    sh "scp -o StrictHostKeyChecking=no ./target/rhsso-extensions-1.0-SNAPSHOT.jar ec2-user@ec2-3-130-236-137.us-east-2.compute.amazonaws.com:/home/ec2-user/rh-sso-7.4/standalone/deployments/rhsso-extensions.jar"
                }
            }
        }
    }

}
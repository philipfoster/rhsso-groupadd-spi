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
                // To run on linux, change "bat" to "sh"
                sh "mvn clean"
            }
        }

        stage ('Deploy') {
            steps {
                // To run on linux, change "bat" to "sh"
//                withCredentials([usernamePassword(credentialsId: "${JBOSS_SERVER_SECRET}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//                    bat "mvn jboss-as:deploy -Ddeploy.jboss.host=ec2-3-130-236-137.us-east-2.compute.amazonaws.com -Ddeploy.jboss.port=9990 -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}"
////                    scp -i ~/aws/awskeys/nvs-philip.pem rh-sso-7.4.0.zip ec2-user@ec2-18-218-223-97.us-east-2.compute.amazonaws.com:/home/ec2-user
////                    bat "scp target/rhsso-extensions-1.0-SNAPSHOT.jar ${USERNAME}@ec2-18-218-223-97.us-east-2.compute.amazonaws.com:/home/ec2-user/rh-sso-7.4/standalone/deployments"
//                }

                sshagent(credentials : ["nvs-philip"]) {
                    sh "scp ./target/rhsso-extensions-1.0-SNAPSHOT.jar ec2-user@ec2-3-130-236-137.us-east-2.compute.amazonaws.com:/home/ec2-user/rh-sso-7.4/standalone/deployments"
                }
            }
        }
    }

}
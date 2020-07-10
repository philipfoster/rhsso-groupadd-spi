pipeline {

    agent any


    parameters {
        string(
                name: 'JBOSS_HOST',
                description: "The hostname of the JBoss server to deploy to"
        )
        string(
                name: 'JBOSS_PORT',
                description: "The port of the JBoss server to deploy to"
        )
    }

    environment {
        JBOSS_SERVER_SECRET = "sso-extensions-jboss-server"
    }


    stages {

        stage ('Build') {
            steps {
//                sh 'mvn help:all-profiles'
                // To run on linux, change "bat" to "sh"
                bat "mvn clean"
            }
        }

        stage ('Deploy') {
            steps {
                // To run on linux, change "bat" to "sh"
                withCredentials([usernamePassword(credentialsId: "${JBOSS_SERVER_SECRET}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    bat "mvn jboss-as:deploy -Ddeploy.jboss.host=${JBOSS_HOST} -Ddeploy.jboss.port=${JBOSS_PORT} -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}"
                }
            }
        }
    }

}
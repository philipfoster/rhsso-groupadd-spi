pipeline {

    agent any


    parameters {
        string(
                name: 'jboss_host',
                description: "The hostname of the JBoss server to deploy to"
        )
        string(
                name: 'jboss_port',
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
                    bat "mvn wildfly:deploy -Ddeploy.jboss.host=${params.jboss_host} -Ddeploy.jboss.port=${params.jboss_port} -Ddeploy.jboss.user=${USERNAME} -Ddeploy.jboss.password=${PASSWORD}"
                }
            }
        }
    }

}
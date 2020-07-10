pipeline {

    agent any

    stages {

        stage ('Build') {
            steps {
//                sh 'mvn help:all-profiles'
                bat "mvn clean install -P\\!jboss-ga-repository -P\\!jboss-earlyaccess-repository"
            }
        }
    }

}
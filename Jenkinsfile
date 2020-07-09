pipeline {

    agent any

    stages {

        stage ('Build') {
            steps {
//                sh 'mvn help:all-profiles'
                sh "mvn clean install -P\\!jboss-ga-repository -P\\!jboss-earlyaccess-repository"
            }
        }
    }

}
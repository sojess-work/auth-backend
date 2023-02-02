node {
  def image
   stage ('checkout') {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: 'https://@github.com/sojess-work/auth-backend.git']]])
        }

   stage ('Build') {
         def mvnHome = tool name: 'Maven', type: 'maven'
         def mvnCMD = "${mvnHome}/bin/mvn "
         sh "${mvnCMD} clean package"
        }
    stage('Build image') {

       app = docker.build("verdant-tempest-376308/myauthapp-backend")
    }

    stage('Push image to gcr') {
        docker.withRegistry('https://gcr.io', 'gcr:auth-app') {
            app.push("auth-0.0.1-SNAPSHOT")
        }
    }

}
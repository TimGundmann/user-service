node {
   def mvnHome
   stage('Preparation') { 
      git 'https://github.com/TimGundmann/eureka-service.git'
      env.JAVA_HOME="${tool 'jdk 8'}"
      env.PATH="/var/lib/jenkins/.local/bin:${env.PATH}"
      mvnHome = tool 'maven 3.3.9'
   }
   stage("version update") {
       pom = readMavenPom file: 'pom.xml'
       newVersion = pom.version + "." + env.BUILD_NUMBER
       sh "'${mvnHome}/bin/mvn' versions:set -DnewVersion=${newVersion}"
       currentBuild.displayName = newVersion
   }
   stage('Build') {
     sh "'${mvnHome}/bin/mvn' clean package"
   }
   stage('Results') {
      junit '**/target/surefire-reports/TEST-*.xml'
      archive 'target/*.jar'
   }
   stage('Docker Deploy') {
        sh "docker-compose stop"
        sh "docker-compose build"
        sh "docker-compose up -d"
   }
}
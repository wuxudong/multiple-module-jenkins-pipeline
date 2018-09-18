def call(Map pipelineParams) {
    pipeline {
        agent any
        environment {
            branch = ${pipelineParams.branch}
            scmUrl = ${pipelineParams.scmUrl}
            serviceName = ${pipelineParams.serviceName}
        }
        stages {
            stage('checkout git') {
                steps {
                    git branch: branch, url: scmUrl
                }
            }

            stage('build') {
                steps {
                    sh 'mvn clean package -DskipTests=true'
                }
            }

            stage('deploy'){
                steps {
                    sh "JENKINS_NODE_COOKIE=dontKillMe nohup java -jar ${pipelineParams.serviceName}/target/${pipelineParams.serviceName}.jar &"
                }
            }

        
        }
    
    }
}

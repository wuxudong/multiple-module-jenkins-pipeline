def call(Map pipelineParams) {
    pipeline {
        agent any
        environment {
            branch = ${pipelineParams.branch} ? "${pipelineParams.branch}" : 'master'
            scmUrl = ${pipelineParams.scmUrl} ? "${pipelineParams.scmUrl}" : 'https://github.com/wuxudong/spring-cloud-best-practice.git'
            serviceName = "${pipelineParams.serviceName}"
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

                    sh "echo `ps -ef | grep ${serviceName} | grep -v grep | awk '{print \$2}'`"
                    sh "JENKINS_NODE_COOKIE=dontKillMe nohup java -jar ${serviceName}/target/${serviceName}.jar &"
                }
            }

        
        }
    
    }
}

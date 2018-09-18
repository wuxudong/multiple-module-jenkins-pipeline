def call(Map pipelineParams) {
    def getOrDefault(value, defaultValue) {
        return value ? value : defaultValue
    }

    pipeline {
        agent any
        environment {
            branch = getOrDefault("${pipelineParams.branch}","master")
            scmUrl = getOrDefault("${pipelineParams.scmUrl}","https://github.com/wuxudong/spring-cloud-best-practice.git")
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

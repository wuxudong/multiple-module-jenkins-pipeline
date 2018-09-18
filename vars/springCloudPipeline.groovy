import com.github.wuxudong.pipeline.utils.TernaryUtils

def call(Map pipelineParams) {
   

    pipeline {
        agent any
        environment {
            branch = TernaryUtils.getOrDefault(pipelineParams.branch,"master")
            scmUrl = TernaryUtils.getOrDefault(pipelineParams.scmUrl,"https://github.com/wuxudong/spring-cloud-best-practice.git")
            serviceName = "${pipelineParams.serviceName}"
        }
        stages {
            stage('checkout git') {
                steps {

                    sh "echo ${pipelineParams.branch} ${branch} ${pipelineParams.scmUrl} ${scmUrl}"
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
                    sh "echo `ps -ef | grep ${serviceName}.jar | grep -v grep | awk '{print \$2}'`"
                    sh "JENKINS_NODE_COOKIE=dontKillMe nohup java -jar ${serviceName}/target/${serviceName}.jar &"
                }
            }

        
        }
    

    }
  
}

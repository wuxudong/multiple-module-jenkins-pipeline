import com.github.wuxudong.pipeline.utils.TernaryUtils





def call(Map pipelineParams) {
    def jobs = ["JobA", "JobB", "JobC"]

    def stageGenerator = load "stageGenerator.groovy"
    
    def parallelStagesMap = jobs.collectEntries {
       ["${it}" : stageGenerator.generateStage(it)]
    }

    
    pipeline {
        agent any
        environment {
            branch = TernaryUtils.getOrDefault(pipelineParams.branch,"master")
            scmUrl = TernaryUtils.getOrDefault(pipelineParams.scmUrl,"https://github.com/wuxudong/spring-cloud-best-practice.git")
            serviceName = "${pipelineParams.serviceName}"
        }
        stages {
            stage('parallel stage') {
                steps {
                    script {
                        parallel parallelStagesMap
                    }
                }
            }

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
                    sh "ps -ef | grep ${serviceName}.jar | grep -v grep | awk '{print \$2}' | xargs kill"
                    sh "JENKINS_NODE_COOKIE=dontKillMe nohup java -jar ${serviceName}/target/${serviceName}.jar &"
                }
            }

        
        }
    

    }
  
}

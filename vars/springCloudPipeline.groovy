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
            stage('parallel stage') {
                steps {
                    script {
                        def jobs = ["JobA", "JobB", "JobC"]

                      
                        
                        def parallelStagesMap = jobs.collectEntries {
                           ["${it}" : {
                                stage("stage: ${it}") {
                                        echo "This is ${it}."
                                        sh script: "sleep 15"
                                }
                            }]
                        }

                        stage("stage: just wait") {
                                        echo "This is wait."
                                        sh script: "sleep 15"
                        }

                        stage("stage: just wait again") {
                                        echo "This is wait again."
                                        sh script: "sleep 15"
                        }
                        

                        for (def index = 0; index < jobs.size(); index++) {
                            stage("stage: ${jobs[index]}") {
                                echo "This is ${jobs[index]}."
                                sh script: "sleep 15"
                            }
                        }
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

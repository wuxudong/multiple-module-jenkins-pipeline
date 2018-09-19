def generateStage(job) {
    return {
        stage("stage: ${job}") {
                echo "This is ${job}."
                sh script: "sleep 15"
        }
    }
}


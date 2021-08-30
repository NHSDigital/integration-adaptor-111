String tfProject     = "nia"
String tfEnvironment = "build1" // change for ptl, vp goes here
String tfComponent   = "OneOneOne"  // this defines the application - nhais, mhs, 111 etc

Map<String,String> tfOutputs = [:] //map for collecting values from tfOutput

pipeline {
    agent{
        label 'jenkins-workers'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: "10")) // keep only last 10 builds
    }
    
    environment {
        BUILD_TAG = sh label: 'Generating build tag', returnStdout: true, script: 'python3 scripts/tag.py ${GIT_BRANCH} ${BUILD_NUMBER} ${GIT_COMMIT}'
        BUILD_TAG_LOWER = sh label: 'Lowercase build tag', returnStdout: true, script: "echo -n ${BUILD_TAG} | tr '[:upper:]' '[:lower:]'"
        ENVIRONMENT_ID = "build1"
        ECR_REPO_DIR = "111"
        ECR_REPO_DIR_NGINX = "111-nginx"
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/${ECR_REPO_DIR}:${BUILD_TAG}"
        DOCKER_NGINX_IMAGE = "${DOCKER_REGISTRY}/${ECR_REPO_DIR_NGINX}:${BUILD_TAG}"
    }    

    stages {
        stage('Test') {
            steps {
                script {
                    if (sh(label: 'Build image for tests', script: 'docker build -t local/111-tests:${BUILD_TAG} -f Dockerfile.tests .', returnStatus: true) != 0) {error("Failed to build docker image for tests")}
                    if (sh(label: 'Running docker tests container', script: 'docker run -v /var/run/docker.sock:/var/run/docker.sock --name 111-tests-${BUILD_TAG} local/111-tests:${BUILD_TAG} ./gradlew check -i --continue --rerun-tasks', returnStatus: true) != 0) {error("Failed to run docker tests container")}
                }
            }
            post {
                always {
                    sh label: 'Create logs and reports directories', script: 'mkdir -p logs build/reports'
                    sh label: 'Copy 111-tests container logs', script: 'docker logs 111-tests-${BUILD_TAG} > logs/111-tests.log'
                    archiveArtifacts artifacts: 'logs/*.log', fingerprint: true
                    sh label: 'Copy 111-tests container reports', script: 'docker cp 111-tests-${BUILD_TAG}:/home/gradle/service/build/reports/. ./build/reports'
                    archiveArtifacts artifacts: 'build/reports/**/*.*', fingerprint: true
                    sh label: 'Stop docker container', script: 'docker stop 111-tests-${BUILD_TAG}'
                    sh label: 'Remove docker container', script: 'docker rm 111-tests-${BUILD_TAG}'
                    recordIssues(
                        enabledForFailure: true,
                        tools: [
                            checkStyle(pattern: 'build/reports/checkstyle/*.xml'),
                            spotBugs(pattern: 'build/reports/spotbugs/*.xml')
                        ]
                    )
                }
            }
        }
        stage('Build') {
            stages {
                stage('Build Docker Images') {
                    steps {
                        script {
                            if (sh(label: 'Running 111 docker build', script: 'docker build -t ${DOCKER_IMAGE} -f Dockerfile.111 .', returnStatus: true) != 0) {error("Failed to build 111 Docker image")}
                            if (sh(label: 'Running nginx docker buid', script: 'docker build -t ${DOCKER_NGINX_IMAGE} -f Dockerfile.nginx .', returnStatus: true) != 0) {error("Failed to build Nginx Docker image")}
                        }
                    }
                }
                stage('Push Image') {
                    when {
                        expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
                    }
                    steps {
                        script {
                            if (ecrLogin(TF_STATE_BUCKET_REGION) != 0 )  { error("Docker login to ECR failed") }
                            String dockerPushCommand = "docker push ${DOCKER_IMAGE}"
                            String dockerNginxPushCommand = "docker push ${DOCKER_NGINX_IMAGE}"
                            if (sh (label: "Pushing image", script: dockerPushCommand, returnStatus: true) !=0) { error("Docker push 111 image failed") }
                            if (sh (label: "Pushing image", script: dockerNginxPushCommand, returnStatus: true) !=0) { error("Docker push nginx image failed") }
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            options {
              lock("${tfProject}-${tfEnvironment}-${tfComponent}")
            }
            stages {
              stage('Deploy using Terraform') {
                steps {
                  script {
                    String tfCodeBranch  = "develop"
                    String tfCodeRepo    = "https://github.com/nhsconnect/integration-adaptors"
                    String tfRegion      = TF_STATE_BUCKET_REGION

                    List<String> tfParams = []
                    Map<String,String> tfVariables = ["${tfComponent}_build_id": BUILD_TAG]

                    dir ("integration-adaptors") {
                      // Clone repository with terraform
                      git (branch: tfCodeBranch, url: tfCodeRepo)
                      dir ("terraform/aws") {
                        // Run TF Init
                        if (terraformInit(TF_STATE_BUCKET, tfProject, tfEnvironment, tfComponent, tfRegion) !=0) { error("Terraform init failed")}

                        // Run TF Plan
                        if (terraform('plan', TF_STATE_BUCKET, tfProject, tfEnvironment, tfComponent, tfRegion, tfVariables) !=0 ) { error("Terraform Plan failed")}

                        //Run TF Apply
                        if (terraform('apply', TF_STATE_BUCKET, tfProject, tfEnvironment, tfComponent, tfRegion, tfVariables) !=0 ) { error("Terraform Apply failed")}
                        tfOutputs = collectTfOutputs(tfComponent)
                      }
                    }
                  }
                }
              }
              stage ('Verify AWS Deployment') {
                steps {
                  script {
                    sleep(60)
                    if (checkLbTargetGroupHealth(tfOutputs["${tfComponent}_lb_target_group_arn"], TF_STATE_BUCKET_REGION) != 0) { error("AWS healthcheck failed, check the CloudWatch logs")}
                  }
                }
              }
              // stage('Run integration tests') {
              //     steps {
              //         echo 'TODO run integration tests'
              //         echo 'TODO archive test results'
              //     }
              // }
            }

        }
        // stage('Run SonarQube analysis') {
        //     steps {
        //         runSonarQubeAnalysis()
        //     }
        // }
    }
    post {
        always {

            // sh label: 'Stopping containers', script: 'docker-compose down -v'
            sh label: 'Remove all unused images not just dangling ones', script:'docker system prune --force'
            sh 'docker image rm -f $(docker images "*/*:*${BUILD_TAG}" -q) $(docker images "*/*/*:*${BUILD_TAG}" -q) || true'
        }
    }
}

void runSonarQubeAnalysis() {
    sh label: 'Running SonarQube analysis', script: "sonar-scanner -Dsonar.host.url=${SONAR_HOST} -Dsonar.login=${SONAR_TOKEN}"
}

String tfEnv(String tfEnvRepo="https://github.com/tfutils/tfenv.git", String tfEnvPath="~/.tfenv") {
  sh(label: "Get tfenv" ,  script: "git clone ${tfEnvRepo} ${tfEnvPath}", returnStatus: true)
  sh(label: "Install TF",  script: "${tfEnvPath}/bin/tfenv install"     , returnStatus: true)
  return "${tfEnvPath}/bin/terraform"
}

int terraformInit(String tfStateBucket, String project, String environment, String component, String region) {
  String terraformBinPath = tfEnv()
  println("Terraform Init for Environment: ${environment} Component: ${component} in region: ${region} using bucket: ${tfStateBucket}")
  String command = "${terraformBinPath} init -backend-config='bucket=${tfStateBucket}' -backend-config='region=${region}' -backend-config='key=${project}-${environment}-${component}.tfstate' -input=false -no-color"
  dir("components/${component}") {
    return( sh( label: "Terraform Init", script: command, returnStatus: true))
  } // dir
} // int TerraformInit

int terraform(String action, String tfStateBucket, String project, String environment, String component, String region, Map<String, String> variables=[:], List<String> parameters=[]) {
    println("Running Terraform ${action} in region ${region} with: \n Project: ${project} \n Environment: ${environment} \n Component: ${component}")
    variablesMap = variables
    variablesMap.put('region',region)
    variablesMap.put('project', project)
    variablesMap.put('environment', environment)
    variablesMap.put('tf_state_bucket',tfStateBucket)
    parametersList = parameters
    parametersList.add("-no-color")
    //parametersList.add("-compact-warnings")  /TODO update terraform to have this working

    // Get the secret variables for global
    String secretsFile = "etc/secrets.tfvars"
    writeVariablesToFile(secretsFile,getAllSecretsForEnvironment(environment,"nia",region))
    String terraformBinPath = tfEnv()
    List<String> variableFilesList = [
      "-var-file=../../etc/global.tfvars",
      "-var-file=../../etc/${region}_${environment}.tfvars",
      "-var-file=../../${secretsFile}"
    ]
    if (action == "apply"|| action == "destroy") {parametersList.add("-auto-approve")}
    List<String> variablesList=variablesMap.collect { key, value -> "-var ${key}=${value}" }
    String command = "${terraformBinPath} ${action} ${variableFilesList.join(" ")} ${parametersList.join(" ")} ${variablesList.join(" ")} "
    dir("components/${component}") {
      return sh(label:"Terraform: "+action, script: command, returnStatus: true)
    } // dir
} // int Terraform

int ecrLogin(String aws_region) {
    String ecrCommand = "aws ecr get-login --region ${aws_region}"
    String dockerLogin = sh (label: "Getting Docker login from ECR", script: ecrCommand, returnStdout: true).replace("-e none","") // some parameters that AWS provides and docker does not recognize
    return sh(label: "Logging in with Docker", script: dockerLogin, returnStatus: true)
}

int checkLbTargetGroupHealth(String lbTargetGroupName, String region, int retries=30, int wait=15) {
  String getLBsCommand = "aws elbv2 describe-target-health --region ${region} --target-group-arn ${lbTargetGroupName} --query TargetHealthDescriptions[].[Target.Id,TargetHealth.State] --output text"
  List<String> lbStatusList = []
  int retriesLeft = retries
  int lbStatus = 1
  while (retriesLeft>0 && lbStatus == 1) {
    lbStatusList = sh (script: getLBsCommand, returnStdout: true).split('/n')
    int allHosts = lbStatusList.size()
    int healthyHosts= lbStatusList.count {it.contains('healthy')}
    println lbStatusList
    if (allHosts == healthyHosts) {
      println("All hosts are healthy")
      lbStatus = 0
    } else {
      retriesLeft = retriesLeft -1
      sleep(wait)
    }
  }
  println "Finished checking"
  return lbStatus
}

Map<String,String> collectTfOutputs(String component) {
  Map<String,String> returnMap = [:]
  dir("components/${component}") {
    String terraformBinPath = tfEnv()
    List<String> outputsList = sh (label: "Listing TF outputs", script: "${terraformBinPath} output", returnStdout: true).split("\n")
    outputsList.each {
      returnMap.put(it.split("=")[0].trim(),it.split("=")[1].trim())
    }
  } // dir
  return returnMap
}

// Retrieving Secrets from AWS Secrets
String getSecretValue(String secretName, String region) {
  String awsCommand = "aws secretsmanager get-secret-value --region ${region} --secret-id ${secretName} --query SecretString --output text"
  return sh(script: awsCommand, returnStdout: true).trim()
}

void writeSecretToFile(String secretName, String fileName, String region) {
  String secretValue = getSecretValue(secretName, region)
  sh (script: "touch ${fileName} && echo '\n' > ${fileName}")
  sh (script: "echo '${secretValue}' >> ${fileName}")
}

Map<String,Object> decodeSecretKeyValue(String rawSecret) {
  List<String> secretsSplitted = rawSecret.replace("{","").replace("}","").split(",")
  Map<String,Object> secretsDecoded = [:]
  secretsSplitted.each {
    String key = it.split(":")[0].trim().replace("\"","")
    Object value = it.split(":")[1]
    secretsDecoded.put(key,value)
  }
  return secretsDecoded
}

List<String> getSecretsByPrefix(String prefix, String region) {
  String awsCommand = "aws secretsmanager list-secrets --region ${region} --query SecretList[].Name --output text"
  List<String> awsReturnValue = sh(script: awsCommand, returnStdout: true).split()
  return awsReturnValue.findAll { it.startsWith(prefix) }
}

Map<String,Object> getAllSecretsForEnvironment(String environment, String secretsPrefix, String region) {
  List<String> globalSecrets = getSecretsByPrefix("${secretsPrefix}-global",region)
  println "global secrets:" + globalSecrets
  List<String> environmentSecrets = getSecretsByPrefix("${secretsPrefix}-${environment}",region)
  println "env secrets:" + environmentSecrets
  Map<String,Object> secretsMerged = [:]
  globalSecrets.each {
    String rawSecret = getSecretValue(it,region)
    if (it.contains("-kvp")) {
      secretsMerged << decodeSecretKeyValue(rawSecret)
    } else {
      secretsMerged.put(it.replace("${secretsPrefix}-global-",""),rawSecret)
    }
  }
  environmentSecrets.each {
    String rawSecret = getSecretValue(it,region)
    if (it.contains("-kvp")) {
      secretsMerged << decodeSecretKeyValue(rawSecret)
    } else {
      secretsMerged.put(it.replace("${secretsPrefix}-${environment}-",""),rawSecret)
    }
  }
  return secretsMerged
}

void writeVariablesToFile(String fileName, Map<String,Object> variablesMap) {
  List<String> variablesList=variablesMap.collect { key, value -> "${key} = ${value}" }
  sh (script: "touch ${fileName} && echo '\n' > ${fileName}")
  variablesList.each {
    sh (script: "echo '${it}' >> ${fileName}")
  }
}

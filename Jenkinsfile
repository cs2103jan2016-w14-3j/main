// A Declarative Pipeline is defined within a 'pipeline' block.
pipeline {
    // agent defines where the pipeline will run.
    agent any
    // triggers after gitlab push or mergerequest
    // triggers {
    //     gitlab(triggerOnPush: true, triggerOnMergeRequest: true, branchFilterType: 'All')
    // }

     // The tools directive allows you to automatically install tools configured in jenkins
    tools {
        ant 'ant'
        jdk 'java jdk'
    }
    stages {
        // stage('Checkout'){
        //     steps{
        //         checkout scm
        //     }       
        // }
        stage('Build') {
            // Every stage must have a steps block containing at least one step.
            steps {
                // step([$class: 'WsCleanup'])
                notifyBuild('STARTED')
                echo 'Building..'

                echo pwd()
                // Clean any locally modified files and ensure we are actually on origin/master
                // as a failed release could leave the local workspace ahead of origin/master
                sh "git clean -f && git reset --hard origin/master"
                // we want to pick up the version from the pom
                sh "ls -la ${pwd()}"
                // sh '''
                //     echo "PATH = ${PATH}"
                //     echo "M2_HOME = ${M2_HOME}"
                // '''
                dir("${pwd()}/build") {
                    sh 'ant'
        //             sh 'ls -la'
        //             sh 'scp -v -o StrictHostKeyChecking=no addressbook.war builder@172.104.43.189:/opt/apache-tomcat-8.5.15/webapps'
        //             // sh 'cp addressbook.war /opt/apache-tomcat-8.5.15/webapps/'
                 }        
                
                 // sh 'mvn  -Dmaven.test.failure.ignore=true clean package -DskipTests cobertura:cobertura checkstyle:checkstyle findbugs:findbugs pmd:pmd pmd:cpd'

            }

        }

        // stage('Test') {
        //     steps {
        //          echo 'Testing..'  
        //          script{  
        //             try {
        //                 // test stuff
        //                 //We define that if those fail, we don’t want to break the build, we will just make it unstable. 
        //                 // We also archive our test results by capturing them from the generated results xml file.
                        
        //                 // mvn test with jacoco coverage report - inferior UI
        //                 // sh 'mvn test-compile org.jacoco:jacoco-maven-plugin:prepare-agent surefire:test -B -e'

        //                 //mvn test with cobertura coverage report
        //                 sh 'mvn test-compile cobertura:cobertura -Dcobertura.report.format=xml surefire:test -B -e'
           
        //             } catch(err) {

        //                 currentBuild.result = "UNSTABLE"
        //                 dir("${pwd()}/target/surefire-reports") {
        //                     // some block
        //                     sh 'ls -la'
        //                     rocketSend channel: 'jenkins-tests', 
        //                     message: "Test Result of ${env.JOB_NAME} ${env.BUILD_NUMBER} -\
        //                      (<${env.BUILD_URL}execution/node/4/ws/target/surefire-reports/${env.JOB_NAME}.SampleTest.txt |\
        //                     Click here to see which Unit Test(s) Failed>)", 
        //                     rawMessage: true
                            
        //                 }   
        //                 // handle the exception; or ignore it
        //             } finally {
        //                 // step([$class: 'JUnitResultArchiver', testResults: '**/TEST-*.xml'])
        //                 junit '**/target/surefire-reports/*.xml'
            
        //             }
                    
        //         }    

        //     }  
            
        // }


        // // stage('Parallel'){
        // //      steps {
        // //     // Note that parallel can only be used as the only step for a stage.
        // //     // Also, if you want to have your parallel branches run on different
        // //     // nodes, you'll need control that manually with "node('some-label') {"
        // //     // blocks inside the parallel branches, and per-stage post won't be able
        // //     // to see anything from the parallel workspaces.
        // //         parallel(
        // //             one: {
        // //                 echo "I'm on the first branch!"
        // //                 // sh 'mvn -Dmaven.test.failure.ignore=true test'
        // //             },
        // //             two: {
        // //                 echo "I'm on the second branch!"
        // //                 // sh 'mvn -Dmaven.test.failure.ignore=true test'
        // //             },
        // //             three: {
        // //                 echo "I'm on the third branch!"
        // //                 echo "But you probably guessed that already."
        // //                 // sh 'mvn -Dmaven.test.failure.ignore=true test'
        // //             }
        // //         )
        // //     }
        // // }

        // stage('Documentation') {
        //     steps{
        //         sh 'mvn javadoc:javadoc -B -e'
        //         step([$class: 'JavadocArchiver', javadocDir: 'target/site/apidocs', keepAll: false])
        //     }
            
        // }
      
        // stage('Code Analysis'){
        //     steps {
        //         echo 'Analysing..'
        //         sh 'mvn checkstyle:checkstyle pmd:pmd pmd:cpd findbugs:findbugs -B -e'
        //         step([$class: 'CheckStylePublisher', pattern: 'target/checkstyle-result.xml'])
        //         step([$class: 'FindBugsPublisher', pattern: 'target/findbugsXml.xml'])
        //         step([$class: 'PmdPublisher', pattern: 'target/pmd.xml'])
        //         step([$class: 'DryPublisher', pattern: 'target/cpd.xml'])
        //         // publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/site', reportFiles: 'checkstyle.html', reportName: 'Checkstyle Report', reportTitles: ''])
        //         // publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/site', reportFiles: 'pmd.html', reportName: 'Pmd Report', reportTitles: ''])
        //         // publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/site', reportFiles: 'cpd.html', reportName: 'Cpd Report', reportTitles: ''])
                
        //     }
        // }

        // stage('Code Coverage') {
        //     steps{
        //         //Publish Jacoco coverage report
        //         // step([$class: 'JacocoPublisher', execPattern: 'target/jacoco.exec'])
        //         //Publish Cobertura coverage report
        //         step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: '**/target/site/cobertura/coverage.xml', failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false])
        //     } 
        // }


        // // stage('SonarQube analysis') {
        // //     steps{
        // //         script{
        // //             def scannerHome = tool 'sonarqube';
        // //             withSonarQubeEnv('sonarqube') {
        // //                 sh "${scannerHome}/bin/sonar-scanner -e -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.projectName=webaddressbook -Dsonar.projectVersion=1.0 -Dsonar.projectKey=webaddressbook -Dsonar.sources=src"
        // //             }
        // //         }    
        // //     }
        // //     post{
        // //         success{
        // //             rocketSend channel: 'jenkins-tests', 
        // //                 message: "Test Result of ${env.JOB_NAME} ${env.BUILD_NUMBER} -(<http://localhost:9000/dashboard/index/webaddressbook|Click here to see detailed report>)"   
        // //         }
        // //     }
        // // }


        // stage('Deploy') {
        //     steps {
        //         echo 'Deploying...'
                
        //          dir("${pwd()}/target") {
        //             sh 'ls -la'
        //             sh 'scp -v -o StrictHostKeyChecking=no addressbook.war builder@172.104.43.189:/opt/apache-tomcat-8.5.15/webapps'
        //             // sh 'cp addressbook.war /opt/apache-tomcat-8.5.15/webapps/'
        //          }             
        //     }
        // }


    }
    post {
        always{
            notifyBuild(currentBuild.result)
        }
    }

    // The options directive is for configuration that applies to the whole job.
  options {
    // For example, we'd like to make sure we only keep 10 builds at a time, so
    // we don't fill up our storage!
    buildDiscarder(logRotator(numToKeepStr:'10'))
     
    // And we'd really like to be sure that this build doesn't hang forever, so
    // let's time it out after an hour.
    timeout(time: 60, unit: 'MINUTES')
    gitLabConnection('java webapp')
    gitlabCommitStatus(name: 'Java Webapp')
  }

}

def buildNumber = env.BUILD_NUMBER
def buildStatus = currentBuild.result

def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus = buildStatus ?: 'SUCCESS'

  def details
  def emojiface
  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    emojiface = ':rocket:'
    details = "Build Started! - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
  } else if (buildStatus == 'SUCCESS') {
    emojiface = ':smile:'
    details = "Build Successful! - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>) \n Deploy Successful! - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<http://172.104.43.189:8080/addressbook/|Open Webapp>)"
  } else if (buildStatus == 'UNSTABLE'){
    emojiface = ':sweat_smile:'
    details = "Build Unstable! - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
  } else if (buildStatus == 'FAILURE'){
    emojiface = ':sob:'
    details = "Build Failure! - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
  }

  rocketSend channel: 'jenkins-tests', emoji: emojiface, message: details, rawMessage: true

}




def remove_report_dir() {
    try {
        bat "rmdir /Q /s reports"
        bat "rmdir /Q /s allure-results"
    } catch (ex) {
        echo "file not exit"
    }
}

def checkout() {
    checkout([$class              : 'SubversionSCM',
              filterChangelog     : false,
              ignoreDirPropChanges: false,
              locations           : [[cancelProcessOnExternalsFail: true,
                                      credentialsId               : 'db00ff19-5b9a-4603-a174-ac8a5ae664b8',
                                      depthOption                 : 'infinity',
                                      ignoreExternalsOption       : true,
                                      local                       : '.',
                                      remote                      : "${params.svn}"]],
              quietOperation      : true, workspaceUpdater: [$class: 'UpdateUpdater']])
}

def config_file_copy() {
    configFileProvider([configFile(fileId: 'cli-login-settings', targetLocation: './src/test/resources/cli.properties')]) {}
    configFileProvider([configFile(fileId: 'general', targetLocation: './src/test/resources/general.properties')]) {}
}


def upload_package() {
    bat "mvn clean -Dtest=HealthCheckingTestPage#* -Dcenter=${params.CENTER} test";
    bat "mvn clean -Dtest=SetupSettings#* -Dcenter=${params.CENTER} test";
    bat "mvn test -PIE -Dpackage=management/system/software -Dcenter=${params.CENTER} -Dbranch=${params.BRANCH}"
}


def cli_test(String server) {
    bat "mvn clean -PIE -Dpackage=cli -Dimage=${params.image} -Dcenter=" + server + " -Dgroups=\"! EMODULE\" test"
}

def make_report() {
    try {
        allure includeProperties: false,
                jdk: "OpenJDK-15",
                results: [[path: 'allure-results'], [path: 'target/allure-results']]
    } catch (Exception ex) {
        echo ex.message
    }
}

def test_agent_install() {
    bat "mvn clean -Dtest=GsAgentInstall#* -Dcenter=${params.CENTER} test"
}

def test_agent_functions() {
    try {
        bat "mvn clean -Dtest=CheckEventUpload#* -Dcenter=${params.CENTER} test";
    } catch (Exception ex) {
        echo ex.message
    }
}

def test_agent_delete(String tag) {
    try {
        def date = new Date()
        bat "xcopy \"C:\\Program Files\\Geni\\Insights\\logs\" \"logs\" /ICY"
        zip archive: true, dir: 'logs', glob: '', zipFile: 'agent_log_' + date.format("yyyyMMdd_HHmmss") + '_' + tag + '.zip'
    } catch (Exception ex) {
        echo ex.message
    }

    bat "mvn clean -Dtest=GsAgentDelete#* -Dcenter=${params.CENTER} test"
}

def checkout_stage() {

    stage('Checkout') {
        checkout()
        config_file_copy()
        remove_report_dir()
    }
}

def do_test(tag) {
    env.JAVA_HOME = "${tool 'OpenJDK-15'}"
    env.M3 = "${tool 'MAVEN_3.8.1'}"
    withEnv(["PATH+MAVEN=${tool 'MAVEN_3.8.1'}/bin", "PATH+POWERSHELL=C:\\WINDOWS\\System32\\WindowsPowerShell\\v1.0"]) {
        stage('Install') {
            test_agent_install()
        }
        stage('Agent Action') {
            test_agent_functions()
        }
        stage('Agent Delete') {
            test_agent_delete(tag)
        }
        stage('Report') {
            make_report()
        }
    }
}

return this

package org.test
import hudson.scm.SubversionSCM

class Checkout implements Serializable {

    def script

    Checkout() {

    }

    def run(String scmUrl) {
        checkout([$class              : 'SubversionSCM',
                  filterChangelog     : false,
                  ignoreDirPropChanges: false,
                  locations           : [[cancelProcessOnExternalsFail: true,
                                          credentialsId               : 'db00ff19-5b9a-4603-a174-ac8a5ae664b8',
                                          depthOption                 : 'infinity',
                                          ignoreExternalsOption       : true,
                                          local                       : '.',
                                          remote                      : [[url: scmUrl]],
                  quietOperation      : true, workspaceUpdater: [$class: 'UpdateUpdater']])
    }

}

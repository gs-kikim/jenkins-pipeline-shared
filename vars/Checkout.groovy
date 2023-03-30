package org.test

class Checkout implements Serializable {

    def script

    Checkout() {

    }

    def checkout(String svn) {
        checkout([$class              : 'SubversionSCM',
                  filterChangelog     : false,
                  ignoreDirPropChanges: false,
                  locations           : [[cancelProcessOnExternalsFail: true,
                                          credentialsId               : 'db00ff19-5b9a-4603-a174-ac8a5ae664b8',
                                          depthOption                 : 'infinity',
                                          ignoreExternalsOption       : true,
                                          local                       : '.',
                                          remote                      : svn]],
                  quietOperation      : true, workspaceUpdater: [$class: 'UpdateUpdater']])
    }

}

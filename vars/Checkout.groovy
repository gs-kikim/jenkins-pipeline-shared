package org.test


import hudson.scm.SubversionSCM
import hudson.scm.SubversionSCM.ModuleLocation


class Checkout implements Serializable {

    def script

    Checkout() {

    }

    def call(String scmUrl, String branch, String checkoutDir) {
        checkout(new SubversionSCM([
                new ModuleLocation(scmUrl, branch, null, null, true)
        ]), checkoutDir)
    }

}

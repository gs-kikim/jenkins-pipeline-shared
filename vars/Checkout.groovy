package org.test


import hudson.scm.SubversionSCM
import hudson.scm.SubversionSCM.ModuleLocation


def call(String scmUrl, String branch, String checkoutDir) {
    checkout(new SubversionSCM([
            new ModuleLocation(scmUrl, branch, null, null, true)
    ]), checkoutDir)
}


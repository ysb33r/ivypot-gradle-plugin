package org.ysb33r.gradle.ivypot.internal

import groovy.transform.TupleConstructor
import org.gradle.api.artifacts.repositories.PasswordCredentials

/**
 * @author Schalk W. Cronjé
 */
@TupleConstructor
class Credentials implements PasswordCredentials {
    String username
    String password

    void username(final String s) {this.username=s}
    void password(final String s) {this.password=s}
}

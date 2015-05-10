package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileStatic

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class JCenter extends MavenRepository {

    @Override
    String resolverXml() {
        """<ibiblio name="${name}" root="https://jcenter.bintray.com/" m2compatible="true"/>"""
    }

}

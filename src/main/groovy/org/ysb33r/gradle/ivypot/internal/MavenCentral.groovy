package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileStatic
import org.gradle.api.GradleException

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MavenCentral extends MavenRepository {

    @Override
    String resolverXml() {
        """<ibiblio name="${name}" m2compatible="true"/>"""
    }

}

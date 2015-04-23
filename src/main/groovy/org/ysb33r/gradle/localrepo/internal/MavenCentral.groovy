package org.ysb33r.gradle.localrepo.internal

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.GradleScriptException

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MavenCentral extends MavenRepository {

    MavenCentral(Map properties=[:]) {
        super(properties)
        super.name='maven2'
    }

    @Override
    void setName(String name) {not_allowed()}

    @Override
    String getName() {
        'maven2'
    }

    @Override
    String resolverXml() {
        '<ibiblio name="maven2" m2compatible="true"/>'
    }

    private void not_allowed() {
        throw new GradleException('Not an allowed configuration for mavenCentral()')
    }
}

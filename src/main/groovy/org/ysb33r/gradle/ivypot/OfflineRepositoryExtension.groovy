package org.ysb33r.gradle.ivypot

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

@CompileStatic
class OfflineRepositoryExtension {
    String groovyVersion = GroovySystem.version
    String ivyVersion = '2.4.0'

    OfflineRepositoryExtension(Project project) {
        this.project = project
    }

    Configuration getConfiguration() {
        def deps = [
            createDependency("${GROOVY_GROUP}:groovy-ant:${groovyVersion}"),
            createDependency("${IVY_GROUP}:ivy:${ivyVersion}")
        ]

        project.configurations.detachedConfiguration(
            deps.toArray() as Dependency[]
        )
    }

    private Dependency createDependency(final String notation, final Closure configurator = null) {
        if (configurator) {
            project.dependencies.create(notation, configurator)
        } else {
            project.dependencies.create(notation)
        }
    }

    private static final String GROOVY_GROUP = 'org.codehaus.groovy'
    private static final String IVY_GROUP = 'org.apache.ivy'
    private Project project
}

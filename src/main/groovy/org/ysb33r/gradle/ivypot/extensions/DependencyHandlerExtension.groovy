package org.ysb33r.gradle.ivypot.extensions

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.ysb33r.gradle.ivypot.internal.DefaultBinaryArtifactDependency
import org.ysb33r.gradle.ivypot.repositories.binary.NamedBinaryArtifactDependency

@CompileStatic
class DependencyHandlerExtension implements NamedDomainObjectContainer<NamedBinaryArtifactDependency> {

    DependencyHandlerExtension(Project project) {
        Closure instantiator = { Project p, String name ->
            DefaultBinaryArtifactDependency.create(p, name)
        }.curry(project)

        delegate = project.container(NamedBinaryArtifactDependency, instantiator)
        delegate.addRule('new dependency', instantiator)
    }

    void add(String notation) {
        this.delegate.create(notation)
    }

    @Delegate
    private final NamedDomainObjectContainer<NamedBinaryArtifactDependency> delegate
}

package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyArtifact
import org.gradle.api.artifacts.ModuleDependency
import org.ysb33r.gradle.ivypot.repositories.binary.NamedBinaryArtifactDependency

@CompileStatic
class DefaultBinaryArtifactDependency implements NamedBinaryArtifactDependency {

    private final DependencyArtifact artifactExtras
    final String group
    final String revision
    final String module

    String getClassifier() {
        this.artifactExtras?.classifier
    }

    String getExtension() {
        this.artifactExtras?.extension
    }

    String getType() {
        this.artifactExtras?.type
    }

    @Override
    String getName() {
        "${group ?: ''}:${module}:${revision ?: ''}${classifier ? ':' + classifier : ''}" +
                "${extension ? ':' + extension : ''}${type ? '/type=' + type : ''}"
    }

    @Override
    String toString() {
        name
    }

    private DefaultBinaryArtifactDependency(String organisation, String module, String version, DependencyArtifact dep) {
        this.artifactExtras = dep
        this.group = organisation
        this.revision = version
        this.module = module
    }

    static DefaultBinaryArtifactDependency create(Project project, String s) {
        ModuleDependency dep = (ModuleDependency) project.dependencies.create(s)
        new DefaultBinaryArtifactDependency(dep.group, dep.name, dep.version, dep.artifacts[0])
    }
}

package org.ysb33r.gradle.ivypot

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.ysb33r.gradle.ivypot.extensions.DependencyHandlerExtension

@CompileStatic
class BinaryPotBasePlugin implements Plugin<Project> {

    public final static String EXTENSION_NAME = 'cachedBinaries'
    @Override
    void apply(Project project) {
        ((ExtensionAware)project.dependencies).extensions.create(EXTENSION_NAME, DependencyHandlerExtension, project)
    }
}

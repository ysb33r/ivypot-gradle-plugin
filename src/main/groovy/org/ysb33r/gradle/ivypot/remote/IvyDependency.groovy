package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic

@CompileStatic
class IvyDependency implements Serializable {
    String organisation
    String module
    String revision
    boolean transitive
    String typeFilter
    String confFilter
}

package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic

@CompileStatic
class BinaryDependency implements Serializable {
    String organisation
    String module
    String revision
    String extension
    String classifier
    String typeFilter
}

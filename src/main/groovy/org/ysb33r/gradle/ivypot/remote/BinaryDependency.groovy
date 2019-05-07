package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic

@CompileStatic
class BinaryDependency extends IvyDependency {
    String extension
    String classifier
}

package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic

@CompileStatic
class BinaryRepositoryDescriptor implements Serializable {
    URI rootUri
    String artifactPattern
}

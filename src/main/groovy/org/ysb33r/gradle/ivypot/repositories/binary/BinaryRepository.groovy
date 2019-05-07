package org.ysb33r.gradle.ivypot.repositories.binary

import groovy.transform.CompileStatic
import org.gradle.api.Named

@CompileStatic
interface BinaryRepository extends Named {
    URI getRootUri()
    void setRootUri(String uri)
    void setRootUri(URI uri)

    String getArtifactPattern()
    void setArtifactPattern(String pattern)
}

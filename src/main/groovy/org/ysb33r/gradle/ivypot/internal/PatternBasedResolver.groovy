package org.ysb33r.gradle.ivypot.internal

import org.apache.commons.lang.NotImplementedException
import org.gradle.api.internal.artifacts.repositories.resolver.IvyResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.M2ResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.ResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.PatternBasedResolver as GradlePatternBasedResolver

/**
 * @author Schalk W. Cronjé
 */
class PatternBasedResolver implements GradlePatternBasedResolver {

    boolean m2compatible = false
    List<ResourcePattern> artifactPatterns = []
    List<ResourcePattern> ivyPatterns = []

    @Override
    void addArtifactLocation(URI baseUri, String pattern) {
        ResourcePattern rp
        if(m2compatible) {
            rp = new M2ResourcePattern(baseUri, pattern)
        } else {
            rp = new IvyResourcePattern(baseUri, pattern)
        }
        artifactPatterns+= rp
    }

    @Override
    void addDescriptorLocation(URI baseUri, String pattern) {
        ResourcePattern rp
        if(m2compatible) {
            rp = new M2ResourcePattern(baseUri, pattern)
        } else {
            rp = new IvyResourcePattern(baseUri, pattern)
        }
        ivyPatterns+= rp
    }

}


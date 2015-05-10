package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.util.CollectionUtils
import org.ysb33r.gradle.ivypot.IvyXml

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MavenRepository implements MavenArtifactRepository, IvyXml, RepositoryTraits {

    /**
     * Returns the additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * @return The additional URLs. Returns an empty list if there are no such URLs.
     */
    @Override
    @CompileDynamic
    Set<URI> getArtifactUrls() {
        CollectionUtils.stringize(this.artifactUrls).collect{ String it -> it.toURI() } as Set<URI>
    }

    /**
     * Adds some additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * <p>The provided values are evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated
     * relative to the project directory.
     *
     * @param urls The URLs to add.
     */
    @Override
    void artifactUrls(Object... urls) {
        artifactUrls.addAll(urls as List)
    }

    /**
     * Sets the additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * <p>The provided values are evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated
     * relative to the project directory.
     *
     * @param urls The URLs.
     */
    @Override
    void setArtifactUrls(Iterable<?> urls) {
        artifactUrls.clear()
        artifactUrls.addAll(urls)
    }

    /** Returns a XML snippet suitable for including in the resolvers section
     *
     * @return
     */
    @Override
    String resolverXml() {
        // TODO: Test == [organisation]/[module]/[revision]/[artifact]-[revision].[ext]
        String ret = "<url name='${name}' m2compatible='true'>"
        getArtifactUrls().each { URI u ->
            ret+= "<artifact pattern='${u}/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>"
        }
        ret + '</url>'
    }

    private List<Object> artifactUrls = []
}

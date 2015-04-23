package org.ysb33r.gradle.localrepo.internal

import groovy.transform.CompileStatic
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.util.CollectionUtils
import org.ysb33r.gradle.localrepo.IvyXml

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class MavenRepository implements MavenArtifactRepository, IvyXml {

    MavenRepository(Map properties) {

    }
    /**
     * The base URL of this repository. This URL is used to find both POMs and artifact files. You can add additional URLs to use to look for artifact files, such as jars, using {@link
     * # setArtifactUrls ( Iterable )}.
     *
     * @return The URL.
     */
    @Override
    URI getUrl() {
        this.url.toString().toURI()
    }

    /**
     * Sets the base URL of this repository. This URL is used to find both POMs and artifact files. You can add additional URLs to use to look for artifact files, such as jars, using {@link
     * # setArtifactUrls ( Iterable )}.
     *
     * <p>The provided value is evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated relative
     * to the project directory.
     *
     * @param url The base URL.
     */
    @Override
    void setUrl(Object url) {
        this.url = url
    }

    /**
     * Returns the additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * @return The additional URLs. Returns an empty list if there are no such URLs.
     */
    @Override
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

    /**
     * Returns the name for this repository. A name must be unique amongst a repository set. A default name is provided for the repository if none
     * is provided.
     *
     * <p>The name is used in logging output and error reporting to point to information related to this repository.
     *
     * @return The name.
     */
    @Override
    String getName() {
        this.name
    }

    /**
     * Sets the name for this repository.
     *
     * If this repository is to be added to an {@link org.gradle.api.artifacts.ArtifactRepositoryContainer}
     * (including {@link org.gradle.api.artifacts.dsl.RepositoryHandler}), its name should not be changed after it has
     * been added to the container. This capability has been deprecated and is scheduled to be removed in the next major
     * Gradle version.
     *
     * @param name The name. Must not be null.
     */
    @Override
    void setName(String name) {
        this.name = name
    }

    /**
     * Provides the Credentials used to authenticate to this repository.
     * @return The credentials
     */
    @Override
    PasswordCredentials getCredentials() {
        return null
    }

    /**
     * Configure the Credentials for this repository using the supplied Closure.
     *
     * <pre autoTested=''>
     * repositories {*     maven {*         credentials {*             username = 'joe'
     *             password = 'secret'
     *}*}*}* </pre>
     */
    @Override
    void credentials(Closure closure) {

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

    private Object url
    private String name
    private List<Object> artifactUrls = []
}

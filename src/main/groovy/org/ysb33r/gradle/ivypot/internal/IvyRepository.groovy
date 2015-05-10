package org.ysb33r.gradle.ivypot.internal

import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.IvyArtifactRepositoryMetaDataProvider
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.ysb33r.gradle.ivypot.IvyXml

/**
 * @author Schalk W. Cronjé
 */
class IvyRepository implements IvyArtifactRepository, IvyXml, RepositoryTraits {
    /**
     * Adds an independent pattern that will be used to locate artifact files in this repository. This pattern will be used to locate ivy files as well, unless a specific
     * ivy pattern is supplied via {@link #ivyPattern(String)}.
     *
     * If this pattern is not a fully-qualified URL, it will be interpreted as a file relative to the project directory.
     * It is not interpreted relative the the URL specified in {@link #setUrl(Object)}.
     *
     * Patterns added in this way will be in addition to any layout-based patterns added via {@link #setUrl}.
     *
     * @param pattern The artifact pattern.
     */
    @Override
    void artifactPattern(String pattern) {

    }

    /**
     * Adds an independent pattern that will be used to locate ivy files in this repository.
     *
     * If this pattern is not a fully-qualified URL, it will be interpreted as a file relative to the project directory.
     * It is not interpreted relative the the URL specified in {@link #setUrl(Object)}.
     *
     * Patterns added in this way will be in addition to any layout-based patterns added via {@link #setUrl}.
     *
     * @param pattern The ivy pattern.
     */
    @Override
    void ivyPattern(String pattern) {

    }

    /**
     * Specifies the layout to use with this repository, based on the root url.
     * See {@link #layout(String, Closure)}.
     *
     * @param layoutName The name of the layout to use.
     */
    @Override
    void layout(String layoutName) {

    }

    /**
     * Specifies how the items of the repository are organized.
     * <p>
     * The layout is configured with the supplied closure.
     * <p>
     * Recognised values are as follows:
     * </p>
     * <h4>'gradle'</h4>
     * <p>
     * A Repository Layout that applies the following patterns:
     * <ul>
     *     <li>Artifacts: <code>$baseUri/{@value #GRADLE_ARTIFACT_PATTERN}</code></li>
     *     <li>Ivy: <code>$baseUri/{@value #GRADLE_IVY_PATTERN}</code></li>
     * </ul>
     * </p>
     * <h4>'maven'</h4>
     * <p>
     * A Repository Layout that applies the following patterns:
     * <ul>
     *     <li>Artifacts: <code>$baseUri/{@value #MAVEN_ARTIFACT_PATTERN}</code></li>
     *     <li>Ivy: <code>$baseUri/{@value #MAVEN_IVY_PATTERN}</code></li>
     * </ul>
     * </p>
     * <p>
     * Following the Maven convention, the 'organisation' value is further processed by replacing '.' with '/'.
     * </p>
     * <h4>'ivy'</h4>
     * <p>
     * A Repository Layout that applies the following patterns:
     * <ul>
     *     <li>Artifacts: <code>$baseUri/{@value #IVY_ARTIFACT_PATTERN}</code></li>
     *     <li>Ivy: <code>$baseUri/{@value #IVY_ARTIFACT_PATTERN}</code></li>
     * </ul>
     * </p>
     * <p><b>Note:</b> this pattern is currently {@link org.gradle.api.Incubating incubating}.</p>
     * <h4>'pattern'</h4>
     * <p>
     * A repository layout that allows custom patterns to be defined. eg:
     * <pre autoTested="">
     * repositories {*     ivy {*         layout 'pattern' , {*             artifact '[module]/[revision]/[artifact](.[ext])'
     *             ivy '[module]/[revision]/ivy.xml'
     *}*}*}* </pre>
     * </p>
     *
     * @param layoutName The name of the layout to use.
     * @param config The closure used to configure the layout.
     */
    @Override
    void layout(String layoutName, Closure config) {

    }

    /**
     * Returns the meta-data provider used when resolving artifacts from this repository. The provider is responsible for locating and interpreting the meta-data
     * for the modules and artifacts contained in this repository. Using this provider, you can fine tune how this resolution happens.
     *
     * @return The meta-data provider for this repository.
     */
    @Override
    IvyArtifactRepositoryMetaDataProvider getResolve() {
        return null
    }

    /** Returns a XML snippet suitable for including in the resolvers section
     *
     * @return
     */
    @Override
    String resolverXml() {
        return null
    }
}

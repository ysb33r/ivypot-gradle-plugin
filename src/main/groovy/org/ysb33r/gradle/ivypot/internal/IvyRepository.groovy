//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2015
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================
//

package org.ysb33r.gradle.ivypot.internal

import groovy.transform.TypeChecked
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.IvyArtifactRepositoryMetaDataProvider
import org.ysb33r.gradle.ivypot.IvyXml

/**
 * @author Schalk W. Cronjé
 */
class IvyRepository implements IvyArtifactRepository, IvyXml, RepositoryTraits {

    String artifactPattern
    String ivyPattern
    String layoutName

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
        this.artifactPattern += pattern
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
        this.ivyPattern = pattern
    }

    /**
     * Specifies the layout to use with this repository, based on the root url.
     * See {@link #layout(String, Closure)}.
     *
     * @param layoutName The name of the layout to use.
     */
    @Override
    @TypeChecked
    void layout(final String layoutName) {
        final String namespace = 'org.gradle.api.internal.artifacts.repositories.layout.'
        String repositoryLayoutName
        Class layoutClass
        switch (layoutName) {
            case 'maven':
            case 'ivy':
            case 'gradle':
            case 'pattern':
                repositoryLayoutName = layoutName.capitalize()
                break
            default:
                throw new UnsupportedOperationException("'${layoutName}' is not a valid layout")
        }

        try {
            layoutClass =  Class.forName "${namespace}${repositoryLayoutName}RepositoryLayout"
        } catch(ClassNotFoundException e) {
            // Change in class name prefix in Gradle 2.3 from 'Pattern' to 'DefaultIvyPattern'.
            if(layoutName == 'pattern') {
                layoutClass = Class.forName "${namespace}DefaultIvy${repositoryLayoutName}RepositoryLayout"
            } else {
                throw e
            }
        }

        repositoryLayout =  layoutClass.newInstance()
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
        layout(layoutName)
        def cfg = config.clone()
        cfg.delegate = repositoryLayout
        cfg()
    }

    /**
     * Returns the meta-data provider used when resolving artifacts from this repository. The provider is responsible for locating and interpreting the meta-data
     * for the modules and artifacts contained in this repository. Using this provider, you can fine tune how this resolution happens.
     *
     * @return Null. This is not supported at present.
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

        if (repositoryLayout == null) {
            throw new UnsupportedOperationException('layout has not seen set for Ivy repository')
        }

        def patterns = new PatternBasedResolver()
        repositoryLayout.apply(url, patterns)

        String ret = "<url name='${name}' m2compatible='${patterns.m2compatible ? 'true' : 'false'}'>"
        patterns.ivyPatterns.each {
            ret += "<ivy pattern='${it.pattern}'/>"
        }
        patterns.artifactPatterns.each {
            ret += "<artifact pattern='${it.pattern}'/>"
        }
        ret += '</url>'
    }

    private def repositoryLayout
}

//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2019
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

package org.ysb33r.gradle.ivypot.repositories

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.ysb33r.grolifant.api.StringUtils

/**
 * @since 1.0
 */
@CompileStatic
class MavenArtifactRepository implements Repository, RepositoryTraits {

    /**
     * Returns the additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * @return The additional URLs. Returns an empty list if there are no such URLs.
     */
    Set<URI> getArtifactUrls() {
        StringUtils.stringize(this.artifactUrls).collect { String it -> it.toURI() } as Set<URI>
    }

    /**
     * Adds some additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * <p>The provided values are evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated
     * relative to the project directory.
     *
     * @param urls The URLs to add.
     */
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
    void setArtifactUrls(Set<URI> urls) {
        artifactUrls.clear()
        artifactUrls.addAll(urls)
    }

    /**
     * Sets the additional URLs to use to find artifact files. Note that these URLs are not used to find POM files.
     *
     * <p>The provided values are evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example,
     * that you can pass in a {@code File} object, or a relative path to be evaluated
     * relative to the project directory.
     *
     * @param urls The URLs.
     */
    void setArtifactUrls(Iterable<?> urls) {
        artifactUrls.clear()
        artifactUrls.addAll(urls)
    }

    @Override
    @CompileDynamic
    void writeTo(MarkupBuilder builder) {
        if (artifactUrls.size()) {
            builder.chain(name: name) {
                ibiblio(name: "${name}_root}", m2compatible: true, root: url, usepoms: true)
                getArtifactUrls().eachWithIndex { URI u, int index ->
                    ibiblio(name: "${name}_${index}", m2compatible: true, root: u, usepoms: false)
                }
            }
        } else {
            builder.ibiblio(name: name, m2compatible: true, root: url)
        }
    }

    private List<Object> artifactUrls = []
}

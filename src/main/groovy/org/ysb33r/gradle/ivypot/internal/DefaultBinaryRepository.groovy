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

package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileStatic
import org.ysb33r.gradle.ivypot.repositories.binary.BinaryRepository

@CompileStatic
class DefaultBinaryRepository implements BinaryRepository {

    final String name
    String artifactPattern
    private URI rootUri

    static BinaryRepository create(final String name, String baseURI, final String artifactPattern) {
        new DefaultBinaryRepository(name,baseURI?.toURI(),artifactPattern)
    }

    @Override
    URI getRootUri() {
        this.rootUri
    }

    @Override
    void setRootUri(URI uri) {
        this.rootUri = uri
    }

    @Override
    void setRootUri(String uri) {
        this.rootUri = uri?.toURI()
    }

    private DefaultBinaryRepository(final String name, final URI baseURI, final String artifactPattern) {
        this.name = name
        this.rootUri = baseURI
        this.artifactPattern = artifactPattern
    }
}

//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2018
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

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class JCenter extends MavenArtifactRepository {

    @Override
    @CompileDynamic
    void writeTo(MarkupBuilder builder) {
        builder.ibiblio(name: name, root: 'https://jcenter.bintray.com/', m2compatible: true)
    }
}

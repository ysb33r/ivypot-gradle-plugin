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

package org.ysb33r.gradle.ivypot

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.ivypot.extensions.OfflineRepositoryExtension
import spock.lang.Specification


class OfflineRepositoryExtensionSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void 'Version values are read from property file'() {
        given:
        OfflineRepositoryExtension ext = project.extensions.create('foo', OfflineRepositoryExtension, project)

        expect:
        ext.ivyVersion != '2.3.0'
        ext.groovyVersion != ext.ivyVersion
    }
}
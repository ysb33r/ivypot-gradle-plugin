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

package org.ysb33r.gradle.ivypot

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositoryPluginSpec extends Specification {

    void setupSpec() {
        OfflineRepositorySync.DONT_LOOK_FOR_IVY_JAR = true
    }

    def "Can the plugin be applied"() {
        given:
            def project = ProjectBuilder.builder().build()
            project.apply plugin : 'org.ysb33r.ivypot'

        expect:
            project.tasks.getByName('syncRemoteRepositories') instanceof OfflineRepositorySync
    }
}
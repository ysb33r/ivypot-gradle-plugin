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
import spock.lang.Specification

class BinaryPotBasePluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void setup() {
        project.apply plugin: 'org.ysb33r.ivypot.binary.base'
    }

    void 'Add an external dependency'() {

        when:
        project.allprojects {
            dependencies {
                cachedBinaries.add 'group1:module:version:classifier@ext'
                cachedBinaries.add 'group2:module:version:classifier'
                cachedBinaries.add 'group3:module:version'
            }
        }

        def bins = project.dependencies.cachedBinaries.asMap.values()

        then:
        bins.size() == 3
        bins[0].group == 'group1'
        bins[0].module == 'module'
        bins[0].revision == 'version'
        bins[0].classifier == 'classifier'
        bins[0].extension == 'ext'
        bins[1].group == 'group2'
        bins[1].classifier == 'classifier'
        bins[1].extension == 'jar'
        bins[2].group == 'group3'
        bins[2].classifier == null
        bins[2].extension == null
    }
}

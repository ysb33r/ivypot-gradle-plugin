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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BinaryDependencySpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void 'Create a binary dependency directly'() {
        when:
        DefaultBinaryArtifactDependency dep = DefaultBinaryArtifactDependency.create(project, "gradleDist:gradle:4.5.1:bin@zip")

        then:
        verifyAll {
            dep.group == 'gradleDist'
            dep.module == 'gradle'
            dep.revision == '4.5.1'
            dep.classifier == 'bin'
            dep.type == 'zip'
            dep.extension == 'zip'
        }
    }
}
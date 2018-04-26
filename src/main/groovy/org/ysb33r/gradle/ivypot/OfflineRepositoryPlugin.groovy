//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2017
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

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositoryPlugin implements Plugin<Project> {

    final static String MINIMUM_GRADLE = '4.0'

    void apply(Project project) {

        if(GradleVersion.current() < GradleVersion.version(MINIMUM_GRADLE)) {
            throw new GradleException("Ivypot can only be used with Gradle ${MINIMUM_GRADLE} or later")
        }
        project.tasks.create 'syncRemoteRepositories', OfflineRepositorySync
    }
}

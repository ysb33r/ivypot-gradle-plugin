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

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project

/** Exception that indicates that the operation cannot be performed on the current project.
 *
 * @since 0.6
 */
@CompileStatic
class CannotUseCurrentProjectException extends GradleException {
    CannotUseCurrentProjectException() {
        super('This operation is not valid for the the current project')
    }

    /**
     *
     * @param s Reason
     */
    CannotUseCurrentProjectException(final String s) {
        super(s)
    }

    /**
     * @param s Reason
     * @param p Project that operation was applied to
     */
    CannotUseCurrentProjectException(final String s,final Project p) {
        super("${s}: ${p.name}")
    }
}

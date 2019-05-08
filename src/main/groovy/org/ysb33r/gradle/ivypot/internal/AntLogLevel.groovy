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
import org.gradle.api.logging.LogLevel

import static org.apache.tools.ant.Project.MSG_DEBUG
import static org.apache.tools.ant.Project.MSG_ERR
import static org.apache.tools.ant.Project.MSG_VERBOSE
import static org.apache.tools.ant.Project.MSG_WARN

@CompileStatic
class AntLogLevel {
    static int fromGradleLogLevel(LogLevel gradleLogLevel) {
        switch (gradleLogLevel) {
            case null:
            case gradleLogLevel.LIFECYCLE:
                MSG_WARN
                break
            case gradleLogLevel.DEBUG:
                MSG_DEBUG
                break
            case gradleLogLevel.QUIET:
                MSG_ERR
                break
            case gradleLogLevel.INFO:
                MSG_VERBOSE
                break
        }
    }
}

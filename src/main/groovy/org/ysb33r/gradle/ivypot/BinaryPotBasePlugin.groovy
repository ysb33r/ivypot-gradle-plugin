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

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.ysb33r.gradle.ivypot.extensions.DependencyHandlerExtension

@CompileStatic
class BinaryPotBasePlugin implements Plugin<Project> {

    public final static String EXTENSION_NAME = 'cachedBinaries'
    @Override
    void apply(Project project) {
        ((ExtensionAware)project.dependencies).extensions.create(EXTENSION_NAME, DependencyHandlerExtension, project)
    }
}

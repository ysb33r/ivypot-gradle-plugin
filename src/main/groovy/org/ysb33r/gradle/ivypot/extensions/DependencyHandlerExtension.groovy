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

package org.ysb33r.gradle.ivypot.extensions

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.ysb33r.gradle.ivypot.internal.DefaultBinaryArtifactDependency
import org.ysb33r.gradle.ivypot.repositories.binary.NamedBinaryArtifactDependency

@CompileStatic
class DependencyHandlerExtension implements NamedDomainObjectContainer<NamedBinaryArtifactDependency> {

    DependencyHandlerExtension(Project project) {
        Closure instantiator = { Project p, String name ->
            DefaultBinaryArtifactDependency.create(p, name)
        }.curry(project)

        delegate = project.container(NamedBinaryArtifactDependency, instantiator)
        delegate.addRule('new dependency', instantiator)
    }

    void add(String notation) {
        this.delegate.create(notation)
    }

    @Delegate
    private final NamedDomainObjectContainer<NamedBinaryArtifactDependency> delegate
}

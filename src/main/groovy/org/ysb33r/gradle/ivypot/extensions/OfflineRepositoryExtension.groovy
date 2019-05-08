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
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

@CompileStatic
class OfflineRepositoryExtension {

    String groovyVersion = GroovySystem.version
    String ivyVersion = '2.3.0'

    OfflineRepositoryExtension(Project project) {
        this.project = project


        this.class.getResourceAsStream('/META-INF/org.ysb33r.gradle.ivypot.versions.properties').withCloseable {
            if (it != null) {
                Properties props = new Properties()
                props.load(it)

                if (props.containsKey('ivy.version')) {
                    ivyVersion = props['ivy.version']
                }
                if (props.containsKey('groovy.version')) {
                    groovyVersion = props['groovy.version']
                }
            }
        }
    }

    Configuration getConfiguration() {
        def deps = [
                createDependency("${GROOVY_GROUP}:groovy-ant:${groovyVersion}"),
                createDependency("${GROOVY_GROUP}:groovy-xml:${groovyVersion}"),
                createDependency("${IVY_GROUP}:ivy:${ivyVersion}")
        ]

        project.configurations.detachedConfiguration(
                deps.toArray() as Dependency[]
        )
    }

    private Dependency createDependency(final String notation, final Closure configurator = null) {
        if (configurator) {
            project.dependencies.create(notation, configurator)
        } else {
            project.dependencies.create(notation)
        }
    }

    private static final String GROOVY_GROUP = 'org.codehaus.groovy'
    private static final String IVY_GROUP = 'org.apache.ivy'
    private Project project
}

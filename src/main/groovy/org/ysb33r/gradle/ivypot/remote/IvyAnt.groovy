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

package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.apache.tools.ant.BuildListener
import org.apache.tools.ant.DefaultLogger
import org.apache.tools.ant.Project

@CompileStatic
class IvyAnt {
    public static final String CONFIGURE_TASK = 'ivyConfigure'
    public static final String CONFIGURE_CLASS = 'org.apache.ivy.ant.IvyConfigure'
    public static final String RESOLVE_TASK = 'ivyResolve'
    public static final String RESOLVE_CLASS = 'org.apache.ivy.ant.IvyResolve'

    IvyAnt(File ivySettings) {
        this.ivySettings = ivySettings
        ivyAnt = new AntBuilder()
        configureAnt()
        initialiseIvy()
    }

    void setLogLevel(int antLogLevel) {
        Project localRef = ivyAnt.project

        localRef.buildListeners.findAll { BuildListener it ->
            it instanceof DefaultLogger
        }.each {
            ((DefaultLogger) it).messageOutputLevel = antLogLevel
        }
    }

    void resolve(File repoRoot, List<IvyDependency> deps, boolean overwrite) {
        repoRoot.mkdirs()
        deps.each {
            ivyInstall(it, overwrite, repoRoot)
        }
    }

    @CompileDynamic
    private void initialiseIvy() {
        ivyAnt."${CONFIGURE_TASK}" file: ivySettings.absolutePath
    }

    @CompileDynamic
    private void ivyInstall(IvyDependency dep, boolean overwrite, File repoRoot) {

        if (dep.extension || dep.classifier) {
            File ivyFile = new File(repoRoot,"ivy-${dep.module}.xml")
            Map artifactAttributes = [name: dep.module, type: dep.typeFilter]
            if (dep.extension) {
                artifactAttributes.'ext' = dep.extension
            }
            if (dep.classifier) {
                artifactAttributes.'e:classifier' = dep.classifier
            }
            def writer = new StringWriter()
            new MarkupBuilder(writer).'ivy-module'(version: '2.0', 'xmlns:e': 'http://ant.apache.org/ivy/extra') {
                info( organisation:'ignore', module:'ignore')
                dependencies {
                    dependency(
                            org: dep.organisation,
                            name: dep.module,
                            rev: dep.revision,
                            transitive: dep.transitive,
                            conf: dep.confFilter
                    ) {
                        artifact(artifactAttributes)
                    }
                }
            }
            ivyFile.text = writer.toString()
            ivyFile.deleteOnExit()
            ivyAnt."${RESOLVE_TASK}"(
                    file: ivyFile.absolutePath
            )
        } else {
            ivyAnt."${RESOLVE_TASK}"(
                    inline: true,
                    organisation: dep.organisation, module: dep.module, revision: dep.revision,
                    transitive: dep.transitive,
                    type: dep.typeFilter,
                    conf: dep.confFilter
            )
        }
    }

    @CompileDynamic
    private void configureAnt() {
        ivyAnt.taskdef(name: CONFIGURE_TASK, classname: CONFIGURE_CLASS)
        ivyAnt.taskdef(name: RESOLVE_TASK, classname: RESOLVE_CLASS)
    }

    void test() {
        //IvyPatternHelper.substitute()
    }

    private final AntBuilder ivyAnt
    private final File ivySettings
}

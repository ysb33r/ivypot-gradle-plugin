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

import groovy.xml.MarkupBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.IgnoreIf
import spock.lang.Specification

class IvyAntSpec extends Specification {

    @Rule
    TemporaryFolder testProject

    File ivySettings
    File repoDir
    File cacheDir

    void setup() {
        ivySettings = new File(testProject.root, 'ivysettings.xml')
        repoDir = new File(testProject.root, 'repo')
        cacheDir = new File(testProject.root, 'cache')
        writeIvySettings()
    }

    void 'Can initialise IvyAnt'() {
        when:
        IvyAnt ivyAnt = new IvyAnt(ivySettings)

        then:
        noExceptionThrown()

        when:
        ivyAnt.logLevel = 2

        then:
        noExceptionThrown()
    }

    @IgnoreIf({ System.getProperty('OFFLINE') })
    void 'Can resolve an artifact'() {

        when:
        def dep = new IvyDependency(
                organisation: 'commons-io',
                module: 'commons-io',
                revision: '2.4',
                transitive: true,
                typeFilter: '*',
                confFilter: '*'
        )

        new IvyAnt(ivySettings).resolve(repoDir, [dep], true)

        then:
        verifyAll {
            new File(repoDir, 'commons-io/commons-io/2.4/ivy-2.4.xml').exists()
            new File(repoDir, 'commons-io/commons-io/2.4/commons-io-2.4.jar').exists()
        }
    }

    private void writeIvySettings() {
        ivySettings.withWriter { writer ->
            new MarkupBuilder(writer).ivysettings {
                settings(defaultResolver: 'foobar')
                caches(
                        defaultCacheDir: repoDir.absolutePath,
                        resolutionCacheDir: cacheDir.absolutePath,
                        artifactPattern: '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])',
                        ivyPattern: '[organisation]/[module]/[revision]/ivy-[revision].xml',
                )
                resolvers {
                    chain(name: 'foobar', returnFirst: true) {
                        ibiblio(name: 'MavenRepo', m2compatible: true)
                    }
                }
            }
        }
    }
}

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
        def dep1 = new IvyDependency(
                organisation: 'commons-io',
                module: 'commons-io',
                revision: '2.4',
                transitive: true,
                typeFilter: '*',
                confFilter: '*'
        )

        def dep2 = new IvyDependency(
                organisation: 'org.apache.karaf',
                module: 'apache-karaf',
                revision: '4.2.2',
                extension: 'zip',
                transitive: true,
                typeFilter: '*',
                confFilter: '*'
        )

        def dep3 = new IvyDependency(
                organisation: 'org.ysb33r.gradle',
                module: 'grolifant',
                revision: '0.12.1',
                classifier: 'sources',
                transitive: false,
                extension: 'jar',
                typeFilter: '*',
                confFilter: '*'
        )

        new IvyAnt(ivySettings).resolve(repoDir, [dep3, dep2, dep1], true)

        then:
        verifyAll {
            file_exists 'commons-io/commons-io/2.4/ivy-2.4.xml'
            file_exists 'commons-io/commons-io/2.4/commons-io-2.4.jar'
            file_exists 'org.apache.karaf/apache-karaf/4.2.2/apache-karaf-4.2.2.zip'
            file_exists 'org.ysb33r.gradle/grolifant/0.12.1/grolifant-0.12.1-sources.jar'
        }
    }

    private boolean file_exists(String path) {
        new File(repoDir, path).exists()
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
                        ibiblio(name: 'BintrayJCenter', m2compatible: true, root: 'https://jcenter.bintray.com/')
                    }
                }
            }
        }
    }
}

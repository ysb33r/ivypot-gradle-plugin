//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2015
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

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositorySyncSpec extends Specification {

    Project project
    OfflineRepositorySync syncTask

    void setup() {
        project = ProjectBuilder.builder().build()
        syncTask = project.tasks.create('syncTask',OfflineRepositorySync)
    }

    def "TEST configuration" () {

        given:
        project.allprojects {

            // tag::usage[]
            syncTask {
                repoRoot '/path/to/folder'

                configurations 'compile','testCompile'

                repositories {
                    mavenCentral()
                    mavenLocal()
                    jcenter()

                    maven {
                        url 'http://foo.com/bar'
                    }

                    maven {
                        url 'http://hog.com/whole'
                        artifactUrls 'http://hog.com/one'
                        artifactUrls 'http://hog.com/two'

                        credentials {
                            username 'the'
                            password 'pig'
                        }
                    }

                    // ivy {
                    // }

//                    flatDir {
//                        dirs
//                    }
                }
            }
            // end::usage[]
        }
println "*** ${syncTask.repositories.names}"
        // Need to extract repositories in order collected using these gradle-assigned names
        def mavenC = syncTask.repositories.getByName('MavenRepo')
        def mavenL = syncTask.repositories.getByName('MavenLocal')
        def maven2 = syncTask.repositories.getByName('maven')
        def maven3 = syncTask.repositories.getByName('maven2')
        def bintray= syncTask.repositories.getByName('BintrayJCenter')

        expect: 'Local repo has been set'
        syncTask.repoRoot == new File('/path/to/folder').absoluteFile

        and: 'mavenCentral is loaded'
        mavenC.resolverXml() == """<ibiblio name="MavenRepo" m2compatible="true"/>"""

        and: 'a maven repo can be added'
        maven2.url == 'http://foo.com/bar'.toURI()
        maven2.resolverXml() == '''<url name='maven' m2compatible='true'></url>'''

        and: 'a maven repo with artifact urls and credentials can be added'
        maven3.url == 'http://hog.com/whole'.toURI()
        maven3.artifactUrls.contains('http://hog.com/one'.toURI())
        maven3.artifactUrls.contains('http://hog.com/two'.toURI())
        maven3.credentials.username == 'the'
        maven3.credentials.password == 'pig'
        maven3.resolverXml() == '''<url name='maven2' m2compatible='true'>''' +
            "<artifact pattern='http://hog.com/one/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>" +
            "<artifact pattern='http://hog.com/two/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>" +
            '</url>'

        and: 'JCenter is loaded'
        bintray.resolverXml() == '<ibiblio name="BintrayJCenter" root="https://jcenter.bintray.com/" m2compatible="true"/>'

        and: 'mavenLocal is loaded'
        mavenL.resolverXml() == """<ibiblio name="MavenLocal" root="file:${System.getProperty('user.home')}/.m2/repository/" m2compatible="true" checkmodified="true" changingPattern=".*" changingMatcher="regexp"/>"""

    }
}
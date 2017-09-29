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

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositorySyncSpec extends Specification {

    Project project
    OfflineRepositorySync syncTask

    void setupSpec() {
        OfflineRepositorySync.DONT_LOOK_FOR_IVY_JAR = true
    }
    void setup() {
        project = ProjectBuilder.builder().build()
        syncTask = project.tasks.create('syncTask',OfflineRepositorySync)
    }

    def "Setting up repositories" () {

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

                    ivy {
                        url 'http://ivy/climber'
                        layout 'maven'
                    }

                    ivy {
                        url 'http://gradle/grover'
                        layout 'gradle'
                    }

                    ivy {
                        url 'http://hog/roast'
                        layout 'ivy'

                        credentials {
                            username 'the'
                            password 'pig'
                        }
                    }

                    ivy {
                        url 'http://pat/tern'
                        layout 'pattern', {
                            artifact '[artifact].[ext]'
                            ivy 'foo/ivy.xml'
                        }
                    }

//                    flatDir {
//                        dirs
//                    }
                }
            }
            // end::usage[]
        }

        // Need to extract repositories in order collected using these gradle-assigned names
        def mavenC =    syncTask.repositories.getByName('MavenRepo')
        def mavenL =    syncTask.repositories.getByName('MavenLocal')
        def maven2 =    syncTask.repositories.getByName('maven')
        def maven3 =    syncTask.repositories.getByName('maven2')
        def bintray=    syncTask.repositories.getByName('BintrayJCenter')
        def ivyMaven=   syncTask.repositories.getByName('ivy')
        def ivyGradle=  syncTask.repositories.getByName('ivy2')
        def ivyIvy=     syncTask.repositories.getByName('ivy3')
        def ivyPattern= syncTask.repositories.getByName('ivy4')

        expect: 'Local repo has been set'
        syncTask.repoRoot == project.file('/path/to/folder')

        and: 'mavenCentral is loaded'
        mavenC.resolverXml() == """<ibiblio name="MavenRepo" m2compatible="true"/>"""

        and: 'a maven repo can be added'
        maven2.url == 'http://foo.com/bar'.toURI()
        maven2.resolverXml() == "<ibiblio name='maven' m2compatible='true' root='http://foo.com/bar'/>"

        and: 'a maven repo with artifact urls and credentials can be added'
        maven3.url == 'http://hog.com/whole'.toURI()
        maven3.artifactUrls.contains('http://hog.com/one'.toURI())
        maven3.artifactUrls.contains('http://hog.com/two'.toURI())
        maven3.credentials.username == 'the'
        maven3.credentials.password == 'pig'
//        maven3.resolverXml() == "<ibiblio name='maven2' m2compatible='true' root='http://hog.com/whole'/>"
        maven3.resolverXml() == '''<chain name='maven2'><ibiblio name='maven2_root' m2compatible='true' root='http://hog.com/whole' usepoms='true'/>''' +
            "<ibiblio name='maven2_0' m2compatible='true' root='http://hog.com/one' usepoms='false'/>" +
            "<ibiblio name='maven2_1' m2compatible='true' root='http://hog.com/two' usepoms='false'/>" +
            '</chain>'

//        maven3.resolverXml() == '''<url name='maven2' m2compatible='true'>''' +
//            "<artifact pattern='http://hog.com/whole/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>" +
//            "<artifact pattern='http://hog.com/one/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>" +
//            "<artifact pattern='http://hog.com/two/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>" +
//            '</url>'

        and: 'JCenter is loaded'
        bintray.resolverXml() == '<ibiblio name="BintrayJCenter" root="https://jcenter.bintray.com/" m2compatible="true"/>'

        and: 'mavenLocal is loaded'
        mavenL.resolverXml() == """<ibiblio name="MavenLocal" root="${new File(System.getProperty('user.home')).absoluteFile.toURI()}.m2/repository/" m2compatible="true" checkmodified="true" changingPattern=".*" changingMatcher="regexp"/>"""

        and: 'ivy with maven layout loaded'
        ivyMaven.resolverXml() == '''<url name='ivy' m2compatible='true'>''' +
            "<ivy pattern='http://ivy/climber/[organisation]/[module]/[revision]/ivy-[revision].xml'/>" +
            "<artifact pattern='http://ivy/climber/${IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN}'/>" +
            '</url>'

        and: 'ivy with gradle layout loaded'
        ivyGradle.resolverXml() == '''<url name='ivy2' m2compatible='false'>''' +
            "<ivy pattern='http://gradle/grover/[organisation]/[module]/[revision]/ivy-[revision].xml'/>" +
            "<artifact pattern='http://gradle/grover/${IvyArtifactRepository.GRADLE_ARTIFACT_PATTERN}'/>" +
            '</url>'

        and: 'ivy with ivy layout loaded + credentials'
        ivyIvy.resolverXml() == '''<url name='ivy3' m2compatible='false'>''' +
            "<ivy pattern='http://hog/roast/${IvyArtifactRepository.IVY_ARTIFACT_PATTERN}'/>" +
            "<artifact pattern='http://hog/roast/${IvyArtifactRepository.IVY_ARTIFACT_PATTERN}'/>" +
            '</url>'
        ivyIvy.credentials.username == 'the'
        ivyIvy.credentials.password == 'pig'

        and: 'ivy with pattern layout loaded'
        ivyPattern.resolverXml() == '''<url name='ivy4' m2compatible='false'>''' +
            "<ivy pattern='http://pat/tern/foo/ivy.xml'/>" +
            "<artifact pattern='http://pat/tern/[artifact].[ext]'/>" +
            '</url>'

        //and: 'a flatDir repo can be added'
    }

    def "Not specifying a configuration, means all configurations are loaded"() {
        given:
        project.allprojects {
            syncTask {

            }

            configurations {
                config1
                config2
            }
        }
        ConfigurationContainer configs = syncTask.configurations

        expect:
        configs.getByName('config1') != null
        configs.getByName('config2') != null
        configs.size() == 2
    }

    def "Specifying configurations means only those are added"() {
        given:
        project.allprojects {
            syncTask {
                configurations 'config1'
            }

            configurations {
                config1
                config2
            }
        }
        ConfigurationContainer configs = syncTask.configurations

        when:
        configs.getByName('config2') != null

        then:
        thrown(org.gradle.api.UnknownDomainObjectException)

        and:
        configs.getByName('config1') != null
        configs.size() == 1
    }

}
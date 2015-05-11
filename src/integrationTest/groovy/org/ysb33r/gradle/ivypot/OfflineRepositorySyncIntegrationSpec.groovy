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
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.IgnoreIf
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositorySyncIntegrationSpec extends Specification {

    static final boolean OFFLINE = System.getProperty('IS_OFFLINE')
    static final File TESTROOT = new File( "${System.getProperty('TESTROOT') ?: new File('./build').absolutePath}/integrationTest/orsis" )
    static final File LOCALREPO = new File(TESTROOT,'local-offline-repo')

    Project project
    Task syncTask

    void setup() {

        if(TESTROOT.exists()) {
            TESTROOT.deleteDir()
        }

        TESTROOT.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(TESTROOT).build()
        project.apply plugin : 'org.ysb33r.ivypot'
    }

    @IgnoreIf({OFFLINE})
    def "Can we sync from mavenCentral?"() {

        given:
            def pathToLocalRepo = LOCALREPO

            project.allprojects {

                configurations {
                    compile
                }

                // tag::example_jcenter[]
                dependencies {
                    compile 'commons-io:commons-io:2.4'
                }

                syncRemoteRepositories {
                    repositories {
                        mavenCentral()
                    }

                    repoRoot "${pathToLocalRepo}"
                }
                // end::example_jcenter[]
            }

            project.evaluate()
            project.tasks.syncRemoteRepositories.execute()

        expect:
            LOCALREPO.exists()
            new File(LOCALREPO,'commons-io/commons-io//ivys/ivy-2.4.xml').exists()
            new File(LOCALREPO,'commons-io/commons-io//jars/commons-io-2.4.jar').exists()
    }

    @IgnoreIf({OFFLINE})
    def "Two syncs to same folder should not cause an overwrite exceptions"() {

        given:
            def pathToLocalRepo = LOCALREPO
            project.tasks.create('secondSync',OfflineRepositorySync)

            project.allprojects {

                configurations {
                    compile
                }

                dependencies {
                    compile 'commons-io:commons-io:2.4'
                }

                syncRemoteRepositories {
                    repositories {
                        jcenter()
                        mavenCentral()
                    }

                    repoRoot "${pathToLocalRepo}"
                }
                secondSync {
                    repositories {
                        jcenter()
                        mavenCentral()
                    }

                    repoRoot "${pathToLocalRepo}"
                }
            }

            project.evaluate()
            project.tasks.syncRemoteRepositories.execute()
            project.tasks.secondSync.execute()

        expect:
            LOCALREPO.exists()
            project.tasks.secondSync.didWork

            new File(LOCALREPO,'commons-io/commons-io//ivys/ivy-2.4.xml').exists()
            new File(LOCALREPO,'commons-io/commons-io//jars/commons-io-2.4.jar').exists()
    }

}
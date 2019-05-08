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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.IgnoreIf
import spock.lang.Issue
import spock.lang.PendingFeature
import spock.lang.Specification

/** These fixtures needs to run in the order specified as these tests are expensive in terms of downloads
 * The repository is only created once during the run to save on download time.
 *
 * @author Schalk W. Cronjé
 */
@IgnoreIf({ System.getProperty('IS_OFFLINE') })
class OfflineRepositorySyncIntegrationSpec extends Specification {

    public static final String DEFAULT_TASK = 'syncRemoteRepositories'
    public static final String REPO_PATH = 'repo'

    @Rule
    TemporaryFolder testFolder

    File projectDir
    File buildFile
    File settingsFile
    File repoDir
    String buildScript

    void setup() {
        projectDir = testFolder.root
        buildFile = new File(projectDir, 'build.gradle')
        settingsFile = new File(projectDir, 'settings.gradle')
        repoDir = new File(projectDir, REPO_PATH)
        settingsFile.text = 'rootProject.name="testproject"'

    }

    void 'Can we sync from mavenCentral?'() {

        setup:
        writeBuildFile """
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
            }
            // end::example_jcenter[]
        """

        when:
        BuildResult result = build()

        then:
        verifyAll {
            result.task(":${DEFAULT_TASK}").outcome == TaskOutcome.SUCCESS
            repoDir.exists()
        }
        verifyAll {
            file_exists 'commons-io/commons-io/2.4/ivy-2.4.xml'
            file_exists 'commons-io/commons-io/2.4/commons-io-2.4.jar'
        }
    }

    void 'Honour non-transitive dependencies'() {

        setup:
        writeBuildFile """
            configurations {
                compile
            }

            dependencies {
                compile 'junit:junit:4.12', {
                    transitive = false
                }
            }

            syncRemoteRepositories {
                repositories {
                    mavenCentral()
                }
            }        
         """

        when:
        BuildResult result = build()

        then:
        verifyAll {
            result.task(":${DEFAULT_TASK}").outcome == TaskOutcome.SUCCESS
        }
        verifyAll {
            file_exists 'junit/junit/4.12/ivy-4.12.xml'
            file_exists 'junit/junit/4.12/junit-4.12.jar'
            not_file_exists 'org.hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar'
        }
    }

    void 'Can we sync from mavenCentral using a different local artifactPattern?'() {

        setup:
        writeBuildFile """
            configurations {
                compile
            }

            dependencies {
                compile 'commons-io:commons-io:2.4'
            }

            syncRemoteRepositories {
                repositories {
                    mavenCentral()
                }

                repoArtifactPattern = '[organisation]/[module]/[revision]/[type]s/[artifact]-[revision](.[ext])'
            }
        """

        when:
        BuildResult result = build()

        then:
        verifyAll {
            result.task(":${DEFAULT_TASK}").outcome == TaskOutcome.SUCCESS
        }
        verifyAll {
            file_exists 'commons-io/commons-io/2.4/ivy-2.4.xml'
            file_exists 'commons-io/commons-io/2.4/jars/commons-io-2.4.jar'
        }
    }

    void "Two syncs to same folder should not cause an overwrite exceptions"() {

        setup:
        writeBuildFile """
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
            }

        """

        when:
        BuildResult result1 = build()
        BuildResult result2 = build()

        then:
        verifyAll {
            result1.task(":${DEFAULT_TASK}").outcome == TaskOutcome.SUCCESS
            result2.task(":${DEFAULT_TASK}").outcome == TaskOutcome.UP_TO_DATE
            repoDir.exists()
        }
        verifyAll {
            file_exists 'commons-io/commons-io/2.4/ivy-2.4.xml'
            file_exists 'commons-io/commons-io/2.4/commons-io-2.4.jar'
        }
    }

    void "Setting includeBuildScriptDependencies means that buildscript configuration will be added"() {

        given:
        withBuildScript '''
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath 'commons-io:commons-io:2.4'
            }
        '''

        writeBuildFile """
            syncRemoteRepositories {
                repositories {
                    mavenCentral()
                }

                includeBuildScriptDependencies = true
            }
        """

        when:
        BuildResult result = build()

        then:
        verifyAll {
            result.task(":${DEFAULT_TASK}").outcome == TaskOutcome.SUCCESS
            repoDir.exists()
        }
        verifyAll {
            file_exists 'commons-io/commons-io/2.4/ivy-2.4.xml'
            file_exists 'commons-io/commons-io/2.4/commons-io-2.4.jar'
        }
    }

    @Issue('https://github.com/ysb33r/ivypot-gradle-plugin/issues/12')
    @PendingFeature
    void "Can we sync from a rubygems proxy?"() {

        setup:
        writeBuildFile """
        configurations {
                compile
            }

            // tag::example_rubygems]
            dependencies {
                compile 'rubygems:colorize:0.7.7'
            }

            syncRemoteRepositories {
                repositories {
                    maven { url 'http://rubygems.lasagna.io/proxy/maven/releases' }
                }
            }
            // end::example_rubygems[]
        """

        when:
        build()

        then:
        file_exists 'rubygems/colorize/0.7.7/ivys/ivy.xml'
        file_exists 'rubygems/colorize/0.7.7/gems/colorize.gem'
    }

    void 'Can we sync from Google?'() {
        setup:
        writeBuildFile """
            configurations {
                compile
            }

            // tag::example_google[]
            dependencies {
                compile 'com.android.support.constraint:constraint-layout:1.0.2'
            }

            syncRemoteRepositories {
                repositories {
                    google()
                }
            }
            // end::example_google[]
        """

        when:
        build()

        then:
        file_exists 'com.android.support.constraint/constraint-layout/1.0.2/constraint-layout-1.0.2.aar'
        file_exists 'com.android.support.constraint/constraint-layout-solver/1.0.2/constraint-layout-solver-1.0.2.jar'
    }

    @Issue('https://github.com/ysb33r/ivypot-gradle-plugin/issues/41')
    void 'Sync a remote binary that is defined in the task'() {
        setup:
        writeBuildFile """
        syncRemoteRepositories {
            binaryRepositories {
                nodejs {
                    rootUri = 'https://nodejs.org/dist/'
                    artifactPattern = 'v[revision]/[module]-v[revision]-[classifier].[ext]'
                }
            }
            
            cachedBinaries.add 'nodejs:node:7.10.0:linux-x64@tar.xz'
        }
        """

        when:
        build()

        then:
        file_exists 'binaries/nodejs/v7.10.0/node-v7.10.0-linux-x64.tar.xz'
    }

    @Issue('https://github.com/ysb33r/ivypot-gradle-plugin/issues/34')
    void 'Use a different extension'() {
        setup:
        writeBuildFile """
            configurations {
                karaf
            }

            // tag::example_with_explicit_extension[]
            dependencies {
                karaf 'org.apache.karaf:apache-karaf:4.2.2@zip'
            }

            syncRemoteRepositories {
                repositories {
                    mavenCentral()
                }
            }
            // end::example_with_explicit_extension[]
        """

        when:
        build()

        then:
        file_exists 'com.android.support.constraint/constraint-layout/1.0.2/constraint-layout-1.0.2.aar'
    }

    private boolean file_exists(String path) {
        new File(repoDir, path).exists()
    }

    private boolean not_file_exists(String path) {
        !file_exists(path)
    }

    private BuildResult build() {
        GradleRunner.create().withDebug(true)
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withArguments(['-i', '-s', DEFAULT_TASK])
                .forwardOutput()
                .build()
    }

    void withBuildScript(String content) {
        buildScript = """
        buildscript {
            ${content}
        }
        """
    }

    private void writeBuildFile(String content) {
        buildFile.text = """
        ${buildScript ?: ''}

        plugins {
            id 'org.ysb33r.ivypot'
        }

        repositories {
            mavenCentral()
            jcenter()
        }

        syncRemoteRepositories.repoRoot '${REPO_PATH}'

        ${content}
        """
    }
}
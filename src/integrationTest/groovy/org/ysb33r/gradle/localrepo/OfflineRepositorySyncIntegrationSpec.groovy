package org.ysb33r.gradle.localrepo

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

        project = ProjectBuilder.builder().build()
        project.apply plugin : 'org.ysb33r.offline-repo'
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
        false
    }


}
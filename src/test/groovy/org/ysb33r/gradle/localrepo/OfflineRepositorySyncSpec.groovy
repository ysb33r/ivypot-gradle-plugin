package org.ysb33r.gradle.localrepo

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositorySyncSpec extends Specification {

    Project project
    Task syncTask

    void setup() {
        project = ProjectBuilder.builder().build()
        syncTask = project.tasks.create('syncTask',OfflineRepositorySync)
    }

    @Ignore
    def "TEST configuration" () {

        given:
        project.allprojects {

            // tag::usage[]
            syncTask {
                repoRoot '/path/to/folder'

                configurations 'compile','testCompile'

                repositories {
                    jcenter()
                }
            }
            // end::usage[]
        }

        expect:

            syncTask.localRepo == new File('/path/to/folder').absoluteFile
    }
}
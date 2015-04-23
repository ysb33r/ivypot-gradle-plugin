package org.ysb33r.gradle.localrepo

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositoryPluginSpec extends Specification {

    def "Can the plugin be applied"() {
        given:
            def project = ProjectBuilder.builder().build()
            project.apply plugin : 'org.ysb33r.offline-repo'

        expect:
            project.tasks.getByName('syncRemoteRepositories') instanceof OfflineRepositorySync
    }
}
package org.ysb33r.gradle.localrepo

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Schalk W. Cronjé
 */
class OfflineRepositoryPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.create 'syncRemoteRepositories', OfflineRepositorySync
    }
}

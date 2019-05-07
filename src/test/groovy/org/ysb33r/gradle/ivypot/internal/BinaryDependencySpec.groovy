package org.ysb33r.gradle.ivypot.internal

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BinaryDependencySpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void 'Create a binary dependency directly'() {
        when:
        DefaultBinaryArtifactDependency dep = DefaultBinaryArtifactDependency.create(project, "gradleDist:gradle:4.5.1:bin@zip")

        then:
        verifyAll {
            dep.group == 'gradleDist'
            dep.module == 'gradle'
            dep.revision == '4.5.1'
            dep.classifier == 'bin'
            dep.type == 'zip'
            dep.extension == 'zip'
        }
    }
}
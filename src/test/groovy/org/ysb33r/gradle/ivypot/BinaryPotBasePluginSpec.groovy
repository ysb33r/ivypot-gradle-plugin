package org.ysb33r.gradle.ivypot

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BinaryPotBasePluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void setup() {
        project.apply plugin: 'org.ysb33r.ivypot.binary.base'
    }

    void 'Add an external dependency'() {

        when:
        project.allprojects {
            dependencies {
                cachedBinaries.add 'group1:module:version:classifier@ext'
                cachedBinaries.add 'group2:module:version:classifier'
                cachedBinaries.add 'group3:module:version'
            }
        }

        def bins = project.dependencies.cachedBinaries.asMap.values()

        then:
        bins.size() == 3
        bins[0].group == 'group1'
        bins[0].module == 'module'
        bins[0].revision == 'version'
        bins[0].classifier == 'classifier'
        bins[0].extension == 'ext'
        bins[1].group == 'group2'
        bins[1].classifier == 'classifier'
        bins[1].extension == 'jar'
        bins[2].group == 'group3'
        bins[2].classifier == null
        bins[2].extension == null
    }
}

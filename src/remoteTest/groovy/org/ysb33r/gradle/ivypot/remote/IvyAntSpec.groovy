package org.ysb33r.gradle.ivypot.remote

import groovy.xml.MarkupBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.IgnoreIf
import spock.lang.Specification

class IvyAntSpec extends Specification {

    @Rule
    TemporaryFolder testProject

    File ivySettings
    File repoDir
    File cacheDir

    void setup() {
        ivySettings = new File(testProject.root, 'ivysettings.xml')
        repoDir = new File(testProject.root, 'repo')
        cacheDir = new File(testProject.root, 'cache')
        writeIvySettings()
    }

    void 'Can initialise IvyAnt'() {
        when:
        IvyAnt ivyAnt = new IvyAnt(ivySettings)

        then:
        noExceptionThrown()

        when:
        ivyAnt.logLevel = 2

        then:
        noExceptionThrown()
    }

    @IgnoreIf({ System.getProperty('OFFLINE') })
    void 'Can resolve an artifact'() {

        when:
        def dep = new IvyDependency(
                organisation: 'commons-io',
                module: 'commons-io',
                revision: '2.4',
                transitive: true,
                typeFilter: '*',
                confFilter: '*'
        )

        new IvyAnt(ivySettings).resolve(repoDir, [dep], true)

        then:
        verifyAll {
            new File(repoDir, 'commons-io/commons-io/2.4/ivy-2.4.xml').exists()
            new File(repoDir, 'commons-io/commons-io/2.4/commons-io-2.4.jar').exists()
        }
    }

    private void writeIvySettings() {
        ivySettings.withWriter { writer ->
            new MarkupBuilder(writer).ivysettings {
                settings(defaultResolver: 'foobar')
                caches(
                        defaultCacheDir: repoDir.absolutePath,
                        resolutionCacheDir: cacheDir.absolutePath,
                        artifactPattern: '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])',
                        ivyPattern: '[organisation]/[module]/[revision]/ivy-[revision].xml',
                )
                resolvers {
                    chain(name: 'foobar', returnFirst: true) {
                        ibiblio(name: 'MavenRepo', m2compatible: true)
                    }
                }
            }
        }
    }
}

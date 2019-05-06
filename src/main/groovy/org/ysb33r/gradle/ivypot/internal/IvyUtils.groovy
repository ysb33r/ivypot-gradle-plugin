package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.ysb33r.gradle.ivypot.repositories.RepositoryHandler
import org.ysb33r.grolifant.api.FileUtils

@CompileStatic
class IvyUtils {
    public static final String REMOTECHAINNAME = '~~~remote~~~resolvers~~~'

    @CompileDynamic
    static void writeSettingsFile(
            File settingsFile,
            RepositoryHandler repositories,
            File repoRoot,
            File cacheDir,
            String ivyPattern,
            String artifactPattern,
            Iterable<Map<String, String>> repositoryCredentials
    ) {
        def xmlWriter = new StringWriter()
        def xml = new MarkupBuilder(xmlWriter)
        xml.ivysettings {
            settings(defaultResolver: REMOTECHAINNAME)
            caches(
                    defaultCacheDir: repoRoot.absolutePath,
                    artifactPattern: artifactPattern,
                    ivyPattern: ivyPattern,
                    resolutionCacheDir: cacheDir.absolutePath
            )

            repositoryCredentials.each { repo ->
                credentials(repo)
            }

            resolvers {
                chain(name: REMOTECHAINNAME, returnFirst: true) {
                    repositories.each { repo ->
                        repo.writeTo(xml)
                    }
                }
            }
        }
        settingsFile.text = xmlWriter.toString()
    }
}

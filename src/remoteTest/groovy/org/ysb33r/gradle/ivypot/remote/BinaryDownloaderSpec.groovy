package org.ysb33r.gradle.ivypot.remote

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.IgnoreIf
import spock.lang.Specification


class BinaryDownloaderSpec extends Specification {

    @Rule
    TemporaryFolder testFolder

    void 'Convert dependency to relativePath with classifier'() {
        when:
        def dep = new BinaryDependency(
                organisation: 'foo',
                module: 'bar',
                revision: '1.2.3',
                transitive: false,
                extension: 'tar',
                classifier: 'bin'
        )
        def pat = '[module]:[revision]:([classifier]:)[ext]'

        then:
        BinaryDownloader.makeRelativePath(pat,dep) == 'bar:1.2.3:bin:tar'
    }

    void 'Convert dependency to relativePath with optional classifier'() {
        when:
        def dep = new BinaryDependency(
                organisation: 'foo',
                module: 'bar',
                revision: '1.2.3',
                transitive: false,
                extension: 'tar'
        )
        def pat = '[module]:[revision]:([classifier]:)[ext]'

        then:
        BinaryDownloader.makeRelativePath(pat,dep) == 'bar:1.2.3:tar'
    }

    @IgnoreIf({ System.getProperty('OFFLINE') })
    void 'Can download a binary'() {
        setup:
        File repoRoot = testFolder.root
        def repositories = [ nodejs: new BinaryRepositoryDescriptor(
                rootUri: 'https://nodejs.org/dist/'.toURI(),
                artifactPattern: 'v[revision]/[module]-v[revision]-[classifier].[ext]'
        )]
        def downloader = new BinaryDownloader(repoRoot,repositories, true)
        def binaries = [new BinaryDependency(
                organisation: 'nodejs',
                module: 'node',
                revision: '7.10.0',
                classifier: 'linux-x64',
                extension: 'tar.xz'
        )]
        def expectedFile = new File(repoRoot,'nodejs/v7.10.0/node-v7.10.0-linux-x64.tar.xz')

        when:
        downloader.resolve(binaries, true)

        then:
        expectedFile.exists()

        when:
        long lastModified = expectedFile.lastModified()
        downloader.resolve(binaries, false)

        then:
        expectedFile.exists()
        lastModified == expectedFile.lastModified()

        when:
        downloader.resolve(binaries, true)

        then:
        expectedFile.exists()
        lastModified != expectedFile.lastModified()
    }
}

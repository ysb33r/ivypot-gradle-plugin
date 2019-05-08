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

package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic
import org.apache.ivy.core.IvyPatternHelper

import java.nio.file.Files

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

@CompileStatic
class BinaryDownloader {

    BinaryDownloader(
            File repoRoot,
            Map<String, BinaryRepositoryDescriptor> binaryRepositories,
            boolean logProgress
    ) {
        this.repoRoot = repoRoot
        repositories.putAll(binaryRepositories)
        this.logProgress = logProgress
    }

    void resolve(
            List<BinaryDependency> binaries,
            boolean overwrite
    ) {
        repoRoot.mkdirs()
        List<Artifact> artifacts = downloadableArtifacts(binaries)

        artifacts.each {
            if (overwrite || !it.destinationPath.exists()) {
                if (logProgress) {
                    println "Retrieving ${it.downloadUri}"
                }
                switch (it.downloadUri.scheme) {
                    case 'file':
                        copyFile(new File(it.downloadUri), it.destinationPath)
                        break
                    case 'http':
                    case 'https':
                        downloadFile(it.downloadUri, it.destinationPath)
                        break
                    default:
                        throw new RuntimeException("${it.downloadUri} is not a supported scheme")
                }
            }
        }
    }

    List<Artifact> downloadableArtifacts(List<BinaryDependency> binaries) {
        binaries.collect {
            BinaryRepositoryDescriptor repo = repositories[it.organisation]

            if (repo == null) {
                throw new RuntimeException("Organisation '${it.organisation}' is not mapped to a repository")
            }

            String relativePath = makeRelativePath(repo.artifactPattern, it)

            new Artifact(
                    downloadUri: repo.rootUri.resolve(relativePath),
                    destinationPath: new File(repoRoot, "${it.organisation}/${relativePath}")
            )
        }
    }

    private void copyFile(File from, File to) {
        Files.copy(from.toPath(), to.toPath(), COPY_ATTRIBUTES, REPLACE_EXISTING)
    }

    private void downloadFile(URI from, File to) {
        to.parentFile.mkdirs()
        File tmpFile = new File("${to}.tmp")
        try {
            from.toURL().withInputStream { strm ->
                tmpFile.withOutputStream { output ->
                    output << strm
                }
            }
            Files.move(tmpFile.toPath(), to.toPath(), ATOMIC_MOVE, REPLACE_EXISTING)
        } catch( Exception e ) {
            tmpFile.delete()
            throw e
        }
    }

    static String makeRelativePath(String artifactPattern, BinaryDependency dep) {
        IvyPatternHelper.substitute(
                artifactPattern,
                dep.organisation,
                dep.module,
                dep.revision,
                dep.module,
                dep.typeFilter == '*' ? dep.extension : dep.typeFilter,
                dep.extension,
                null,
                null,
                dep.classifier ? ['m:classifier': dep.classifier] : [:]
        )
    }

    static class Artifact {
        URI downloadUri
        File destinationPath
    }

    private final boolean logProgress = false
    private final File repoRoot
    private final Map<String, BinaryRepositoryDescriptor> repositories = [:]
}

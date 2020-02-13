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

package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.ysb33r.gradle.ivypot.repositories.Repository
import org.ysb33r.gradle.ivypot.repositories.RepositoryHandler
import org.ysb33r.grolifant.api.FileUtils

@CompileStatic
class IvyUtils {
    public static final String REMOTECHAINNAME = '~~~remote~~~resolvers~~~'

    @CompileDynamic
    static void writeSettingsFile(
            File settingsFile,
            RepositoryHandler repositoryHandler,
            File repoRoot,
            File cacheDir,
            String ivyPattern,
            String artifactPattern,
            Iterable<Map<String, String>> repositoryCredentials
    ) {
        List<Repository> repositories = repositoryHandler.sort { a,b ->
                a.index <=> b.index
        }

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

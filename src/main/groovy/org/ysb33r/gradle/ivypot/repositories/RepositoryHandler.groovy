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

package org.ysb33r.gradle.ivypot.repositories

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

import java.util.concurrent.atomic.AtomicInteger

/**
 * @since 1.0
 */
@CompileStatic
class RepositoryHandler {

    @Delegate(parameterAnnotations = true)
    final NamedDomainObjectContainer<Repository> registeredRepositories

    RepositoryHandler(Project project) {
        registeredRepositories = project.container(Repository)
    }

    MavenArtifactRepository jcenter(Action<? super MavenArtifactRepository> configurator) {
        addByName('BintrayJCenter', new JCenter(), configurator)
    }

    MavenArtifactRepository jcenter() {
        (MavenArtifactRepository) addByName('BintrayJCenter', new JCenter())
    }

    MavenArtifactRepository mavenCentral() {
        (MavenArtifactRepository) addByName('MavenRepo', new MavenCentral())
    }

    MavenArtifactRepository mavenLocal() {
        (MavenArtifactRepository) addByName('MavenLocal', new MavenLocal())
    }

    MavenArtifactRepository google() {
        (MavenArtifactRepository) addByName('Google', new Google())
    }

    MavenArtifactRepository maven(Closure configurator) {
        MavenArtifactRepository repo = new MavenArtifactRepository()
        Closure cfg = (Closure) configurator.clone()
        cfg.resolveStrategy = Closure.DELEGATE_FIRST
        cfg.delegate = repo
        addByName(
            "maven_${postfix}",
            repo,
            cfg as Action<MavenArtifactRepository>
        )
    }

    MavenArtifactRepository maven(Action<? super MavenArtifactRepository> configurator) {
        addByName("maven_${postfix}", new MavenArtifactRepository(), configurator)
    }

    IvyArtifactRepository ivy(Closure configurator) {
        IvyArtifactRepository repo = new IvyArtifactRepository()
        Closure cfg = (Closure) configurator.clone()
        cfg.resolveStrategy = Closure.DELEGATE_FIRST
        cfg.delegate = repo
        addByName(
            "ivy_${postfix}",
            repo,
            cfg as Action<IvyArtifactRepository>
        )
    }

    IvyArtifactRepository ivy(Action<? super IvyArtifactRepository> configurator) {
        addByName("ivy_${postfix}", new IvyArtifactRepository(), configurator)
    }

    private Repository addByName(final String name, Repository repo) {
        repo.name = name
        repo.index = registeredRepositories.size() + 1
        registeredRepositories.add(repo)
        repo
    }

    private MavenArtifactRepository addByName(final String name, MavenArtifactRepository repo, Action<? super MavenArtifactRepository> configurator) {
        repo.name = name
        repo.index = registeredRepositories.size() + 1
        configurator.execute(repo)
        registeredRepositories.add(repo)
        repo
    }

    private IvyArtifactRepository addByName(final String name, IvyArtifactRepository repo, Action<? super IvyArtifactRepository> configurator) {
        repo.name = name
        repo.index = registeredRepositories.size() + 1
        configurator.execute(repo)
        registeredRepositories.add(repo)
        repo
    }

    private String getPostfix() {
        nameSequence.incrementAndGet().toString()
    }

    private final AtomicInteger nameSequence = new AtomicInteger()
}

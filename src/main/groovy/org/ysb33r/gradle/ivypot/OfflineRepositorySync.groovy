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

package org.ysb33r.gradle.ivypot

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.Internal
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaExecSpec
import org.ysb33r.gradle.ivypot.extensions.DependencyHandlerExtension
import org.ysb33r.gradle.ivypot.extensions.OfflineRepositoryExtension
import org.ysb33r.gradle.ivypot.internal.AntLogLevel
import org.ysb33r.gradle.ivypot.internal.DefaultBinaryRepository
import org.ysb33r.gradle.ivypot.internal.IvyUtils
import org.ysb33r.gradle.ivypot.remote.BinaryDependency
import org.ysb33r.gradle.ivypot.remote.BinaryRepositoryDescriptor
import org.ysb33r.gradle.ivypot.remote.Downloader
import org.ysb33r.gradle.ivypot.remote.ExecutionData
import org.ysb33r.gradle.ivypot.remote.IvyDependency
import org.ysb33r.gradle.ivypot.repositories.RepositoryHandler
import org.ysb33r.gradle.ivypot.repositories.binary.BinaryArtifactDependency
import org.ysb33r.gradle.ivypot.repositories.binary.BinaryRepository
import org.ysb33r.grolifant.api.FileUtils
import org.ysb33r.grolifant.api.StringUtils

import static org.ysb33r.grolifant.api.FileUtils.resolveClassLocation
import static org.ysb33r.grolifant.api.FileUtils.toSafeFileName

@CompileStatic
class OfflineRepositorySync extends DefaultTask {

    @CompileDynamic
    OfflineRepositorySync() {

        repositories = new RepositoryHandler(project)

        inputs.properties.put('project configurations', { OfflineRepositorySync ors ->
            Set<Configuration> configs = ors.getConfigurations()
            configs.collect { Configuration c ->
                c.dependencies.collect { Dependency d ->
                    "${d.group}:${d.name}:${d.version}"
                }.join(',')
            }.join('|')
        }.curry(this))

        inputs.properties.put('cached binaries' , { OfflineRepositorySync ors ->
            ors.binaries*.toString().join('')
        }.curry(this))

        binaryRepositories = project.container(BinaryRepository) { String repoName ->
            DefaultBinaryRepository.create(repoName, null, null)
        }

        binaryRepositories.addRule('create new binary repository') { t ->
            binaryRepositories.create(t)
        }

        extBinaries = extensions.create('cachedBinaries', DependencyHandlerExtension, project)
    }

    /** The pattern that will be used to write artifacts into the target repository
     *
     */
    @Input
    String repoArtifactPattern = IvyArtifactRepository.GRADLE_ARTIFACT_PATTERN

    /** The pattern that will be used to write Ivy metafiles into the target repository
     *
     */
    @Input
    String repoIvyPattern = IvyArtifactRepository.GRADLE_ARTIFACT_PATTERN

    /** Include buildscript dependencies from the root project.
     *
     */
    @Input
    boolean includeBuildScriptDependencies = false

    @OutputDirectory
    File getRepoRoot() {
        project.file(this.repoRoot).absoluteFile
    }

    @OutputDirectory
    File getBinaryRepoRoot() {
        project.file(this.binaryRepoRoot).absoluteFile
    }

    NamedDomainObjectContainer<BinaryRepository> getBinaryRepositories() {
        this.binaryRepositories
    }

    void binaryRepositories(@DelegatesTo(NamedDomainObjectContainer) Closure cfg) {
        cfg.delegate = this.binaryRepositories
        cfg.resolveStrategy = Closure.DELEGATE_ONLY
        cfg()
    }

    void binaryRepositories(Action<NamedDomainObjectContainer<BinaryRepository>> action) {
        action.execute(this.binaryRepositories)
    }

    void repoRoot(Object repo) {
        this.repoRoot = repo
    }

    void setRepoRoot(Object repo) {
        this.repoRoot = repo
    }

    /** If no configurations were listed, returns all the configurations
     *
     * @return A project configuration container with all of the named configurations. Does not
     * return the {@code buildscript} configuration in here. The latter is made available directly to
     */
    Set<Configuration> getConfigurations() {
        Set<Configuration> configurationSet = []
        projectConfigurations.collect { Project p, List<Object> configs ->
            configurationSet.addAll(getConfigurationsFor(p, configs))
        }

        configurationSet.addAll(getConfigurationsFor(project, this.configurations))
        configurationSet
    }

    /** Clears the current list of configurations and assigns a new list
     *
     * @param names Configuration names to assign
     */
    void setConfigurations(Object... names) {
        this.configurations.clear()
        this.configurations.addAll(names as List)
    }

    /** Appends to the current list of configurations.
     *
     * @param names Configuration names to assign
     */
    void configurations(Object... names) {
        this.configurations.addAll(names as List)
    }

    /** Adds configurations from all projects except the current one
     *
     * <p> Any existing project configurations (except the current project) will be replaced.
     */
    void addAllProjects() {
        addConfigurationsRecursivelyFrom(project.rootProject)
    }

    /** Adds all of the configurations for a project.
     *
     * <p> If the project has been added previously, the existing list of configurations will be
     * overriden by this one.
     *
     * @param p Project for which all configuratiosn needs to be added. If it is the current project
     * and exception will be thrown.
     * @throw {@link CannotUseCurrentProjectException} if operation is attempted on the current subproject.
     */
    void addProject(final Project p) {
        if (p == project) {
            throw new CannotUseCurrentProjectException('The current project cannot be added in this way. Use configurations instead', p)
        } else {
            projectConfigurations[p] = []
        }
    }

    /** Adds all of the configurations for a project.
     *
     * <p> If the project has been added previously, the existing list of configurations will be
     * overriden by this one.
     *
     * @param s Project name for which all configurations needs to be added. If it is the current project
     * and exception will be thrown.
     * @throw {@link CannotUseCurrentProjectException} if operation is attempted on the current subproject.
     */
    void addProject(final String s) {
        addProject(project.findProject(s))
    }

    /* Adds one or more configurations from a given project.
     *
     * <p> If the project has been added previously, the existing list of configurations will be
     * overriden by this one.
     *
     * @param p Project for configurations needs to be added
     * @param config1 First configuration from project
     * @param configs Remainder of configurations from project
     * @throw {@link CannotUseCurrentProjectException} if operation is attempted on the current subproject.
     */

    void addProject(final Project p, Object config1, Object... configs) {
        if (p == project) {
            throw new CannotUseCurrentProjectException('The current project cannot be added in this way. Use configurations instead', p)
        } else {
            final List<Object> cfg = [config1]
            cfg.addAll(configs)
            projectConfigurations[p] = cfg
        }
    }

    /* Adds one or more configurations from a given project.
     *
     * <p> If the project has been added previously, the existing list of configurations will be
     * overriden by this one.
     *
     * @param p Project for configurations needs to be added
     * @param config1 First configuration from project
     * @param configs Remainder of configurations from project
     * @throw {@link CannotUseCurrentProjectException} if operation is attempted on the current subproject.
     */

    void addProject(final String s, Object config1, Object... configs) {
        addProject(project.findProject(s), config1, configs)
    }

    /** Adds remote repositories as per Gradle convention.
     *
     * @param repoConfigurator A closure that is suitable to delegate to a {@code RepositoryHandler}
     * object
     */
    void repositories(Closure repoConfigurator) {
        Closure configurator = (Closure) (repoConfigurator.clone())
        configurator.delegate = this.repositories
        configurator()
    }

    /** Access to the repositories that have been defined
     *
     * @return A repository handler that Gradle users should be accustomed to.
     */
    RepositoryHandler getRepositories() {
        this.repositories
    }

    /** Obtains a list of all the external module dependencies contained within
     * the provided configurations
     *
     * @return A set of all the dependencies
     */
    Set<Dependency> getDependencies() {
        final Set<Dependency> deps = []

        getConfigurations().each { Configuration cfg ->
            deps.addAll(getExternalModuleDependencies(cfg))
        }

        if (includeBuildScriptDependencies) {
            deps.addAll(getExternalModuleDependencies(
                    project.rootProject.buildscript.configurations.getByName('classpath')
            ))
        }

        deps
    }

    /** Returns the artifact pattern that will be used for storing dependendencies
     *
     * @return Artifact pattern or null in case repoRoot has not been set.
     */
    String getArtifactPattern() {
        repoRoot ? "${repoRoot}/${repoArtifactPattern}" : null
    }

    @Internal
    List<BinaryArtifactDependency> getBinaries() {
        List<DependencyHandlerExtension> handlers = [extBinaries]
        handlers.addAll(
            projectConfigurations.keySet().collect { Project p ->
                (DependencyHandlerExtension) (
                        (ExtensionAware) p.dependencies).extensions.findByName(BinaryPotBasePlugin.EXTENSION_NAME
                )
            }.findAll { it != null }
        )

        handlers.collectMany { DependencyHandlerExtension dhext ->
            dhext.asMap.values()
        } as List<BinaryArtifactDependency>
    }

    @TaskAction
    void exec() {
        File executionDataFile = new File(project.buildDir, "tmp/${toSafeFileName(name)}/execution.data")
        File ivySettingsFile = createIvySettingsFile()

        List<IvyDependency> ivyDeps = dependencies.collect {
            ivyDependency(it)
        }

        ExecutionData executionData = new ExecutionData()
        executionData.with {
            overwrite = project.gradle.startParameter.isRefreshDependencies()
            ivySettings = ivySettingsFile
            ivyRepoRoot = getRepoRoot()
            logLevel = AntLogLevel.fromGradleLogLevel(logging.level)
            dependencies.addAll(ivyDeps)
        }

        executionData.binaryRepoRoot = getBinaryRepoRoot()
        executionData.binaryRepositories.putAll(this.binaryRepositories.collectEntries { repo ->
            [repo.name, new BinaryRepositoryDescriptor(rootUri: repo.rootUri, artifactPattern: repo.artifactPattern)]
        })

        executionData.binaries.addAll(binaries.collect {
            binaryDependency(it)
        })

        ExecutionData.serializeData(executionDataFile, executionData)
        Configuration ivyRuntime = offlineRepositorySync.configuration
        File entryPointClasspath = resolveClassLocation(Downloader).file
        project.javaexec { JavaExecSpec jes ->
            jes.with {
                main = Downloader.canonicalName
                classpath ivyRuntime, entryPointClasspath
                args executionDataFile.absolutePath
            }
        }
    }

    private BinaryDependency binaryDependency(BinaryArtifactDependency dep) {
        new BinaryDependency(
                organisation: dep.group,
                module: dep.module,
                revision: dep.revision,
                transitive: false,
                typeFilter: dep.type,
                confFilter: '*',
                classifier: dep.classifier,
                extension: dep.extension
        )
    }

    private IvyDependency ivyDependency(Dependency dep) {
        new IvyDependency(
                organisation: dep.group,
                module: dep.name,
                revision: dep.version,
                transitive: dep instanceof ModuleDependency ? ((ModuleDependency) dep).transitive : true,
                typeFilter: '*',
                confFilter: '*'
        )
    }

    private OfflineRepositoryExtension getOfflineRepositorySync() {
        project.extensions.getByType(OfflineRepositoryExtension)
    }

    private File createIvySettingsFile() {
        File target = new File(temporaryDir, 'ivysettings.xml')
        IvyUtils.writeSettingsFile(
                target,
                repositories,
                getRepoRoot(),
                new File(project.buildDir, "tmp/ivypot/${FileUtils.toSafeFileName(name)}"),
                repoIvyPattern,
                repoArtifactPattern,
                repositoryCredentials
        )
        target
    }

    @CompileDynamic
    private List<Map<String, String>> getRepositoryCredentials() {
        this.repositories.findAll { repo ->
            repo.metaClass.respondsTo(repo, 'getRepositoryCredentials') && repo.credentials?.username && repo.credentials?.password
        }.collect { repo ->
            [
                    host    : repo.url.host,
                    username: repo.credentials.username,
                    password: repo.credentials.password
            ] as Map<String, String>
        }
    }

    private void addConfigurationsRecursivelyFrom(final Project p) {
        if (p != project) {
            projectConfigurations[p] = []
        }

        p.childProjects.each { final String name, final Project child ->
            addConfigurationsRecursivelyFrom(child)
        }
    }

    // Given a project and associated list of configs returns the config objects
    // if configs is null or empty all configurations will be returned.
    private Iterable<Configuration> getConfigurationsFor(final Project p, Iterable<Object> configs) {
        if (!configs || configs.size() < 1) {
            p.configurations
        } else {
            configs.collect { Object config ->
                if (config instanceof Configuration) {
                    (Configuration) config
                } else {
                    p.configurations.getByName(StringUtils.stringize(config))
                }
            }
        }
    }

    private Iterable<Dependency> getExternalModuleDependencies(final Configuration configuration) {
        configuration.allDependencies.findAll { Dependency dep ->
            dep instanceof ExternalModuleDependency
        }
    }

    final NamedDomainObjectContainer<BinaryRepository> binaryRepositories

    private Object repoRoot
    private Object binaryRepoRoot = { new File(getRepoRoot(), 'binaries') }
    private final RepositoryHandler repositories
    private final DependencyHandlerExtension extBinaries
    private final List<Object> configurations = []
    private final Map<Project, List<Object>> projectConfigurations = [:]
}

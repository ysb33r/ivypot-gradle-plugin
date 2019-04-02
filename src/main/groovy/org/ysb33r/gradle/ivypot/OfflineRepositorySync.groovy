//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2018
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
import groovy.transform.PackageScope
import org.apache.tools.ant.BuildListener
import org.apache.tools.ant.DefaultLogger
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.FileUtils
import org.gradle.util.GradleVersion
import org.ysb33r.gradle.ivypot.repositories.RepositoryHandler
import org.ysb33r.grolifant.api.StringUtils

@CompileStatic
class OfflineRepositorySync extends DefaultTask {

    @CompileDynamic
    OfflineRepositorySync() {
        String ivyJar = findIvyJarPath(project)
        ivyAnt = new AntBuilder()

        ivyAnt.taskdef name: "${name}Configure",
            classname: 'org.apache.ivy.ant.IvyConfigure',
            classpath: ivyJar

        ivyAnt.taskdef name: "${name}Resolve",
            classname: 'org.apache.ivy.ant.IvyResolve',
            classpath: ivyJar

        repositories = new RepositoryHandler(project)

        if (GradleVersion.current() < GradleVersion.version('4.0')) {
            inputs.properties.put('project configurations', { ->
                this.projectConfigurations
            })
        } else {
            inputs.property('project configurations', { OfflineRepositorySync ors ->
                Set<Configuration> configs = ors.getConfigurations()
                configs.collect { Configuration c ->
                    c.dependencies.collect { Dependency d ->
                        "${d.group}:${d.name}:${d.version}"
                    }.join(',')
                }.join('|')
            }.curry(this))
        }
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

    @TaskAction
    void exec() {

        boolean overwrite = project.gradle.startParameter.isRefreshDependencies()

        getRepoRoot().mkdirs()
        initIvyInstaller()
        setAntLogLevel()

        dependencies.each { Dependency dep ->
            ivyInstall(dep, overwrite)
        }

    }

    /**
     *
     * @param dep
     * @param overwrite
     *
     * @sa {@link https://ant.apache.org/ivy/history/trunk/use/resolve.html}
     */
    @PackageScope
    @CompileDynamic
    void ivyInstall(Dependency dep, boolean overwrite) {

        ivyAnt."${name}Resolve"(
            inline: true,
            organisation: dep.group, module: dep.name, revision: dep.version,
            transitive: dep instanceof ModuleDependency ? ((ModuleDependency) dep).transitive : true,
            type: '*',
            conf: '*'
        )
    }

    @PackageScope
    @CompileDynamic
    void initIvyInstaller() {
        ivyAnt."${name}Configure" file: createIvySettingsFile().absolutePath

    }

    @PackageScope
    File createIvySettingsFile() {
        def target = new File(temporaryDir, 'ivysettings.xml')
        target.text = ivyXml()
        target
    }


    /** Returns the XML required for ivysettings.xml.
     * @sa {@link https://github.com/apache/groovy/blob/master/src/resources/groovy/grape/defaultGrapeConfig.xml}
     */
    @PackageScope
    @CompileDynamic
    String ivyXml() {
        File cacheDir = project.gradle.startParameter.projectCacheDir ?: new File(project.buildDir, 'tmp')
        String xml = "<ivysettings><settings defaultResolver='${REMOTECHAINNAME}'/>"

        xml += "<caches defaultCacheDir='${repoRoot}' artifactPattern='${repoArtifactPattern}' ivyPattern='${repoIvyPattern}' " +
            "resolutionCacheDir='${cacheDir}/ivypot/${FileUtils.toSafeFileName(name)}'/>"

        this.repositories.each {
            if (it.metaClass.respondsTo(it, 'getCredentials')) {
                if (it.credentials.username && it.credentials.password) {
                    xml += "<credentials host='${it.url.host}' username='${it.credentials.username}' passwd='${it.credentials.password}' "
                    if (it.credentials.realm) {
                        xml += "realm='${it.credentials.realm}' "
                    }
                    xml += "/>"
                }
            }
        }

        xml += """<resolvers><chain name="${REMOTECHAINNAME}" returnFirst="true">"""

        this.repositories.each { xml += it.resolverXml() }

        xml += """</chain></resolvers></ivysettings>"""
    }

    @PackageScope
    void setAntLogLevel() {
        if (ivyAnt) {
            org.apache.tools.ant.Project localRef = ivyAnt.project
            LogLevel gradleLogLevel = project.logging.level
            ivyAnt.project.buildListeners.each { BuildListener it ->
                if (it instanceof DefaultLogger) {
                    DefaultLogger antLogger = ((DefaultLogger) it)
                    switch (gradleLogLevel) {
                        case null:
                        case gradleLogLevel.LIFECYCLE:
                            antLogger.messageOutputLevel = localRef.MSG_WARN
                            break
                        case gradleLogLevel.DEBUG:
                            antLogger.messageOutputLevel = localRef.MSG_DEBUG
                            break
                        case gradleLogLevel.QUIET:
                            antLogger.messageOutputLevel = localRef.MSG_ERR
                            break
                        case gradleLogLevel.INFO:
                            antLogger.messageOutputLevel = localRef.MSG_VERBOSE
                            break
                    }
                }
            }
        }
    }

    /** Returns the JAR path to be used for loading IVY.
     *
     * @param project
     * @return Returns the classpath (or null if the class is already available).
     */
    @CompileDynamic
    private static String findIvyJarPath(Project project) {
        if (DONT_LOOK_FOR_IVY_JAR) {
            return null
        } else {
            def files = new File(project.gradle.gradleHomeDir, 'lib/plugins').listFiles(new FilenameFilter() {
                @Override
                boolean accept(File dir, String name) {
                    name ==~ /ivy-\d.+.jar/
                }
            })

            if (!files?.size()) {
                throw new GradleException("Cannot locate an Ivy Ant jar in ${project.gradle.gradleHomeDir}/lib/plugins")
            }

            return files[0]
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

    private Object repoRoot
    private final RepositoryHandler repositories
    private final AntBuilder ivyAnt
    private final List<Object> configurations = []
    private final Map<Project, List<Object>> projectConfigurations = [:]

    private static final String LOCALREPONAME = '~~~local~~~repo~~~'
    private static final String REMOTECHAINNAME = '~~~remote~~~resolvers~~~'
    private static boolean DONT_LOOK_FOR_IVY_JAR = System.getProperty('DONT_LOOK_FOR_IVY_JAR')
}

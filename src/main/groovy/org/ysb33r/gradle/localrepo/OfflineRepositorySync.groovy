package org.ysb33r.gradle.localrepo

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import groovy.xml.NamespaceBuilder
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.util.CollectionUtils

@CompileStatic
class OfflineRepositorySync extends DefaultTask {

    static final String ARTIFACT_PATTERN = '[organisation]/[module]/[type]s/[artifact]-[revision].[ext]'
    static final String IVY_PATTERN  = '[organisation]/[module]/ivys/ivy-[revision].xml'

    @OutputDirectory
    @CompileDynamic
    File getRepoRoot() {
        project.file(this.repoRoot).absoluteFile
    }

    void repoRoot(Object repo) {
        this.repoRoot = repo
    }

    void setRepoRoot(Object repo) {
        this.repoRoot = repo
    }

    /** If no configuratiosn were listed, returns all the configurations
     *
     * @return a configuration container with all of the named configurations
     */
    ConfigurationContainer getConfigurations() {
        if(this.configurations) {
            List<String> names = CollectionUtils.stringize(this.configurations)
            project.configurations.matching { Configuration it ->
                names.contains(it.name)
            } as ConfigurationContainer
        } else {
            project.configurations
        }
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

    /** Adds remote repositories as per Gradle convention.
     *
     * @param repoConfigurator A closure that is suitable to delegate to a {@code RepositoryHandler}
     * object
     */
    @CompileDynamic
    void repositories(Closure repoConfigurator) {
        def configurator = repoConfigurator.clone()
        configurator.delegate = this.repositories
        configurator()
    }

    List<String> getRepositoryNames() {
        repositories.collect { ArtifactRepository it -> it.name }
    }

    /** Obtains a list of all the dependencies contained within
     * the provided configurations
     *
     * @return A set of all the dependencies
     */
    @CompileDynamic
    Set<Dependency> getDependencies() {
        Set<Dependency> deps = []

        getConfigurations().each { Configuration cfg ->
            cfg.allDependencies.all {
                deps.add(it)
            }
        }
        deps
    }

    @TaskAction
    void exec() {

        boolean overwrite = project.gradle.startParameter.isRefreshDependencies()

        getRepoRoot().mkdirs()
        initIvyInstaller()

        repositoryNames.each { String from ->
            dependencies.each { Dependency dep ->
                ivyInstall(dep,from,overwrite)
            }
        }
    }

    @PackageScope
    @CompileDynamic
    void ivyInstall( Dependency dep, final String fromRepo, boolean overwrite ) {
        ant.'ivy:install' (
            from: fromRepo, to: localRepoName,
            organisation: dep.group, module: dep.name, revision: dep.version,
            transitive:true, overwrite: overwrite
        )
    }

    @PackageScope
    @CompileDynamic
    def initIvyInstaller() {
        File ivySettings = createIvySettingsFile()
        ivyAnt = NamespaceBuilder.newInstance(new AntBuilder(),'antlib:org.apache.ivy.ant','ivy')
        ivyAnt.'ivy:configure' ( file : ivySettings.absolutePath )
        ivyAnt
    }

    @PackageScope
    File createIvySettingsFile() {
        def target= new File(temporaryDir,'ivysettings.xml')
        target.text = ivyXml()
        target
    }

    @PackageScope
    @CompileDynamic
    String ivyXml() {
        String xml= "<ivysettings><settings defaultResolver='${localRepoName}'/><resolvers>"

        this.repositories.each { xml+= it.resolverXml() }
        xml+="""
            <filesystem name="${localRepoName}">
                <ivy pattern="${repoRoot}/${IVY_PATTERN}"/>
                <artifact pattern="${repoRoot}/${ARTIFACT_PATTERN}"/>
            </filesystem></resolvers></ivysettings>"""

//        <bintray name="${defaultResolver}"/>
//        <filesystem name="${localRepoName}">
//            <ivy pattern="${repoRoot}/${IVY_PATTERN}"/>
//            <artifact pattern="${repoRoot}/${ARTIFACT_PATTERN}"/>
//        </filesystem>
    }

    @PackageScope
    final String localRepoName = '$$$local-repo$$$'

    @PackageScope
    def ivyAnt

    @PackageScope
    Object repoRoot

    @PackageScope
    List<Object> configurations = []

    @PackageScope
    RepositoryHandler repositories = new RepositoryHandler()

}

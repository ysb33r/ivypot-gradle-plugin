package org.ysb33r.gradle.ivypot

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import groovy.xml.NamespaceBuilder
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.reflect.DirectInstantiator
import org.gradle.util.CollectionUtils
import org.ysb33r.gradle.ivypot.internal.BaseRepositoryFactory

@CompileStatic
class OfflineRepositorySync extends DefaultTask {

    static final String ARTIFACT_PATTERN = '[organisation]/[module]/[type]s/[artifact]-[revision](-[classifier]).[ext]'
    static final String IVY_PATTERN  = '[organisation]/[module]/ivys/ivy-[revision].xml'
    private static final String LOCALREPONAME = '~~~local~~~repo~~~'
    private static final String REMOTECHAINNAME = '~~~remote~~~resolvers~~~'

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

    /** Access to the repositories that have been defined
     *
     * @return A repository handler that Gradle users hsould be accustomed to.
     */
    RepositoryHandler getRepositories() {
        this.repositories
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

    /** Returns the artifact pattern that will be used for storing dependendencies
     *
     * @return Artifact pattern or null in case repoRoot has not been set.
     */
    String getArtifactPattern() {
        repoRoot ? "${repoRoot}/${ARTIFACT_PATTERN}" : null
    }

    @TaskAction
    void exec() {

        boolean overwrite = project.gradle.startParameter.isRefreshDependencies()

        getRepoRoot().mkdirs()
        initIvyInstaller()

        dependencies.each { Dependency dep ->
            ivyInstall(dep,overwrite)
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
    void ivyInstall( Dependency dep, boolean overwrite ) {
//        ivyAnt.'ivy:install' (
//            from: REMOTECHAINNAME, to: LOCALREPONAME,
//            organisation: dep.group, module: dep.name, revision: dep.version,
//            transitive:true, overwrite: overwrite
//        )
        ivyAnt.'ivy:resolve' (
            inline : true,
            from: REMOTECHAINNAME, to: LOCALREPONAME,
            organisation: dep.group, module: dep.name, revision: dep.version,
            transitive:true, overwrite: overwrite
        )
    }

    @PackageScope
    @CompileDynamic
    void initIvyInstaller() {
        File ivySettings = createIvySettingsFile()
        ivyAnt = NamespaceBuilder.newInstance(new AntBuilder(),'antlib:org.apache.ivy.ant','ivy')
        ivyAnt.'ivy:configure' ( file : ivySettings.absolutePath )
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
        String xml= "<ivysettings><settings defaultResolver='${LOCALREPONAME}'/>"

        xml+= """<caches defaultCacheDir='${repoRoot}' artifactPattern='${ARTIFACT_PATTERN}' ivyPattern='${IVY_PATTERN}'/>"""

        this.repositories.each {
            if(it.metaClass.respondsTo(it,'getCredentials')) {
                if (it.credentials.username && it.credentials.password) {
                    xml+="<credentials host='${it.url.host}' username='${it.credentials.username}' passwd='${it.credentials.password}'/>"
                }
            }
        }

        xml+="""<resolvers>
            <filesystem name="${LOCALREPONAME}">
                <ivy pattern="${repoRoot}/${IVY_PATTERN}"/>
                <artifact pattern="${artifactPattern}"/>
            </filesystem><chain name="${REMOTECHAINNAME}" returnFirst="true">"""

        this.repositories.each { xml+= it.resolverXml() }

        xml+= """</chain></resolvers></ivysettings>"""

//        <bintray name="${defaultResolver}"/>
//        <filesystem name="${LOCALREPONAME}">
//            <ivy pattern="${repoRoot}/${IVY_PATTERN}"/>
//            <artifact pattern="${repoRoot}/${ARTIFACT_PATTERN}"/>
//        </filesystem>
    }
// https://github.com/apache/incubator-groovy/blob/master/src/resources/groovy/grape/defaultGrapeConfig.xml

    @PackageScope
    def ivyAnt

    @PackageScope
    Object repoRoot

    @PackageScope
    List<Object> configurations = []

    @PackageScope
    RepositoryHandler repositories = new DefaultRepositoryHandler( new BaseRepositoryFactory() ,new DirectInstantiator())

}

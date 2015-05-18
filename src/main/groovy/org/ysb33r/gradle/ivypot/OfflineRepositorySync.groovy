//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2015
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
import org.apache.ivy.Ivy
import org.apache.ivy.core.module.descriptor.ModuleDescriptor
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import groovy.xml.NamespaceBuilder
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.reflect.DirectInstantiator
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.CollectionUtils
import org.ysb33r.gradle.ivypot.internal.BaseRepositoryFactory

@CompileStatic
class OfflineRepositorySync extends DefaultTask {


    static final String ARTIFACT_PATTERN = IvyArtifactRepository.IVY_ARTIFACT_PATTERN
    static final String IVY_PATTERN  = IvyArtifactRepository.IVY_ARTIFACT_PATTERN
    private static final String LOCALREPONAME = '~~~local~~~repo~~~'
    private static final String REMOTECHAINNAME = '~~~remote~~~resolvers~~~'

    @CompileDynamic
    OfflineRepositorySync() {
        String ivyJar = findIvyJarPath(project)
        ivyAnt = new AntBuilder()

        ivyAnt.taskdef name: "${name}Configure",
            classname: 'org.apache.ivy.ant.IvyConfigure',
            classpath : ivyJar

        ivyAnt.taskdef name: "${name}Resolve",
            classname: 'org.apache.ivy.ant.IvyResolve',
            classpath : ivyJar

        repositories = createRepositoryHandler(project.gradle)
    }

    @Input
    boolean includeBuildScriptDependencies = false

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
     * @return A project configuration container with all of the named configurations. Does not
     * return the {@code buildscript} configuration in here. THe latter is made available directly to
     */
    @CompileDynamic
    ConfigurationContainer getConfigurations() {
        ConfigurationContainer cc
        if(this.configurations) {
            List<String> names = CollectionUtils.stringize(this.configurations)
            cc = project.configurations.matching { Configuration it ->
                names.contains(it.name)
            } as ConfigurationContainer
        } else {
            cc = project.configurations
        }

        return cc
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

        if(includeBuildScriptDependencies) {
            project.buildscript.configurations.classpath.allDependencies.all {
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
        ivyAnt."${name}Resolve" (
            inline : true,
            organisation: dep.group, module: dep.name, revision: dep.version,
            transitive:true
        )
    }

    @PackageScope
    @CompileDynamic
    void initIvyInstaller() {
        ivyAnt."${name}Configure" file : createIvySettingsFile().absolutePath

    }

    @PackageScope
    File createIvySettingsFile() {
        def target= new File(temporaryDir,'ivysettings.xml')
        target.text = ivyXml()
        target
    }


    /** Returns the XML required for ivysettings.xml.
    * @sa {@link https://github.com/apache/incubator-groovy/blob/master/src/resources/groovy/grape/defaultGrapeConfig.xml}
    */
    @PackageScope
    @CompileDynamic
    String ivyXml() {
        String xml= "<ivysettings><settings defaultResolver='${REMOTECHAINNAME}'/>"

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
     }

    private Object repoRoot
    private List<Object> configurations = []
    private RepositoryHandler repositories
    private AntBuilder ivyAnt

    /** Returns the JAR path to be used for loading IVY.
     *
     * @param project
     * @return Returns the classpath (or null if the class is already available).
     */
    @CompileDynamic
    private static String findIvyJarPath(Project project) {
        if(DONT_LOOK_FOR_IVY_JAR) {
            return null
        } else {
            // TODO: Check whether org.apache.ivy.ant.IvyConfigure is available,
            // If it is, then obtain the URL to where it can be found
            // otherwise continue as below

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

    @CompileDynamic
    private static RepositoryHandler createRepositoryHandler(Gradle gradle) {
        Instantiator instantiator

        // This handles this difference in the internal API between 2.3 & 2.4
        if (DirectInstantiator.metaClass.static.hasProperty('INSTANCE')) {
            instantiator - DirectInstantiator.INSTANCE
        } else {
            instantiator = new DirectInstantiator()
        }

        new DefaultRepositoryHandler(new BaseRepositoryFactory(), instantiator)
    }

    @PackageScope
    static boolean DONT_LOOK_FOR_IVY_JAR = false
}

package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.apache.tools.ant.BuildListener
import org.apache.tools.ant.DefaultLogger
import org.apache.tools.ant.Project

@CompileStatic
class IvyAnt {
    public static final String CONFIGURE_TASK = 'ivyConfigure'
    public static final String CONFIGURE_CLASS = 'org.apache.ivy.ant.IvyConfigure'
    public static final String RESOLVE_TASK = 'ivyResolve'
    public static final String RESOLVE_CLASS = 'org.apache.ivy.ant.IvyResolve'

    static void main(String[] args) {
        if (args.size() != 1) {
            throw new RuntimeException('No serialised location specified')
        }

        ExecutionData executionData = ExecutionData.deserializeData(new File(args[0]))
        def ivyAnt = new IvyAnt(executionData.ivySettings)
        ivyAnt.logLevel = executionData.logLevel
        ivyAnt.resolve(
                executionData.ivyRepoRoot,
                executionData.dependencies,
                executionData.overwrite
        )
    }

    IvyAnt(File ivySettings) {
        this.ivySettings = ivySettings
        ivyAnt = new AntBuilder()
        configureAnt()
        initialiseIvy()
    }

    void setLogLevel(int antLogLevel) {
        Project localRef = ivyAnt.project

        localRef.buildListeners.findAll { BuildListener it ->
            it instanceof DefaultLogger
        }.each {
            ((DefaultLogger) it).messageOutputLevel = antLogLevel
        }
    }

    void resolve(File repoRoot, List<IvyDependency> deps, boolean overwrite) {
        repoRoot.mkdirs()
        deps.each {
            ivyInstall(it, overwrite)
        }
    }

    @CompileDynamic
    private void initialiseIvy() {
        ivyAnt."${CONFIGURE_TASK}" file: ivySettings.absolutePath
    }

    /**
     *
     * @param dep
     * @param overwrite
     *
     * @sa {@link https://ant.apache.org/ivy/history/trunk/use/resolve.html}
     */
    @CompileDynamic
    private void ivyInstall(IvyDependency dep, boolean overwrite) {
        ivyAnt."${RESOLVE_TASK}"(
                inline: true,
                organisation: dep.organisation, module: dep.module, revision: dep.revision,
                transitive: dep.transitive,
                type: dep.typeFilter,
                conf: dep.confFilter
        )
    }

    @CompileDynamic
    private void configureAnt() {

        ivyAnt.taskdef name: CONFIGURE_TASK,
                classname: CONFIGURE_CLASS
//            classpath: ivyJar

        ivyAnt.taskdef name: RESOLVE_TASK,
                classname: RESOLVE_CLASS
//            classpath: ivyJar
    }

    private final AntBuilder ivyAnt
    private final File ivySettings
}

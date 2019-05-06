package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic

@CompileStatic
class ExecutionData implements Serializable {

    boolean overwrite
    int logLevel
    File ivySettings
    File ivyRepoRoot
    List<IvyDependency> dependencies = []

    static void serializeData(File destination, ExecutionData data) {
        destination.parentFile.mkdirs()
        destination.withOutputStream { out ->
            new ObjectOutputStream(out).withCloseable { oos ->
                oos.writeObject(data)
            }
        }
    }

    static ExecutionData deserializeData(File source) {
        ExecutionData ret
        source.withInputStream { input ->
            new ObjectInputStream(input).withCloseable { ois ->
                ret = (ExecutionData) ois.readObject()
            }
        }
        ret
    }
}

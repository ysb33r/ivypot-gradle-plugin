package org.ysb33r.gradle.ivypot.internal

import groovy.transform.CompileStatic
import org.gradle.api.logging.LogLevel

import static org.apache.tools.ant.Project.MSG_DEBUG
import static org.apache.tools.ant.Project.MSG_ERR
import static org.apache.tools.ant.Project.MSG_VERBOSE
import static org.apache.tools.ant.Project.MSG_WARN

@CompileStatic
class AntLogLevel {
    static int fromGradleLogLevel(LogLevel gradleLogLevel) {
        switch (gradleLogLevel) {
            case null:
            case gradleLogLevel.LIFECYCLE:
                MSG_WARN
                break
            case gradleLogLevel.DEBUG:
                MSG_DEBUG
                break
            case gradleLogLevel.QUIET:
                MSG_ERR
                break
            case gradleLogLevel.INFO:
                MSG_VERBOSE
                break
        }
    }
}

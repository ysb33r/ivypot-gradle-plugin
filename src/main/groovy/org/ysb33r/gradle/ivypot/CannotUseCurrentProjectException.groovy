package org.ysb33r.gradle.ivypot

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project

/** Exception that indicates that the operation cannot be performed on the current project.
 *
 * @since 0.6
 */
@CompileStatic
class CannotUseCurrentProjectException extends GradleException {
    CannotUseCurrentProjectException() {
        super('This operation is not valid for the the current project')
    }

    /**
     *
     * @param s Reason
     */
    CannotUseCurrentProjectException(final String s) {
        super(s)
    }

    /**
     * @param s Reason
     * @param p Project that operation was applied to
     */
    CannotUseCurrentProjectException(final String s,final Project p) {
        super("${s}: ${p.name}")
    }
}

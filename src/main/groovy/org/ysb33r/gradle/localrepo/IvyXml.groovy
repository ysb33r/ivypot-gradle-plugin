package org.ysb33r.gradle.localrepo

/**
 * @author Schalk W. Cronjé
 */
interface IvyXml {
    /** Returns a XML snippet suitable for including in the resolvers section
     *
     * @return
     */
    String resolverXml()
}
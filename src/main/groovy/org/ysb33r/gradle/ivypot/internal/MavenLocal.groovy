package org.ysb33r.gradle.ivypot.internal

/**
 * @author Schalk W. Cronjé
 */
class MavenLocal extends MavenRepository {

    MavenLocal() {
        super()
        url =  "file:${System.getProperty('user.home')}/.m2/repository/"
    }

    @Override
    String resolverXml() {
        """<ibiblio name="${name}" root="${url}" m2compatible="true" checkmodified="true" changingPattern=".*" changingMatcher="regexp"/>"""
    }
}

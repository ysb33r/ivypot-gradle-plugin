package org.ysb33r.gradle.ivypot.internal

import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

/**
 * @author Schalk W. Cronjé
 */
class BaseRepositoryFactory implements org.gradle.api.internal.artifacts.BaseRepositoryFactory {
    @Override
    MavenArtifactRepository createJCenterRepository() {
        new JCenter()
    }

    @Override
    MavenArtifactRepository createMavenCentralRepository() {
        new MavenCentral()
    }

    @Override
    MavenArtifactRepository createMavenRepository() {
        new MavenRepository()
    }

    @Override
    MavenArtifactRepository createMavenLocalRepository() {
        new MavenLocal()
    }

    @Override
    IvyArtifactRepository createIvyRepository() {
        return null
    }

    @Override
    FlatDirectoryArtifactRepository createFlatDirRepository() {
        return null
    }

}

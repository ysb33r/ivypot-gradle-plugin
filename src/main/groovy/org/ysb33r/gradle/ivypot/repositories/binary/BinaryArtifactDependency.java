package org.ysb33r.gradle.ivypot.repositories.binary;

public interface BinaryArtifactDependency {
    String getGroup();
    String getRevision();

    String getModule();

    String getClassifier();

    String getExtension();

    String getType();
}

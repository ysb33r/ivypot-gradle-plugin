package org.ysb33r.gradle.ivypot;

import groovy.xml.MarkupBuilder;

import java.io.StringWriter;

/**
 * @author Schalk W. Cronjé
 */
public interface IvyXml {
    /**
     * Returns a XML snippet suitable for including in the resolvers section
     *
     * @return Returns the XML that describes a specific section for Ivy settings.
     */
    default String resolverXml() {
        StringWriter writer = new StringWriter();
        MarkupBuilder builder = new MarkupBuilder(writer);
        writeTo(builder);
        return writer.toString();
    }

    void writeTo(MarkupBuilder builder);
}

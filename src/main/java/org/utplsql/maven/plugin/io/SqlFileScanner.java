package org.utplsql.maven.plugin.io;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

/**
 * Utility to scan all resources
 *
 * @author Alberto Hernández
 * @author Simon Martinelli
 */
public class SqlFileScanner {

    /**
     * Scans a directory looking for the matching patterns.
     *
     * @param baseDir            the base directory
     * @param resources          a list of resources
     * @param defaultDirectory   the default search directory
     * @param defaultFilePattern the default file pattern
     * @return a list of the files found
     */
    public List<String> findSqlScripts(File baseDir, List<Resource> resources, String defaultDirectory, String defaultFilePattern) throws MojoExecutionException {
        List<String> founds = new ArrayList<>();

        for (Resource resource : resources) {
            if (resource.getDirectory() == null) {
                resource.setDirectory(defaultDirectory);
            }

            if (resource.getIncludes().isEmpty()) {
                resource.getIncludes().add(defaultFilePattern);
            }

            DirectoryScanner scanner = buildScanner(baseDir.getPath(), resource);
            scanner.scan();

            for (String basename : scanner.getIncludedFiles()) {
                founds.add(baseDir.toURI().relativize(new File(scanner.getBasedir(), basename).toURI()).getPath());
            }
            founds.addAll(Collections.emptyList());
        }

        return founds;
    }

    private DirectoryScanner buildScanner(String baseDir, Resource resource) throws MojoExecutionException {
        File fileBaseDir = new File(baseDir, resource.getDirectory());
        if (!fileBaseDir.exists() || !fileBaseDir.isDirectory() || !fileBaseDir.canRead()) {
            throw new MojoExecutionException(format("Directory %s does not exist!", resource.getDirectory()));
        }

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(fileBaseDir.getPath());
        scanner.setIncludes(resource.getIncludes().toArray(new String[0]));
        scanner.setExcludes(resource.getExcludes().toArray(new String[0]));
        return scanner;
    }
}

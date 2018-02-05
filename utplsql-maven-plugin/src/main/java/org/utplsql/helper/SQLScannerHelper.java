package org.utplsql.helper;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Resource;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Utility to Scan all resources
 * 
 * @author Alberto Hernández
 *
 */
public class SQLScannerHelper {

    private SQLScannerHelper() {
        // NA
    }

    /**
     * 
     * @param resouces
     * @return
     */
    public static List<String> findSQLs(List<Resource> resources) {
        List<String> founds = new ArrayList<>();

        for (Resource resource : resources) {
            // Build Scanner
            DirectoryScanner scanner = buildScanner(resource);
            scanner.scan();
            for (String basename : scanner.getIncludedFiles()) {
                founds.add(new File(scanner.getBasedir(), basename).getAbsolutePath());
            }

            // Append all scanned objects
            founds.addAll(Arrays.asList());
        }

        return founds;
    }

    /**
     * Build a scanner in forder to Find all Resource files
     * 
     * @param resource
     * @return
     */
    private static DirectoryScanner buildScanner(Resource resource) {
        if (resource != null) {
            File baseDir = new File(resource.getDirectory());
            if (!baseDir.exists() || !baseDir.isDirectory() || !baseDir.canRead()) {
                throw new IllegalArgumentException(
                        format("Invalid <directory> %s in resource. Check your pom.xml", resource.getDirectory()));
            }

            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(resource.getDirectory());
            scanner.setIncludes(resource.getIncludes().toArray(new String[0]));
            scanner.setExcludes(resource.getExcludes().toArray(new String[0]));
            return scanner;
        }
        throw new IllegalArgumentException();
    }

}

package org.utplsql.maven.plugin.helper;

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
    public static List<String> findSQLs(File baseDir,List<Resource> resources, String defaultDirectory, String defaultFilePattern) {
        List<String> founds = new ArrayList<String>();

        for (Resource resource : resources) {
        	if (resource.getDirectory() == null) {
        		// use default directory if not set.
        		resource.setDirectory(defaultDirectory);
        	}
        	
        	if (resource.getIncludes().isEmpty()) {
        		// use default file pattern if not set.
        		resource.getIncludes().add(defaultFilePattern);
        	}
            // Build Scanner
            DirectoryScanner scanner = buildScanner(baseDir.getPath(),resource);
            scanner.scan();
            for (String basename : scanner.getIncludedFiles()) {
                founds.add(baseDir.toURI().relativize(new File(scanner.getBasedir(), basename).toURI()).getPath());
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
    private static DirectoryScanner buildScanner(String baseDir,Resource resource) {
        if (resource != null) {
            File fileBaseDir = new File(baseDir,resource.getDirectory());
            if (!fileBaseDir.exists() || !fileBaseDir.isDirectory() || !fileBaseDir.canRead()) {
                throw new IllegalArgumentException(
                        format("Invalid <directory> %s in resource. Check your pom.xml", resource.getDirectory()));
            }

            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(fileBaseDir.getPath());
            scanner.setIncludes(resource.getIncludes().toArray(new String[0]));
            scanner.setExcludes(resource.getExcludes().toArray(new String[0]));
            return scanner;
        }
        throw new IllegalArgumentException();
    }

}

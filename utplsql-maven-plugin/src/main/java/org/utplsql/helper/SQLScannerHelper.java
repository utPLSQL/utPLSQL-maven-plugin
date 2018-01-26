package org.utplsql.helper;

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
public class SQLScannerHelper 
{
	
	/**
	 * 
	 * @param resouces
	 * @return
	 */
	public static List<String> findSQLs(List<Resource> resources)
	{
		List<String> founds = new ArrayList<String>();
		
		for (Resource resource: resources)
		{
			// Build Scanner
			DirectoryScanner scanner = buildScanner(resource);
			scanner.scan();
			
			// Append all scanned objects
			founds.addAll(Arrays.asList(scanner.getIncludedFiles()));
		}
		
		
		return founds;
	}

	/**
	 * Build a scanner in forder to Find all Resource files
	 * @param resource
	 * @return
	 */
	private static DirectoryScanner buildScanner(Resource resource) 
	{
		DirectoryScanner scanner = new DirectoryScanner();
		
		scanner.setBasedir(resource.getDirectory());
		scanner.setIncludes(resource.getIncludes().toArray(new String[0]));
		scanner.setExcludes(resource.getExcludes().toArray(new String[0]));
		
		
		return scanner;
	}

}

package org.utplsql.maven.plugin.helper;

import java.util.Arrays;

import org.apache.maven.model.Resource;

/**
 * This class provides methods to retrieve the list of resources in the default <source> and <test> directories.
 * 
 * @author Alberto Hernández
 *
 */
public class PluginDefault
{
	// Source Directory
	public static final String SOURCE_DIRECTORY = "src/main/plsql";

	// Test Directory
	public static final String TEST_DIRECTORY = "src/test/plsql";
	
	/**
	 * Default source file pattern.
	 */
	public static final String SOURCE_FILE_PATTERN = "**/*.*";
	
	/**
	 * Default test file pattern.
	 */
	public static final String TEST_FILE_PATTERN = "**/*.pkg";

	private PluginDefault()
	{
		// NA
	}

	/**
	 * This method returns {@link Resource} for the default {@code source} directory
	 * 
	 * @return a {@link Resource}
	 */
	public static Resource buildDefaultSource()
	{
		return buildDirectory(SOURCE_DIRECTORY, SOURCE_FILE_PATTERN);
	}

	/**
	 * This method returns {@link Resource} for the default {@code test} directory
	 * 
	 * @return a {@link Resource}
	 */
	public static Resource buildDefaultTest()
	{
		return buildDirectory(TEST_DIRECTORY, TEST_FILE_PATTERN);
	}

	private static Resource buildDirectory(String directory, String includes)
	{
		Resource resource = new Resource();
		resource.setDirectory(directory);
		resource.setIncludes(Arrays.asList(includes));
		return resource;
	}
}

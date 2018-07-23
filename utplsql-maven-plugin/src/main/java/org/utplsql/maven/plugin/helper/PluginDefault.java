package org.utplsql.maven.plugin.helper;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.apache.maven.model.Resource;

/**
 * This class provides methods to retrieve the list of resources in the default <source> and <test> directories.
 * 
 * @author Alberto Hernández
 *
 */
public class PluginDefault
{

	private static final String STYLE_COLOR_PROPERTY = "style.color";

	private static final String BATCH_MODE = "B";

	private static final String LOG_FILE = "l";

	// Source Directory
	public static final String SOURCE_DIRECTORY = "src/main/plsql";

	// Test Directory
	public static final String TEST_DIRECTORY = "src/test/plsql";

	private PluginDefault()
	{
		// NA
	}

	/**
	 * This method returns {@link Resource} for the default {@code source} directory
	 * 
	 * @return a {@link Resource}
	 */
	public static Resource buildDefaultSource(File baseDir)
	{
		return buildDirectory(baseDir,SOURCE_DIRECTORY, "**/*.*");
	}

	/**
	 * This method returns {@link Resource} for the default {@code test} directory
	 * 
	 * @return a {@link Resource}
	 */
	public static Resource buildDefaultTest(File baseDir)
	{
		return buildDirectory(baseDir, TEST_DIRECTORY, "**/*.pkg");
	}

	private static Resource buildDirectory(File baseDir, String directory, String includes)
	{
		Resource resource = new Resource();
		resource.setDirectory(baseDir.getAbsolutePath()+"/"+directory);
		resource.setIncludes(Arrays.asList(includes));
		return resource;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean resolveColor()
	{
		final Map<String, String> env = System.getenv();
		String color = env.get(STYLE_COLOR_PROPERTY);

		if ("always".equals(color))
		{
			return true;
		}

		if ("never".equals(color))
		{
			return false;
		}

		if (env.containsKey(BATCH_MODE) || env.containsKey(LOG_FILE))
		{
			return false;
		}

		return false;
	}

}

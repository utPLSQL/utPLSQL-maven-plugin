package org.utplsql;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.TestRunner;
import org.utplsql.api.Version;
import org.utplsql.api.exception.SomeTestsFailedException;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.helper.PluginDefault;
import org.utplsql.helper.SQLScannerHelper;
import org.utplsql.model.ReporterParameter;
import org.utplsql.reporter.ReporterWriter;

/**
 * This class expose the {@link TestRunner} interface to Maven.
 * 
 * @author Alberto Hernández
 *
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class UtPLSQLMojo extends AbstractMojo
{
	@Parameter(defaultValue = "${dbUrl}")
	private String url;

	@Parameter(defaultValue = "${dbUser}")
	private String user;

	@Parameter(defaultValue = "${dbPass}")
	private String password;
	
	@Parameter(defaultValue = "3.1.0")
	private String version;

	@Parameter
	private String includeObject;

	@Parameter
	private String excludeObject;

	@Parameter(defaultValue = "false")
	private boolean skipCompatibilityCheck;

	@Parameter
	private List<ReporterParameter> reporters;

	@Parameter(defaultValue = "")
	private List<String> paths;

	@Parameter
	private List<Resource> sources = new ArrayList<>();

	@Parameter
	private List<Resource> tests = new ArrayList<>();

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private String targetDir;

	@Parameter(defaultValue = "${maven.test.failure.ignore}")
	private boolean ignoreFailure;

	// Color in the console, loaded by environment variables
	private boolean colorConsole = PluginDefault.resolveColor();

	// Reporter Writer
	private ReporterWriter reporterWriter;

	/**
	 * 
	 * Execute the plugin
	 * 
	 */
	@Override
	public void execute() throws MojoExecutionException
	{
		Connection connection = null;
		try 
		{
			FileMapperOptions sourceMappingOptions = buildOptions(sources, PluginDefault.buildDefaultSource(), "sources");
			FileMapperOptions testMappingOptions = buildOptions(tests, PluginDefault.buildDefaultTest(), "test");
			
			// Create the Connection to the Database
			connection = DriverManager.getConnection(url, user, password);
			
			List<Reporter> reporterList = initReporters(connection);

			logParameters(sourceMappingOptions, testMappingOptions, reporterList);

			TestRunner runner = new TestRunner()
					.addPathList(paths)
					.addReporterList(reporterList)
					.sourceMappingOptions(sourceMappingOptions)
					.testMappingOptions(testMappingOptions)
					.skipCompatibilityCheck(skipCompatibilityCheck)
					.colorConsole(colorConsole)
					.failOnErrors(!ignoreFailure);

			// Setting Optional Parameters
			if (StringUtils.isNotBlank(excludeObject))
			{
				runner.excludeObject(excludeObject);
			}
			if (StringUtils.isNotBlank(includeObject))
			{
				runner.includeObject(includeObject);
			}

			runner.run(connection);
			
		}
		catch (SomeTestsFailedException e)
		{
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage());
		}
		catch (SQLException e)
		{
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				// Write Reporters
				reporterWriter.writeReporters(connection);
			}
			catch (Exception e)
			{
				getLog().error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * @param resources
	 * @return
	 * @throws MojoExecutionException
	 */
	private FileMapperOptions buildOptions(List<Resource> resources, Resource defaultResource, String msg) throws MojoExecutionException
	{
		try
		{
			// Check if this element is empty
			if (resources.isEmpty())
			{
				resources.add(defaultResource);
			}

			List<String> scripts = SQLScannerHelper.findSQLs(resources);
			return new FileMapperOptions(scripts);

		}
		catch (Exception e)
		{
			throw new MojoExecutionException(format("Invalid <%s> in your pom.xml: %s", msg, e.getMessage()));
		}
	}

	/**
	 * Init all the reporters
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private List<Reporter> initReporters(Connection connection) throws SQLException
	{
		List<Reporter> reporterList = new ArrayList<>();
		
		Version utlVersion = new Version (version);

		// Initialized Reporters
		reporterWriter = new ReporterWriter(targetDir, utlVersion);
		
		ReporterFactory reporterFactory = ReporterFactory.createEmpty();

		for (ReporterParameter reporterParameter : reporters)
		{
			Reporter reporter = reporterFactory.createReporter(reporterParameter.getName());
			reporter.init(connection);
			reporterList.add(reporter);

			// Only added the reporter if at least one of the output is required
			if (StringUtils.isNotBlank(reporterParameter.getFileOutput()) || reporterParameter.isConsoleOutput())
			{
				reporterWriter.addReporter(reporterParameter, reporter);
			}
		}

		return reporterList;
	}

	/**
	 * 
	 * @param sourceMappingOptions
	 * @param testMappingOptions
	 * @param reporterList
	 */
	private void logParameters(FileMapperOptions sourceMappingOptions, FileMapperOptions testMappingOptions,
			List<Reporter> reporterList)
	{
		Log log = getLog();
		log.info("Invoking TestRunner with: " + targetDir);

		// Do nothing when the debug is disabled
		if (!log.isDebugEnabled())
		{
			return;
		}

		log.debug("Invoking TestRunner with: ");
		log.debug("reporters=");
		reporterList.forEach((Reporter r) -> log.debug(r.getTypeName()));
		log.debug("sources=");
		sourceMappingOptions.getFilePaths().forEach(log::debug);
		log.debug("tests=");
		testMappingOptions.getFilePaths().forEach(log::debug);
	}
}
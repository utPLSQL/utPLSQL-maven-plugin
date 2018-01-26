package org.utplsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.TestRunner;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.helper.PluginDefault;
import org.utplsql.helper.SQLScannerHelper;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class UtPLSQLMojo extends AbstractMojo
{

	@Parameter(required = true)
	private String url;

	@Parameter(required = true)
	private String user;

	@Parameter(required = true)
	private String password;

	@Parameter
	private boolean failOnErrors;

	@Parameter
	private boolean colorConsole;

	@Parameter
	private List<String> reporters;

	// Configuration of Sources, Testing

	@Parameter
	private List<Resource> sources = new ArrayList<Resource>();

	@Parameter
	private List<Resource> tests = new ArrayList<Resource>();

	/**
	 * 
	 * Execute the plugin
	 * 
	 */
	@Override
	public void execute() throws MojoExecutionException
	{
		List<String> testPaths = Arrays.asList(new String[] { "tests" });

		FileMapperOptions sourceMappingOptions = buildOptions(sources, PluginDefault.buildDefaultSource());
		FileMapperOptions testMappingOptions = buildOptions(tests, PluginDefault.buildDefaultTest());

		Connection connection = null;
		List<Reporter> reporterList = null;

		try
		{
			connection = DriverManager.getConnection(url, user, password);

			// Init Reporters
			reporterList = initReporters(connection);

			TestRunner runner = new TestRunner()
					.addPathList(testPaths)
					.addReporterList(reporterList)
					.sourceMappingOptions(sourceMappingOptions)
					.testMappingOptions(testMappingOptions)
					.colorConsole(colorConsole)
					.failOnErrors(failOnErrors);

			runner.run(connection);

		}
		catch (Exception e)
		{
			getLog().error(e);
			throw new MojoExecutionException("Unexpected error executing plugin " + e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (SQLException e)
			{
				getLog().error("Error", e);
			}
		}
	}

	/**
	 * 
	 * @param resources
	 * @return
	 */
	private FileMapperOptions buildOptions(List<Resource> resources, Resource defaultResource)
	{
		// Check if this element is empty
		if (resources.isEmpty())
		{
			resources.add(defaultResource);
		}

		List<String> scripts = SQLScannerHelper.findSQLs(resources);
		return new FileMapperOptions(scripts);
	}

	/**
	 * Init all the reports
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private List<Reporter> initReporters(Connection connection) throws SQLException
	{
		List<Reporter> reporterList = new ArrayList<Reporter>();

		for (String reporterId : reporters)
		{
			Reporter reporter = ReporterFactory.createReporter(reporterId);
			reporter.init(connection);

			reporterList.add(reporter);
		}

		return reporterList;
	}
}
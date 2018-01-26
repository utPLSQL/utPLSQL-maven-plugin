package org.utplsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.OutputBuffer;
import org.utplsql.api.TestRunner;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.helper.SQLScannerHelper;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class UtPLSQLMojo extends AbstractMojo
{
    @Parameter(defaultValue = "jdbc:oracle:thin:@localhost:1521:ut3")
	private String url;

    @Parameter(defaultValue = "ut3")
	private String user;

    @Parameter(defaultValue = "XNtxj8eEgA6X6b6f")
	private String password;

    // This parameter should honor the -Dmaven.test.failure.ignore=false
    @Parameter(defaultValue = "false")
	private boolean failOnErrors;

    @Parameter(defaultValue = "false")
	private boolean colorConsole;

    @Parameter(defaultValue = "UT_DOCUMENTATION_REPORTER")
	private List<String> reporters;

    @Parameter
    private List<String> paths;

    @Parameter
	private List<Resource> sources = new ArrayList<>();

    @Parameter
	private List<Resource> tests = new ArrayList<>();

	/**
	 * 
	 * Execute the plugin
	 * 
	 */
	@Override
	public void execute() throws MojoExecutionException
	{
        // List<String> testPaths = Arrays.asList(new String[] { "tests" });

        FileMapperOptions sourceMappingOptions = new FileMapperOptions(new ArrayList<>());
        FileMapperOptions testMappingOptions = new FileMapperOptions(new ArrayList<>()); // buildOptions(tests,
                                                                                         // PluginDefault.buildDefaultTest());

		Connection connection = null;
		List<Reporter> reporterList = null;

		try
		{
			connection = DriverManager.getConnection(url, user, password);

			// Init Reporters
			reporterList = initReporters(connection);
            if (getLog().isDebugEnabled()) {
                StringBuilder msg = new StringBuilder();
                msg.append("Invoking TestRunner with: ").append('\n');
                msg.append("reporters=");
                reporterList.forEach(new Consumer<Reporter>() {
                    @Override
                    public void accept(Reporter t) {
                        try {
                            msg.append(t.getSQLTypeName()).append(", ");
                        } catch (Exception e) {
                            // NA
                        }
                    }
                    
                });
                msg.append('\n');
                msg.append("sources=");
                sourceMappingOptions.getFilePaths().forEach(new Consumer<String>() {
                    @Override
                    public void accept(String t) {
                        msg.append(t).append(", ");
                    }
                });
                msg.append('\n');
                msg.append("tests=");
                testMappingOptions.getFilePaths().forEach(new Consumer<String>() {
                    @Override
                    public void accept(String t) {
                        msg.append(t).append(", ");
                    }
                });
                msg.append('\n');
                getLog().debug(msg.toString());
            }

			TestRunner runner = new TestRunner()
                    .addPathList(paths)
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
                for (Reporter reporter : reporterList) {
                    new OutputBuffer(reporter).printAvailable(connection, System.out);
                }
                if (connection != null) {
					connection.close();
                }
			}
            catch (Exception e)
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
		List<Reporter> reporterList = new ArrayList<>();

		for (String reporterId : reporters)
		{
			Reporter reporter = ReporterFactory.createReporter(reporterId);
			reporter.init(connection);

			reporterList.add(reporter);
		}

		return reporterList;
	}
}
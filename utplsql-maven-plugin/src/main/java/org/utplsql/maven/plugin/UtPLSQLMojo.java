package org.utplsql.maven.plugin;

import java.io.File;
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
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.utplsql.api.DBHelper;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.JavaApiVersionInfo;
import org.utplsql.api.KeyValuePair;
import org.utplsql.api.TestRunner;
import org.utplsql.api.Version;
import org.utplsql.api.exception.SomeTestsFailedException;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.maven.plugin.helper.PluginDefault;
import org.utplsql.maven.plugin.helper.SQLScannerHelper;
import org.utplsql.maven.plugin.model.ReporterParameter;
import org.utplsql.maven.plugin.reporter.ReporterWriter;

/**
 * This class expose the {@link TestRunner} interface to Maven.
 * 
 * @author Alberto Hernández
 *
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class UtPLSQLMojo extends AbstractMojo {
	
	@Parameter(readonly = true, defaultValue = "${project}")
	private MavenProject project;
	
	@Parameter(property = "dbUrl")
	protected String url;

	@Parameter(property = "dbUser")
	protected String user;

	@Parameter(property = "dbPass")
	protected String password;

	@Parameter
	protected String includeObject;

	@Parameter
	protected String excludeObject;

	@Parameter(defaultValue = "false")
	protected boolean skipCompatibilityCheck;

	@Parameter
	protected List<ReporterParameter> reporters = new ArrayList<>();

	@Parameter
	protected List<String> paths = new ArrayList<>();

	// Sources Configuration
	@Parameter
	protected List<Resource> sources = new ArrayList<>();

	@Parameter
	private String sourcesOwner;

	@Parameter
	private String sourcesRegexExpression;

	@Parameter
	private Integer sourcesOwnerSubexpression;

	@Parameter
	private Integer sourcesNameSubexpression;

	@Parameter
	private Integer sourcesTypeSubexpression;

	@Parameter
	private List<CustomTypeMapping> sourcesCustomTypeMapping;

	// Tests Configuration

	@Parameter
	protected List<Resource> tests = new ArrayList<>();

	@Parameter
	private String testsOwner;

	@Parameter
	private String testsRegexExpression;

	@Parameter
	private Integer testsOwnerSubexpression;

	@Parameter
	private Integer testsNameSubexpression;

	@Parameter
	private Integer testsTypeSubexpression;

	@Parameter
	private List<CustomTypeMapping> testsCustomTypeMapping;

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	protected String targetDir;

	@Parameter(defaultValue = "${maven.test.failure.ignore}")
	protected boolean ignoreFailure;

	// Color in the console, bases on maven logging configuration
	private boolean colorConsole = MessageUtils.isColorEnabled();

	// Reporter Writer
	private ReporterWriter reporterWriter;

	/**
	 * 
	 * Execute the plugin
	 * 
	 */
	@Override
	public void execute() throws MojoExecutionException {
		getLog().debug("Java Api Version = " + JavaApiVersionInfo.getVersion());
		loadConfFromEnvironment();

		Connection connection = null;
		try {
			FileMapperOptions sourceMappingOptions = buildSourcesOptions();
			FileMapperOptions testMappingOptions = buildTestsOptions();

			// Create the Connection to the Database
			connection = DriverManager.getConnection(url, user, password);
			getLog().info("utPLSQL Version = " + DBHelper.getDatabaseFrameworkVersion(connection));

			List<Reporter> reporterList = initReporters(connection);

			logParameters(sourceMappingOptions, testMappingOptions, reporterList);

			TestRunner runner = new TestRunner().addPathList(paths).addReporterList(reporterList)
					.sourceMappingOptions(sourceMappingOptions).testMappingOptions(testMappingOptions)
					.skipCompatibilityCheck(skipCompatibilityCheck).colorConsole(colorConsole)
					.failOnErrors(!ignoreFailure);

			// Setting Optional Parameters
			if (StringUtils.isNotBlank(excludeObject)) {
				runner.excludeObject(excludeObject);
			}
			if (StringUtils.isNotBlank(includeObject)) {
				runner.includeObject(includeObject);
			}

			runner.run(connection);

		} catch (SomeTestsFailedException e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage());
		} catch (SQLException e) {
			getLog().error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			try {
				// Write Reporters
				if (connection != null)
					reporterWriter.writeReporters(connection);
			} catch (Exception e) {
				getLog().error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Load some configuration from env variables.
	 * 
	 */
	private void loadConfFromEnvironment() {
		if (StringUtils.isEmpty(url)) {
			url = System.getProperty("dbUrl");
		}

		if (StringUtils.isEmpty(user)) {
			user = System.getProperty("dbUser");
		}

		if (StringUtils.isEmpty(password)) {
			password = System.getProperty("dbPass");
		}
	}

	/**
	 * 
	 * @param resources
	 * @return
	 * @throws MojoExecutionException
	 */
	private FileMapperOptions buildSourcesOptions() throws MojoExecutionException {
		try {
			// Check if this element is empty
			if (sources.isEmpty()) {
				File defaultSourceDirectory = new File(project.getBasedir(),PluginDefault.SOURCE_DIRECTORY);
				if (defaultSourceDirectory.exists()) {
					sources.add(PluginDefault.buildDefaultSource());
				} else {
					return new FileMapperOptions(new ArrayList<String>());
				}
			}

			List<String> scripts = SQLScannerHelper.findSQLs(project.getBasedir(),sources,PluginDefault.SOURCE_DIRECTORY, PluginDefault.SOURCE_FILE_PATTERN);
			FileMapperOptions fileMapperOptions = new FileMapperOptions(scripts);

			if (StringUtils.isNotEmpty(sourcesOwner)) {
				fileMapperOptions.setObjectOwner(sourcesOwner);
			}

			if (StringUtils.isNotEmpty(sourcesRegexExpression)) {
				fileMapperOptions.setRegexPattern(sourcesRegexExpression);
			}

			if (sourcesOwnerSubexpression != null) {
				fileMapperOptions.setOwnerSubExpression(sourcesOwnerSubexpression);
			}

			if (sourcesNameSubexpression != null) {
				fileMapperOptions.setNameSubExpression(sourcesNameSubexpression);
			}

			if (sourcesTypeSubexpression != null) {
				fileMapperOptions.setTypeSubExpression(sourcesTypeSubexpression);
			}

			if (sourcesCustomTypeMapping != null && !sourcesCustomTypeMapping.isEmpty()) {
				fileMapperOptions.setTypeMappings(new ArrayList<KeyValuePair>());
				for (CustomTypeMapping typeMapping : sourcesCustomTypeMapping) {
					fileMapperOptions.getTypeMappings()
							.add(new KeyValuePair(typeMapping.getCustomMapping(), typeMapping.getType()));
				}
			}

			return fileMapperOptions;

		} catch (Exception e) {
			throw new MojoExecutionException("Invalid <SOURCES> in your pom.xml",e);
		}

	}

	/**
	 * 
	 * @param resources
	 * @return
	 * @throws MojoExecutionException
	 */
	private FileMapperOptions buildTestsOptions() throws MojoExecutionException {
		try {
			// Check if this element is empty
		    if (tests.isEmpty()) {
                File defaultTestDirectory = new File(project.getBasedir(),PluginDefault.TEST_DIRECTORY);
                if (defaultTestDirectory.exists()) {
                    tests.add(PluginDefault.buildDefaultTest());
                } else {
                    return new FileMapperOptions(new ArrayList<String>());
                }
            }

			List<String> scripts = SQLScannerHelper.findSQLs(project.getBasedir(),tests,PluginDefault.TEST_DIRECTORY, PluginDefault.TEST_FILE_PATTERN);
			FileMapperOptions fileMapperOptions = new FileMapperOptions(scripts);

			if (StringUtils.isNotEmpty(testsOwner)) {
				fileMapperOptions.setObjectOwner(testsOwner);
			}

			if (StringUtils.isNotEmpty(testsRegexExpression)) {
				fileMapperOptions.setRegexPattern(testsRegexExpression);
			}

			if (testsOwnerSubexpression != null) {
				fileMapperOptions.setOwnerSubExpression(testsOwnerSubexpression);
			}

			if (testsNameSubexpression != null) {
				fileMapperOptions.setNameSubExpression(testsNameSubexpression);
			}

			if (testsTypeSubexpression != null) {
				fileMapperOptions.setTypeSubExpression(testsTypeSubexpression);
			}

			if (testsCustomTypeMapping != null && !testsCustomTypeMapping.isEmpty()) {
				fileMapperOptions.setTypeMappings(new ArrayList<KeyValuePair>());
				for (CustomTypeMapping typeMapping : testsCustomTypeMapping) {
					fileMapperOptions.getTypeMappings()
							.add(new KeyValuePair(typeMapping.getCustomMapping(), typeMapping.getType()));
				}
			}

			return fileMapperOptions;

		} catch (Exception e) {
			throw new MojoExecutionException("Invalid <TESTS> in your pom.xml: " + e.getMessage());
		}

	}

	/**
	 * Init all the reporters
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private List<Reporter> initReporters(Connection connection) throws SQLException {
		List<Reporter> reporterList = new ArrayList<>();

		Version utlVersion = DBHelper.getDatabaseFrameworkVersion(connection);

		// Initialized Reporters
		reporterWriter = new ReporterWriter(targetDir, utlVersion);

		ReporterFactory reporterFactory = ReporterFactory.createEmpty();

		for (ReporterParameter reporterParameter : reporters) {
			Reporter reporter = reporterFactory.createReporter(reporterParameter.getName());
			reporter.init(connection);
			reporterList.add(reporter);
			
			// Turns the console output on by default if both file and console output are empty.
			if (!reporterParameter.isFileOutput() && null == reporterParameter.getConsoleOutput()) {
			    reporterParameter.setConsoleOutput(true);
			}

			// Only added the reporter if at least one of the output is required
			if (StringUtils.isNotBlank(reporterParameter.getFileOutput()) || reporterParameter.isConsoleOutput()) {
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
			List<Reporter> reporterList) {
		Log log = getLog();
		log.info("Invoking TestRunner with: " + targetDir);

		// Do nothing when the debug is disabled
		if (!log.isDebugEnabled()) {
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
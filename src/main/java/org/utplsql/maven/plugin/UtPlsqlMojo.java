package org.utplsql.maven.plugin;

import oracle.jdbc.pool.OracleDataSource;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
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
import org.utplsql.api.db.DefaultDatabaseInformation;
import org.utplsql.api.exception.SomeTestsFailedException;
import org.utplsql.api.reporter.CoreReporters;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.maven.plugin.io.ReportWriter;
import org.utplsql.maven.plugin.io.SqlFileScanner;
import org.utplsql.maven.plugin.model.CustomTypeMapping;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.utplsql.maven.plugin.util.StringUtil.isEmpty;
import static org.utplsql.maven.plugin.util.StringUtil.isNotBlank;
import static org.utplsql.maven.plugin.util.StringUtil.isNotEmpty;

/**
 * This class expose the {@link TestRunner} interface to Maven.
 *
 * @author Alberto Hernández
 * @author Simon Martinelli
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class UtPlsqlMojo extends AbstractMojo {

    @Parameter(readonly = true, defaultValue = "${project}")
    MavenProject project;

    @Parameter(property = "dbUrl")
    String url;
    @Parameter(property = "dbUser")
    String user;
    @Parameter(property = "dbPass")
    String password;

    @Parameter
    String includeObject;
    @Parameter
    String excludeObject;

    @Parameter(defaultValue = "false")
    boolean skipCompatibilityCheck;

    @Parameter
    final List<ReporterParameter> reporters = new ArrayList<>();

    @Parameter
    final List<String> paths = new ArrayList<>();

    @Parameter
    final List<Resource> sources = new ArrayList<>();
    @Parameter
    String sourcesOwner;
    @Parameter
    String sourcesRegexExpression;
    @Parameter
    Integer sourcesOwnerSubexpression;
    @Parameter
    Integer sourcesNameSubexpression;
    @Parameter
    Integer sourcesTypeSubexpression;
    @Parameter
    List<CustomTypeMapping> sourcesCustomTypeMapping;

    @Parameter
    final List<Resource> tests = new ArrayList<>();
    @Parameter
    String testsOwner;
    @Parameter
    String testsRegexExpression;
    @Parameter
    Integer testsOwnerSubexpression;
    @Parameter
    Integer testsNameSubexpression;
    @Parameter
    Integer testsTypeSubexpression;
    @Parameter
    List<CustomTypeMapping> testsCustomTypeMapping;

    @Parameter
    final Set<String> tags = new LinkedHashSet<>();

    @Parameter
    boolean randomTestOrder;

    @Parameter
    Integer randomTestOrderSeed;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    String targetDir;

    @Parameter(defaultValue = "${maven.test.failure.ignore}")
    boolean ignoreFailure;

    @Parameter(property = "skipUtplsqlTests", defaultValue = "false")
    boolean skipUtplsqlTests;

    @Parameter
    boolean dbmsOutput;

    private final SqlFileScanner sqlFileScanner = new SqlFileScanner();

    @Override
    public void execute() throws MojoExecutionException {
        if (skipUtplsqlTests) {
            getLog().info("utPLSQLTests are skipped.");
        } else {
            getLog().debug("Java API Version = " + JavaApiVersionInfo.getVersion());

            Connection connection = null;
            ReportWriter reportWriter = null;
            try {
                connection = createConnection();

                Version utlVersion = new DefaultDatabaseInformation().getUtPlsqlFrameworkVersion(connection);
                getLog().info("utPLSQL Version = " + utlVersion);

                FileMapperOptions sourceMappingOptions = buildSourcesOptions();
                FileMapperOptions testMappingOptions = buildTestsOptions();

                reportWriter = new ReportWriter(targetDir, utlVersion, getLog());
                List<Reporter> reporterList = initReporters(connection, reportWriter, ReporterFactory.createEmpty());

                logParameters(sourceMappingOptions, testMappingOptions, reporterList);

                TestRunner runner = new TestRunner()
                        .addPathList(paths)
                        .addReporterList(reporterList)
                        .sourceMappingOptions(sourceMappingOptions)
                        .testMappingOptions(testMappingOptions)
                        .skipCompatibilityCheck(skipCompatibilityCheck)
                        .colorConsole(MessageUtils.isColorEnabled())
                        .addTags(tags)
                        .randomTestOrder(randomTestOrder)
                        .randomTestOrderSeed(randomTestOrderSeed)
                        .failOnErrors(!ignoreFailure);

                if (isNotBlank(excludeObject)) {
                    if (excludeObject.contains(",")) {
                        String[] excludes = excludeObject.split(",");
                        runner.excludeObjects(Arrays.asList(excludes));
                    } else {
                        runner.excludeObject(excludeObject);
                    }

                }
                if (isNotBlank(includeObject)) {
                    if (includeObject.contains(",")) {
                        String[] includes = includeObject.split(",");
                        runner.includeObjects(Arrays.asList(includes));
                    } else {
                        runner.includeObject(includeObject);
                    }
                }

                runner.run(connection);

            } catch (SomeTestsFailedException e) {
                if (!ignoreFailure) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }
            } catch (SQLException | IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            } finally {
                try {
                    if (connection != null) {
                        if (reportWriter != null) {
                            reportWriter.writeReports(connection);
                        }
                        DBHelper.disableDBMSOutput(connection);
                        connection.close();
                    }
                } catch (SQLException | IOException e) {
                    getLog().error(e.getMessage(), e);
                }
            }
        }
    }

    private Connection createConnection() throws SQLException {
        if (isEmpty(url)) {
            url = System.getProperty("dbUrl");
        }
        if (isEmpty(user)) {
            user = System.getProperty("dbUser");
        }
        if (isEmpty(password)) {
            password = System.getProperty("dbPass");
        }

        OracleDataSource ds = new OracleDataSource();
        ds.setURL(url);
        ds.setUser(user);
        ds.setPassword(password);

        Connection connection = ds.getConnection();
        if (dbmsOutput) {
            DBHelper.enableDBMSOutput(connection);
            getLog().info("Enabled dbms_output.");
        }
        return connection;
    }

    FileMapperOptions buildSourcesOptions() throws IOException {
        if (sources.isEmpty()) {
            File defaultSourceDirectory = new File(project.getBasedir(), Defaults.SOURCE_DIRECTORY);
            if (defaultSourceDirectory.exists()) {
                sources.add(Defaults.buildDefaultSource());
            } else {
                return new FileMapperOptions(new ArrayList<>());
            }
        }

        List<String> scripts = sqlFileScanner.findSqlScripts(project.getBasedir(), sources,
                Defaults.SOURCE_DIRECTORY, Defaults.SOURCE_FILE_PATTERN);
        return createFileMapperOptions(scripts, sourcesOwner, sourcesRegexExpression, sourcesOwnerSubexpression,
                sourcesNameSubexpression, sourcesTypeSubexpression, sourcesCustomTypeMapping);
    }

    FileMapperOptions buildTestsOptions() throws IOException {
        if (tests.isEmpty()) {
            File defaultTestDirectory = new File(project.getBasedir(), Defaults.TEST_DIRECTORY);
            if (defaultTestDirectory.exists()) {
                tests.add(Defaults.buildDefaultTest());
            } else {
                return new FileMapperOptions(new ArrayList<>());
            }
        }

        List<String> scripts = sqlFileScanner.findSqlScripts(project.getBasedir(), tests, Defaults.TEST_DIRECTORY,
                Defaults.TEST_FILE_PATTERN);
        return createFileMapperOptions(scripts, testsOwner, testsRegexExpression, testsOwnerSubexpression,
                testsNameSubexpression, testsTypeSubexpression, testsCustomTypeMapping);
    }

    private FileMapperOptions createFileMapperOptions(List<String> scripts, String objectOwner, String regexPattern,
                                                      Integer ownerSubExpression, Integer nameSubExpression,
                                                      Integer typeSubExpression, List<CustomTypeMapping> typeMappings) {
        FileMapperOptions fileMapperOptions = new FileMapperOptions(scripts);

        if (isNotEmpty(objectOwner)) {
            fileMapperOptions.setObjectOwner(objectOwner);
        }
        if (isNotEmpty(regexPattern)) {
            fileMapperOptions.setRegexPattern(regexPattern);
        }
        if (ownerSubExpression != null) {
            fileMapperOptions.setOwnerSubExpression(ownerSubExpression);
        }
        if (nameSubExpression != null) {
            fileMapperOptions.setNameSubExpression(nameSubExpression);
        }
        if (typeSubExpression != null) {
            fileMapperOptions.setTypeSubExpression(typeSubExpression);
        }
        if (typeMappings != null && !typeMappings.isEmpty()) {
            fileMapperOptions.setTypeMappings(typeMappings.stream()
                    .map(mapping -> new KeyValuePair(mapping.getCustomMapping(), mapping.getType()))
                    .collect(Collectors.toList()));
        }
        return fileMapperOptions;
    }

    List<Reporter> initReporters(Connection connection, ReportWriter reportWriter, ReporterFactory reporterFactory) throws SQLException {
        List<Reporter> reporterList = new ArrayList<>();
        if (reporters.isEmpty()) {
            getLog().debug("No reporters configured using default");

            ReporterParameter reporterParameter = new ReporterParameter();
            reporterParameter.setConsoleOutput(true);
            reporterParameter.setName(CoreReporters.UT_DOCUMENTATION_REPORTER.name());
            reporters.add(reporterParameter);
        }
        for (ReporterParameter reporterParameter : reporters) {
            Reporter reporter = reporterFactory.createReporter(reporterParameter.getName());
            reporter.init(connection);
            reporterList.add(reporter);

            // Turns the console output on by default if both file and console output are empty.
            if (!reporterParameter.isFileOutput() && reporterParameter.getConsoleOutput() == null) {
                reporterParameter.setConsoleOutput(true);
            }

            // Only added the reporter if at least one of the output is required
            if (isNotBlank(reporterParameter.getFileOutput()) || reporterParameter.isConsoleOutput()) {
                reportWriter.addReporter(reporterParameter, reporter);
            }
        }
        return reporterList;
    }

    private void logParameters(FileMapperOptions sourceMappingOptions, FileMapperOptions testMappingOptions, List<Reporter> reporterList) {
        getLog().info("Invoking TestRunner with: " + targetDir);

        if (getLog().isDebugEnabled()) {
            getLog().debug("Invoking TestRunner with: ");

            getLog().debug("reporters=");
            reporterList.forEach((Reporter r) -> getLog().debug(r.getTypeName()));

            getLog().debug("sources=");
            sourceMappingOptions.getFilePaths().forEach(getLog()::debug);

            getLog().debug("tests=");
            testMappingOptions.getFilePaths().forEach(getLog()::debug);
        }
    }

    /**
     * This class provides methods to retrieve the list of resources in the default {@literal <source> and <test>} directories.
     *
     * @author Alberto Hernández
     * @author Simon Martinelli
     */
    private static class Defaults {

        public static final String SOURCE_DIRECTORY = "src/main/plsql";
        public static final String TEST_DIRECTORY = "src/test/plsql";
        public static final String SOURCE_FILE_PATTERN = "**/*.*";
        public static final String TEST_FILE_PATTERN = "**/*.pkg";

        private Defaults() {
        }

        /**
         * This method returns {@link Resource} for the default {@code source} directory
         *
         * @return a {@link Resource}
         */
        public static Resource buildDefaultSource() {
            return buildDirectory(SOURCE_DIRECTORY, SOURCE_FILE_PATTERN);
        }

        /**
         * This method returns {@link Resource} for the default {@code test} directory
         *
         * @return a {@link Resource}
         */
        public static Resource buildDefaultTest() {
            return buildDirectory(TEST_DIRECTORY, TEST_FILE_PATTERN);
        }

        private static Resource buildDirectory(String directory, String includes) {
            Resource resource = new Resource();
            resource.setDirectory(directory);
            resource.setIncludes(Collections.singletonList(includes));
            return resource;
        }
    }
}

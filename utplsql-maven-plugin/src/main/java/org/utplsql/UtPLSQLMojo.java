package org.utplsql;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.OutputBuffer;
import org.utplsql.api.TestRunner;
import org.utplsql.api.exception.SomeTestsFailedException;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.helper.PluginDefault;
import org.utplsql.helper.SQLScannerHelper;
import org.utplsql.model.ReporterConfiguration;
import org.utplsql.model.ReporterParameter;

/**
 * This class expose the {@link TestRunner} interface to Maven.
 * 
 * @author Alberto Hernández
 *
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class UtPLSQLMojo extends AbstractMojo {
    @Parameter(defaultValue = "jdbc:oracle:thin:@localhost:1521:ut3")
    private String url;

    @Parameter(defaultValue = "ut3")
    private String user;

    @Parameter(defaultValue = "XNtxj8eEgA6X6b6f")
    private String password;

    @Parameter
    private String includeObject;

    @Parameter
    private String excludeObject;

    @Parameter(defaultValue = "${maven.test.failure.ignore}")
    private boolean ignoreFailure;

    @Parameter(defaultValue = "false")
    private boolean colorConsole;

    @Parameter(defaultValue = "false")
    private boolean skipCompatibilityCheck;

    @Parameter
    private List<ReporterParameter> reporters;
    private Map<String, ReporterParameter> mapReporters = new HashMap<>();

    @Parameter(defaultValue = "")
    private List<String> paths;

    @Parameter
    private List<Resource> sources = new ArrayList<>();

    @Parameter
    private List<Resource> tests = new ArrayList<>();

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private String targetDir;

    /**
     * 
     * Execute the plugin
     * 
     */
    @Override
    public void execute() throws MojoExecutionException {
        FileMapperOptions sourceMappingOptions = null;
        try {
            sourceMappingOptions = buildOptions(sources, PluginDefault.buildDefaultSource());
        } catch (Exception e) {
            throw new MojoExecutionException(format("Invalid <sources> in your pom.xml: %s", e.getMessage()));
        }
        FileMapperOptions testMappingOptions = null;
        try {
            testMappingOptions = buildOptions(tests, PluginDefault.buildDefaultTest());
        } catch (Exception e) {
            throw new MojoExecutionException(format("Invalid <tests> in your pom.xml: %s", e.getMessage()));
        }

        Connection connection = null;
        List<Reporter> reporterList = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            reporterList = initReporters(connection);

            if (getLog().isDebugEnabled()) {
                dumpParameters(sourceMappingOptions, testMappingOptions, reporterList);
            }

            TestRunner runner = new TestRunner()
                    .addPathList(paths)
                    .addReporterList(reporterList)
                    .sourceMappingOptions(sourceMappingOptions)
                    // .excludeObject(excludeObject)
                    // .includeObject(includeObject)
                    .testMappingOptions(testMappingOptions)
                    .skipCompatibilityCheck(skipCompatibilityCheck)
                    .colorConsole(colorConsole)
                    .failOnErrors(!ignoreFailure);

            runner.run(connection);

        } catch (SomeTestsFailedException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage());
        } catch (SQLException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            try {
                for (Reporter reporter : reporterList) {
                    writeReporter(connection, reporter);
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                getLog().error(e.getMessage(), e);
            }
        }
    }

    private void writeReporter(Connection connection, Reporter reporter) throws SQLException, IOException {
        String outputFile = mapReporters.get(reporter.getReporterId()).getConfiguration().getOutputFile();
        if (StringUtils.isNotBlank(outputFile) && !StringUtils.equals("-", outputFile)) {
            File file = new File(outputFile);
            if (!file.isAbsolute()) {
                file = new File(targetDir, outputFile);
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fout = new FileOutputStream(file)) {

                getLog().info(format("Writing report %s to %s", reporter.getSelfType(), file.getAbsolutePath()));
                new OutputBuffer(reporter).printAvailable(connection, new PrintStream(fout));
            }
        } else {
            getLog().info(format("Reporter %s:", reporter.getSelfType()));
            new OutputBuffer(reporter).printAvailable(connection, System.out);
        }
    }

    /**
     * 
     * @param resources
     * @return
     */
    private FileMapperOptions buildOptions(List<Resource> resources, Resource defaultResource) {
        // Check if this element is empty
        if (resources.isEmpty()) {
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
    private List<Reporter> initReporters(Connection connection) throws SQLException {
        List<Reporter> reporterList = new ArrayList<>();

        for (ReporterParameter reporterParameter : reporters) {
            String reporterName = reporterParameter.getId().name();
            Reporter reporter = ReporterFactory.createReporter(reporterName);
            reporter.init(connection);
            reporterList.add(reporter);
            if (reporterParameter.getConfiguration() == null
                    || StringUtils.isEmpty(reporterParameter.getConfiguration().getOutputFile())) {
                reporterParameter
                        .setConfiguration(new ReporterConfiguration(reporterParameter.getId().getOutputFile()));
            }
            mapReporters.put(reporter.getReporterId(), reporterParameter);
        }

        return reporterList;
    }

    private void dumpParameters(FileMapperOptions sourceMappingOptions, FileMapperOptions testMappingOptions,
            List<Reporter> reporterList) {
        Log log = getLog();
        log.debug("Invoking TestRunner with: ");
        log.debug("reporters=");
        reporterList.forEach((Reporter r) -> log.debug(r.getSelfType()));
        log.debug("sources=");
        sourceMappingOptions.getFilePaths().forEach(log::debug);
        log.debug("tests=");
        testMappingOptions.getFilePaths().forEach(log::debug);
    }
}
package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.utplsql.api.reporter.CoreReporters;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtPlsqlMojoTest {

    private UtPlsqlMojo createUtPlsqlMojo() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        UtPlsqlMojo utPlsqlMojo = new UtPlsqlMojo();
        utPlsqlMojo.project = new MavenProject();
        utPlsqlMojo.targetDir = "target";

        utPlsqlMojo.url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
        utPlsqlMojo.user = "UT3";
        utPlsqlMojo.password = "UT3";

        return utPlsqlMojo;
    }

    @Test
    public void junitReporter() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setFileOutput("junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPlsqlMojo.reporters.add(junitReporter);

        utPlsqlMojo.execute();

        assertTrue(new File("target/junit-report.xml").exists());
    }

    @Test
    public void absolutPath() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);

        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            junitReporter.setFileOutput("c:/tmp/junit-report.xml");
        } else {
            junitReporter.setFileOutput("/tmp/junit-report.xml");
        }
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPlsqlMojo.reporters.add(junitReporter);

        utPlsqlMojo.execute();
        if (os.contains("Windows")) {
            assertTrue(new File("c:/tmp/junit-report.xml").exists());
        } else {
            assertTrue(new File("/tmp/junit-report.xml").exists());
        }
    }

    @Test
    public void parentDoesNotExist() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setFileOutput("not-exist/junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPlsqlMojo.reporters.add(junitReporter);

        utPlsqlMojo.execute();

        assertTrue(new File("target/not-exist/junit-report.xml").exists());
    }

    @Test
    public void onlyConsoleOutput() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPlsqlMojo.reporters.add(junitReporter);

        utPlsqlMojo.execute();

        assertTrue(new File("target/not-exist/junit-report.xml").exists());
    }

    @Test
    public void onlyFileOutput() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(false);
        junitReporter.setFileOutput("not-exist/junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPlsqlMojo.reporters.add(junitReporter);

        utPlsqlMojo.execute();

        assertTrue(new File("target/not-exist/junit-report.xml").exists());
    }

    @Test
    public void skipUtplsqlTests() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        utPlsqlMojo.skipUtplsqlTests = true;

        final ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        utPlsqlMojo.execute();

        String standardOutput = console.toString();

        assertTrue(standardOutput.contains("utPLSQLTests are skipped."));
    }

    @Test
    public void defaultReportAndExcludes() throws MojoExecutionException {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo();

        utPlsqlMojo.excludeObject = "abc";
        utPlsqlMojo.includeObject = "xyz";

        utPlsqlMojo.execute();
    }

}

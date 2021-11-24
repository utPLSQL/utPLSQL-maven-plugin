package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.utplsql.api.reporter.CoreReporters;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class UtPlsqlMojoTest {

    private static UtPlsqlMojo utPLSQLMojo;

    @BeforeClass
    public static void setUp() {
        utPLSQLMojo = new UtPlsqlMojo();
        utPLSQLMojo.project = new MavenProject();
        utPLSQLMojo.targetDir = "target";

        utPLSQLMojo.url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
        utPLSQLMojo.user = "UT3";
        utPLSQLMojo.password = "UT3";
    }

    @Test
    public void junitReporter() throws MojoExecutionException {
        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setFileOutput("junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPLSQLMojo.reporters.add(junitReporter);

        utPLSQLMojo.execute();

        assertTrue(new File("target/junit-report.xml").exists());
    }

    @Test
    public void absolutPath() throws MojoExecutionException {
        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);

        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            junitReporter.setFileOutput("c:/tmp/junit-report.xml");
        } else {
            junitReporter.setFileOutput("/tmp/junit-report.xml");
        }
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPLSQLMojo.reporters.add(junitReporter);

        utPLSQLMojo.execute();
        if (os.contains("Windows")) {
            assertTrue(new File("c:/tmp/junit-report.xml").exists());
        } else {
            assertTrue(new File("/tmp/junit-report.xml").exists());
        }
    }

    @Test
    public void parentDoesNotExist() throws MojoExecutionException {
        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setFileOutput("not-exist/junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPLSQLMojo.reporters.add(junitReporter);

        utPLSQLMojo.execute();

        assertTrue(new File("target/not-exist/junit-report.xml").exists());
    }

    @Test
    public void onlyConsoleOutput() throws MojoExecutionException {
        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPLSQLMojo.reporters.add(junitReporter);

        utPLSQLMojo.execute();

        assertTrue(new File("target/not-exist/junit-report.xml").exists());
    }

    @Test
    public void onlyFileOutput() throws MojoExecutionException {
        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(false);
        junitReporter.setFileOutput("not-exist/junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPLSQLMojo.reporters.add(junitReporter);

        utPLSQLMojo.execute();

        assertTrue(new File("target/not-exist/junit-report.xml").exists());
    }

    @Test
    public void skipUtplsqlTests() throws MojoExecutionException {
        utPLSQLMojo.skipUtplsqlTests = true;

        final ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        utPLSQLMojo.execute();

        String standardOutput = console.toString();

        assertTrue(standardOutput.contains("utPLSQLTests are skipped."));
    }

    @Test
    public void defaultReport() throws MojoExecutionException {
        final ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        utPLSQLMojo.execute();

        String standardOutput = console.toString();

        assertTrue(standardOutput.contains("Finished"));
    }

}

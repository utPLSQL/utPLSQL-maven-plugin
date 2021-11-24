package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.utplsql.api.reporter.CoreReporters;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class UtPLSQLMojoTest {

    private static UtPLSQLMojo utPLSQLMojo;

    @BeforeClass
    public static void setUp() {
        utPLSQLMojo = new UtPLSQLMojo();
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
}

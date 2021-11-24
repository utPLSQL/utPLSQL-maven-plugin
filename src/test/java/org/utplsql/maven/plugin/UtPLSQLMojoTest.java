package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.utplsql.api.reporter.CoreReporters;
import org.utplsql.maven.plugin.model.ReporterParameter;

public class UtPLSQLMojoTest {

    @Test
    public void execute() throws MojoExecutionException {
        UtPLSQLMojo utPLSQLMojo = new UtPLSQLMojo();
        utPLSQLMojo.project = new MavenProject();
        utPLSQLMojo.targetDir = "target";

        utPLSQLMojo.url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
        utPLSQLMojo.user = "UT3";
        utPLSQLMojo.password = "UT3";

        ReporterParameter junitReporter = new ReporterParameter();
        junitReporter.setConsoleOutput(true);
        junitReporter.setFileOutput("junit-report.xml");
        junitReporter.setName(CoreReporters.UT_JUNIT_REPORTER.name());
        utPLSQLMojo.reporters.add(junitReporter);

        utPLSQLMojo.execute();
    }
}

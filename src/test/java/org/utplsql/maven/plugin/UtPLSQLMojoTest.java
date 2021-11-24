package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public class UtPLSQLMojoTest {

    @Test
    public void execute() throws MojoExecutionException {
        UtPLSQLMojo utPLSQLMojo = new UtPLSQLMojo();
        utPLSQLMojo.project = new MavenProject();
        utPLSQLMojo.url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
        utPLSQLMojo.user = "UT3";
        utPLSQLMojo.password = "UT3";

        utPLSQLMojo.execute();
    }
}

package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Mock;

public class UtPlsqlMojoIT {

    @Test
    public void execute() throws MojoExecutionException {
        UtPlsqlMojo utPLSQLMojo = new UtPlsqlMojo();
        utPLSQLMojo.project = new MavenProject();
        utPLSQLMojo.url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
        utPLSQLMojo.user = "UT3";
        utPLSQLMojo.password = "UT3";

        utPLSQLMojo.execute();
    }
}

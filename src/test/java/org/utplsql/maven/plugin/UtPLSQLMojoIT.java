package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public class UtPLSQLMojoIT {

    @Test
    public void execute() throws MojoExecutionException {
        UtPLSQLMojo utPlsqlMojo = new UtPLSQLMojo();
        utPlsqlMojo.project = new MavenProject();
        utPlsqlMojo.url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";
        utPlsqlMojo.user = "UT3";
        utPlsqlMojo.password = "UT3";

        utPlsqlMojo.execute();
    }
}

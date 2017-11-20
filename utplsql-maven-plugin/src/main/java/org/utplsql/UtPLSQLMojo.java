package org.utplsql;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.OutputBuffer;
import org.utplsql.api.TestRunner;
import org.utplsql.api.exception.SomeTestsFailedException;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.utplsql.api.CustomTypes.UT_DOCUMENTATION_REPORTER;

@Mojo(name = "test")
public class UtPLSQLMojo extends AbstractMojo {


    /**
     * List of paths with test suites.
     */
    @Parameter(name = "suitePaths", required = true, property = "utplsql.suite_paths")
    private List<String> suitePaths;

    /**
     * List of paths with source files.
     */
    @Parameter(name = "sourcePaths", required = true, property = "utplsql.source_paths")
    private List<String> sourcePaths;

    /**
     * List of paths with test files.
     */
    @Parameter(name = "testPaths", required = true, property = "utplsql.test_paths")
    private List<String> testPaths;

    public void execute() throws MojoExecutionException, MojoFailureException {

        List<Reporter> reporterList = new ArrayList<>();
        reporterList.add(ReporterFactory.createReporter(UT_DOCUMENTATION_REPORTER));
        List<String> testPaths = listOfStrings("tests");
        FileMapperOptions sourceMappingOptions = new FileMapperOptions(listOfStrings(
                "source/award_bonus/award_bonus.prc"
//                "/Users/kamil.berdychowski/workspaces/utPLSQL/utPLSQL-demo-project/source/between_string/betwnstr.fnc"
        ));
        FileMapperOptions testMappingOptions = new FileMapperOptions(listOfStrings(
                "/Users/kamil.berdychowski/workspaces/utPLSQL/utPLSQL-demo-project/test/award_bonus/test_award_bonus.pkb",
                "/Users/kamil.berdychowski/workspaces/utPLSQL/utPLSQL-demo-project/test/award_bonus/test_award_bonus.pks"
//                "/Users/kamil.berdychowski/workspaces/utPLSQL/utPLSQL-demo-project/test/between_string/test_betwnstr.pkb",
//                "/Users/kamil.berdychowski/workspaces/utPLSQL/utPLSQL-demo-project/test/between_string/test_betwnstr.pks"
        ));

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/XE", "tests", "tests");

            for (Reporter reporter : reporterList) {
                reporter.init(connection);
            }

            new TestRunner()
                    .addPathList(testPaths)
                    .addReporterList(reporterList)
                    .sourceMappingOptions(sourceMappingOptions)
                    .testMappingOptions(testMappingOptions)
                    .colorConsole(true)
                    .failOnErrors(true)
                    .run(connection);
        } catch (SomeTestsFailedException e) {
            getLog().error(e);
            throw new MojoExecutionException("Tests failed", e);
        } catch (SQLException e) {
            getLog().error(e);
            throw new MojoFailureException("SQL error occured", e);
        } finally {
            //TODO: add more security checking, what happens when connection is broken
            try {
                new OutputBuffer(reporterList.get(0)).printAvailable(connection, System.out);
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                getLog().error("Error", e);
            }
        }

    }

    private List<String> listOfStrings(String... values) {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, values);
        return result;
    }
}
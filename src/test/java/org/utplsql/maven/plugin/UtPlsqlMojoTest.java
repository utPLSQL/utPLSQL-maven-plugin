package org.utplsql.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.*;

public class UtPlsqlMojoTest {

    @Rule
    public final MojoRule rule = new MojoRule();

    /**
     * Invalid Sources Directory
     * <p>
     * Given : a pom.xml with invalid sources' directory
     * When : pom is read and buildSourcesOptions is run
     * Then : it should throw a MojoExecutionException
     */
    @Test
    public void invalid_sources_directory() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("invalid_sources_directory");
        assertNotNull(utPlsqlMojo);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, utPlsqlMojo::execute);

        assertEquals("Directory foo does not exist!", exception.getMessage());
    }

    /**
     * Invalid Tests Directory
     * <p>
     * Given : a pom.xml with invalid tests' directory
     * When : pom is read and buildTestsOptions is run
     * Then : it should throw a MojoExecutionException
     */
    @Test
    public void invalid_tests_directory() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("invalid_tests_directory");
        assertNotNull(utPlsqlMojo);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, utPlsqlMojo::execute);

        assertEquals("Directory bar does not exist!", exception.getMessage());
    }

    /**
     * Sources Tests Parameters
     * <p>
     * Given : a pom.xml with sources and tests with a lot of parameters
     * When : pom is read and buildSourcesOptions / buildTestsOptions are run
     * Then : it should fill all parameters correctly
     */
    @Test
    public void sources_tests_parameters() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("sources_tests_parameters");
        assertNotNull(utPlsqlMojo);

        assertEquals(2, utPlsqlMojo.reporters.size());

        // check sources
        FileMapperOptions sources = utPlsqlMojo.buildSourcesOptions();
        assertEquals(1, sources.getFilePaths().size());
        assertEquals("srcs/foo.sql", sources.getFilePaths().get(0));
        assertEquals("code_owner", sources.getObjectOwner());
        assertEquals(".*/\\w+/(\\w+)/(\\w+)\\.\\w{3}", sources.getRegexPattern());
        assertEquals(Integer.valueOf(9), sources.getNameSubExpression());
        assertEquals(Integer.valueOf(1), sources.getTypeSubExpression());
        assertEquals(Integer.valueOf(4), sources.getOwnerSubExpression());
        assertEquals(1, sources.getTypeMappings().size());
        assertEquals("bar", sources.getTypeMappings().get(0).getKey());
        assertEquals("foo", sources.getTypeMappings().get(0).getValue());

        // check tests
        FileMapperOptions tests = utPlsqlMojo.buildTestsOptions();
        assertEquals(2, tests.getFilePaths().size());
        assertTrue(tests.getFilePaths().contains("te/st/file.pkb"));
        assertTrue(tests.getFilePaths().contains("te/st/spec.pks"));
        assertEquals("tests_owner", tests.getObjectOwner());
        assertEquals(".*/\\w+/(\\w+)/(\\w+)\\.\\w{3}", tests.getRegexPattern());
        assertEquals(Integer.valueOf(54), tests.getNameSubExpression());
        assertEquals(Integer.valueOf(21), tests.getTypeSubExpression());
        assertEquals(Integer.valueOf(24), tests.getOwnerSubExpression());
        assertEquals(1, tests.getTypeMappings().size());
        assertEquals("def", tests.getTypeMappings().get(0).getKey());
        assertEquals("abc", tests.getTypeMappings().get(0).getValue());
    }

    /**
     * Sources and Tests Parameter does not exist
     * <p>
     * Given : a pom.xml with no sources / tests tags and default directory does not exist.
     * When : pom is read and buildSourcesOptions / buildTestsOptions are run
     * Then : it should not find any source files
     */
    @Test
    public void sources_and_tests_parameter_does_not_exist() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("sources_and_tests_parameter_does_not_exist");
        assertNotNull(utPlsqlMojo);

        // check sources
        FileMapperOptions sources = utPlsqlMojo.buildSourcesOptions();
        assertEquals(0, sources.getFilePaths().size());

        // check tests
        FileMapperOptions tests = utPlsqlMojo.buildTestsOptions();
        assertEquals(0, tests.getFilePaths().size());
    }

    /**
     * Sources and Tests Parameter does not exist but Default Directory exists
     * <p>
     * Given : a pom.xml with no sources / tests tags but default directory exists.
     * When : pom is read and buildSourcesOptions / buildTestsOptions are run
     * Then : it should find all sources/tests files in default directories
     */
    @Test
    public void sources_and_tests_parameter_does_not_exist_but_default_directory_exists() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("sources_and_tests_parameter_does_not_exist_but_default_directory_exists");
        assertNotNull(utPlsqlMojo);

        // check sources
        FileMapperOptions sources = utPlsqlMojo.buildSourcesOptions();
        assertEquals(2, sources.getFilePaths().size());
        assertTrue(sources.getFilePaths().contains("src/main/plsql/f1.sql"));
        assertTrue(sources.getFilePaths().contains("src/main/plsql/f2.sql"));

        // check tests
        FileMapperOptions tests = utPlsqlMojo.buildTestsOptions();
        assertEquals(2, tests.getFilePaths().size());
        assertTrue(tests.getFilePaths().contains("src/test/plsql/foo/f1.pkg"));
        assertTrue(tests.getFilePaths().contains("src/test/plsql/f2.pkg"));
    }

    /**
     * Sources and Tests Parameter have not Directory Tag
     * <p>
     * Given : a pom.xml with source and test tag not containing a directory tag.
     * When : pom is read and buildSourcesOptions / buildTestsOptions are run
     * Then : it should find all sources/tests files in default directories
     */
    @Test
    public void sources_and_tests_parameter_have_not_directory_tag() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("sources_and_tests_parameter_have_not_directory_tag");
        assertNotNull(utPlsqlMojo);

        // check sources
        FileMapperOptions sources = utPlsqlMojo.buildSourcesOptions();
        assertEquals(2, sources.getFilePaths().size());
        assertTrue(sources.getFilePaths().contains("src/main/plsql/f1.sql"));
        assertTrue(sources.getFilePaths().contains("src/main/plsql/foo/f2.sql"));

        // check tests
        FileMapperOptions tests = utPlsqlMojo.buildTestsOptions();
        assertEquals(3, tests.getFilePaths().size());
        assertTrue(tests.getFilePaths().contains("src/test/plsql/foo/f1.pkg"));
        assertTrue(tests.getFilePaths().contains("src/test/plsql/f2.pkg"));
        assertTrue(tests.getFilePaths().contains("src/test/plsql/foo/f1.sql"));
    }

    /**
     * Sources and Tests Parameter have not Directory Tag
     * <p>
     * Given : a pom.xml with source and test tag not containing a directory tag.
     * When : pom is read and buildSourcesOptions / buildTestsOptions are run
     * Then : it should find all sources/tests files in default directories
     */
    @Test
    public void sources_and_tests_parameter_have_not_includes_tag() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("sources_and_tests_parameter_have_not_includes_tag");
        assertNotNull(utPlsqlMojo);

        // check sources
        FileMapperOptions sources = utPlsqlMojo.buildSourcesOptions();
        assertEquals(2, sources.getFilePaths().size());
        assertTrue(sources.getFilePaths().contains("src/main/foo/f1.sql"));
        assertTrue(sources.getFilePaths().contains("src/main/foo/foo/f2.sql"));

        // check tests
        FileMapperOptions tests = utPlsqlMojo.buildTestsOptions();
        assertEquals(2, tests.getFilePaths().size());
        assertTrue(tests.getFilePaths().contains("src/test/bar/foo/f1.pkg"));
        assertTrue(tests.getFilePaths().contains("src/test/bar/f2.pkg"));
    }

    /**
     * Default Console Behaviour
     * <p>
     * Given : a pom.xml with file and console output
     * When : pom is read
     * Then : it should set the correct output channels
     */
    @Test
    public void default_console_output_behaviour() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("default_console_output_behaviour");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        // Assert that we added only the necessary reporters to the writer.
        List<ReporterParameter> reporters = utPlsqlMojo.reporters;
        assertEquals(3, reporters.size());

        ReporterParameter reporterParameter1 = reporters.get(0);
        assertTrue(reporterParameter1.isConsoleOutput());
        assertFalse(reporterParameter1.isFileOutput());

        ReporterParameter reporterParameter2 = reporters.get(1);
        assertFalse(reporterParameter2.isConsoleOutput());
        assertTrue(reporterParameter2.isFileOutput());

        ReporterParameter reporterParameter3 = reporters.get(2);
        assertTrue(reporterParameter3.isConsoleOutput());
        assertTrue(reporterParameter3.isFileOutput());
    }

    /**
     * Default Reporter
     * <p>
     * Given : a pom.xml without reporters
     * When : pom is read
     * Then : it should set the default reporter
     */
    @Test
    public void default_reporter() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("default_reporter");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals(1, utPlsqlMojo.reporters.size());
        assertEquals("UT_DOCUMENTATION_REPORTER", utPlsqlMojo.reporters.get(0).getName());
    }

    /**
     * Default Reporter
     * <p>
     * Given : a pom.xml with skipUtplsqlTests=true
     * When : pom is read
     * Then : Tests are skipped
     */
    @Test
    public void skip_utplsql_tests() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("skip_utplsql_tests");
        assertNotNull(utPlsqlMojo);

        final ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        utPlsqlMojo.execute();

        String standardOutput = console.toString();

        assertTrue(standardOutput.contains("utPLSQLTests are skipped."));
    }

    /**
     * Set ORA Stuck Timeout
     * <p>
     * Given : a pom.xml with oraStuckTimeout=0
     * When : pom is read
     * Then : Property is set
     */
    @Test
    public void ora_stuck_timeout() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("ora_stuck_timeout");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals(5, (int) utPlsqlMojo.oraStuckTimeout);
    }

    /**
     * Ora Stuck Timeout
     * <p>
     * Given : a pom.xml with ora-stuck-timeout set
     * When : pom is read
     * Then : DBMS_OUTPUT is enabled
     */
    @Test
    public void dbms_output() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("dbms_output");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();
    }

    /**
     * DB configuration from System Properties
     * <p>
     * Given : a pom.xml without dbUrl, dbUser and dbPass configured
     * When : pom is read
     * Then : System Properties must be used to configure database
     */
    @Test
    public void db_config_using_system_properties() throws Exception {
        System.setProperty("dbUrl", "jdbc:oracle:thin:@localhost:1521/FREEPDB1");
        System.setProperty("dbUser", "UT3");
        System.setProperty("dbPass", "UT3");

        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("db_config_using_system_properties");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        System.setProperty("dbUrl", "");
        System.setProperty("dbUser", "");
        System.setProperty("dbPass", "");
    }

    /**
     * Exclude a list of objects
     * <p>
     * Given : a pom.xml with a list of objects to exclude
     * When : pom is read
     * Then : Objects are excluded
     */
    @Test
    public void exclude_object() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("exclude_object");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals("app.pkg_test_me,app.test_pkg_test_me", utPlsqlMojo.excludeObject);
    }

    /**
     * Include a list of objects
     * <p>
     * Given : a pom.xml with a list of objects to include
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    public void include_object() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("include_object");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals("app.pkg_test_me,app.test_pkg_test_me", utPlsqlMojo.includeObject);
    }

    /**
     * Include an object by regex
     * <p>
     * Given : a pom.xml with a regex to include
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    public void include_object_expr() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("include_object_expr");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals("*", utPlsqlMojo.includeObjectExpr);
    }

    /**
     * Exclude an object by regex
     * <p>
     * Given : a pom.xml with a regex to exclude
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    public void exclude_object_expr() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("exclude_object_expr");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals("*", utPlsqlMojo.excludeObjectExpr);
    }


    /**
     * Include a schema by regex
     * <p>
     * Given : a pom.xml with a regex to include
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    public void include_schema_expr() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("include_schema_expr");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals("*", utPlsqlMojo.includeSchemaExpr);
    }

    /**
     * Exclude a schema by regex
     * <p>
     * Given : a pom.xml with a regex to exclude
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    public void exclude_schema_expr() throws Exception {
        UtPlsqlMojo utPlsqlMojo = createUtPlsqlMojo("exclude_schema_expr");
        assertNotNull(utPlsqlMojo);

        utPlsqlMojo.execute();

        assertEquals("*", utPlsqlMojo.excludeSchemaExpr);
    }

    private UtPlsqlMojo createUtPlsqlMojo(String directory) throws Exception {
        return (UtPlsqlMojo) rule.lookupConfiguredMojo(new File("src/test/resources/unit-tests/" + directory), "test");
    }

}

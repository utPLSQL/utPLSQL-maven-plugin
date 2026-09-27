package org.utplsql.maven.plugin;

import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of the mojo configured from the pom.xml files in {@value #UNIT_TESTS}.
 * <p>
 * The test harness does not resolve pom properties, so the database connection is given as mojo parameters.
 * Reports are written to the build directory instead of next to the test poms.
 */
@MojoTest
@MojoParameter(name = "url", value = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1")
@MojoParameter(name = "user", value = "UT3")
@MojoParameter(name = "password", value = "ut3")
@MojoParameter(name = "targetDir", value = "target/unit-tests")
class UtPlsqlMojoTest {

    static final String UNIT_TESTS = "src/test/resources/unit-tests/";

    /**
     * Invalid Sources Directory
     * <p>
     * Given : a pom.xml with invalid sources' directory
     * When : pom is read and buildSourcesOptions is run
     * Then : it should throw a MojoExecutionException
     */
    @Test
    @Basedir(UNIT_TESTS + "invalid_sources_directory")
    void invalid_sources_directory(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) {
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
    @Basedir(UNIT_TESTS + "invalid_tests_directory")
    void invalid_tests_directory(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) {
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
    @Basedir(UNIT_TESTS + "sources_tests_parameters")
    void sources_tests_parameters(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "sources_and_tests_parameter_does_not_exist")
    void sources_and_tests_parameter_does_not_exist(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "sources_and_tests_parameter_does_not_exist_but_default_directory_exists")
    void sources_and_tests_parameter_does_not_exist_but_default_directory_exists(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "sources_and_tests_parameter_have_not_directory_tag")
    void sources_and_tests_parameter_have_not_directory_tag(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "sources_and_tests_parameter_have_not_includes_tag")
    void sources_and_tests_parameter_have_not_includes_tag(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "default_console_output_behaviour")
    void default_console_output_behaviour(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "default_reporter")
    void default_reporter(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "skip_utplsql_tests")
    void skip_utplsql_tests(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        assertTrue(executeAndGetInfoMessages(utPlsqlMojo).contains("utPLSQLTests are skipped."));
    }

    /**
     * Skip Tests
     * <p>
     * Given : a pom.xml without skipUtplsqlTests and -DskipTests
     * When : pom is read
     * Then : Tests are skipped
     */
    @Test
    @Basedir(UNIT_TESTS + "skip_tests")
    @MojoParameter(name = "skipTests", value = "true")
    void skip_tests(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        assertTrue(executeAndGetInfoMessages(utPlsqlMojo).contains("utPLSQLTests are skipped."));
    }

    /**
     * Maven Test Skip
     * <p>
     * Given : a pom.xml without skipUtplsqlTests and -Dmaven.test.skip
     * When : pom is read
     * Then : Tests are skipped
     */
    @Test
    @Basedir(UNIT_TESTS + "skip_tests")
    @MojoParameter(name = "mavenTestSkip", value = "true")
    void maven_test_skip(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        assertTrue(executeAndGetInfoMessages(utPlsqlMojo).contains("utPLSQLTests are skipped."));
    }

    /**
     * Skip Tests overridden by skipUtplsqlTests
     * <p>
     * Given : -DskipTests and -DskipUtplsqlTests=false
     * When : pom is read
     * Then : utPLSQL tests are not skipped
     */
    @Test
    @Basedir(UNIT_TESTS + "skip_tests")
    @MojoParameter(name = "skipTests", value = "true")
    @MojoParameter(name = "mavenTestSkip", value = "true")
    @MojoParameter(name = "skipUtplsqlTests", value = "false")
    void skip_tests_overridden_by_skip_utplsql_tests(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) {
        assertFalse(utPlsqlMojo.isSkipped());
    }

    /**
     * Tests are not skipped by default
     * <p>
     * Given : a pom.xml without skipUtplsqlTests, skipTests and maven.test.skip
     * When : pom is read
     * Then : utPLSQL tests are not skipped
     */
    @Test
    @Basedir(UNIT_TESTS + "skip_tests")
    void not_skipped_by_default(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) {
        assertFalse(utPlsqlMojo.isSkipped());
    }

    private static List<String> executeAndGetInfoMessages(UtPlsqlMojo utPlsqlMojo) throws Exception {
        List<String> infoMessages = new ArrayList<>();
        utPlsqlMojo.setLog(new SystemStreamLog() {
            @Override
            public void info(CharSequence content) {
                infoMessages.add(content.toString());
            }
        });

        utPlsqlMojo.execute();

        return infoMessages;
    }

    /**
     * Set ORA Stuck Timeout
     * <p>
     * Given : a pom.xml with oraStuckTimeout=0
     * When : pom is read
     * Then : Property is set
     */
    @Test
    @Basedir(UNIT_TESTS + "ora_stuck_timeout")
    void ora_stuck_timeout(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "dbms_output")
    void dbms_output(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        utPlsqlMojo.execute();
    }

    /**
     * Exclude a list of objects
     * <p>
     * Given : a pom.xml with a list of objects to exclude
     * When : pom is read
     * Then : Objects are excluded
     */
    @Test
    @Basedir(UNIT_TESTS + "exclude_object")
    void exclude_object(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "include_object")
    void include_object(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "include_object_expr")
    void include_object_expr(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        utPlsqlMojo.execute();

        assertEquals("APP.*", utPlsqlMojo.includeObjectExpr);
    }

    /**
     * Exclude an object by regex
     * <p>
     * Given : a pom.xml with a regex to exclude
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    @Basedir(UNIT_TESTS + "exclude_object_expr")
    void exclude_object_expr(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
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
    @Basedir(UNIT_TESTS + "include_schema_expr")
    void include_schema_expr(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        utPlsqlMojo.execute();

        assertEquals("APP", utPlsqlMojo.includeSchemaExpr);
    }

    /**
     * Exclude a schema by regex
     * <p>
     * Given : a pom.xml with a regex to exclude
     * When : pom is read
     * Then : Objects are included
     */
    @Test
    @Basedir(UNIT_TESTS + "exclude_schema_expr")
    void exclude_schema_expr(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        utPlsqlMojo.execute();

        assertEquals("*", utPlsqlMojo.excludeSchemaExpr);
    }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class MojoRuleTest {

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
    public void invalidSourcesDirectory() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/invalid-sources-directories/"), "test");
        assertNotNull(utplsqlMojo);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, utplsqlMojo::execute);

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
    public void invalidTestsDirectory() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/invalid-tests-sources-directories/"), "test");
        assertNotNull(utplsqlMojo);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, utplsqlMojo::execute);

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
    public void sourcesTestsParameters() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/test-sources-tests-params/"), "test");
        assertNotNull(utplsqlMojo);

        assertEquals(2, utplsqlMojo.reporters.size());

        // check sources
        FileMapperOptions sources = utplsqlMojo.buildSourcesOptions();
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
        FileMapperOptions tests = utplsqlMojo.buildTestsOptions();
        assertEquals(2, tests.getFilePaths().size());
        assertTrue(tests.getFilePaths().contains("te/st/file.bdy"));
        assertTrue(tests.getFilePaths().contains("te/st/spec.spc"));
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
    public void sourcesAndTestsParameterDoesNotExist() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule.lookupConfiguredMojo(
                new File("src/test/resources/unit-tests/test-no-sources-tests-params/directory-does-not-exist/"), "test");
        assertNotNull(utplsqlMojo);

        // check sources
        FileMapperOptions sources = utplsqlMojo.buildSourcesOptions();
        assertEquals(0, sources.getFilePaths().size());

        // check tests
        FileMapperOptions tests = utplsqlMojo.buildTestsOptions();
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
    public void sourcesAndTestsParameterDoesNotExistButDefaultDirectoryExists() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/test-no-sources-tests-params/directory-exists/"), "test");
        assertNotNull(utplsqlMojo);

        // check sources
        FileMapperOptions sources = utplsqlMojo.buildSourcesOptions();
        assertEquals(2, sources.getFilePaths().size());
        assertTrue(sources.getFilePaths().contains("src/main/plsql/f1.sql"));
        assertTrue(sources.getFilePaths().contains("src/main/plsql/f2.sql"));

        // check tests
        FileMapperOptions tests = utplsqlMojo.buildTestsOptions();
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
    public void sourcesAndTestsParameterHaveNotDirectoryTag() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/partial-source-and-test-tag/missing-directory/"), "test");
        assertNotNull(utplsqlMojo);

        // check sources
        FileMapperOptions sources = utplsqlMojo.buildSourcesOptions();
        assertEquals(2, sources.getFilePaths().size());
        assertTrue(sources.getFilePaths().contains("src/main/plsql/f1.sql"));
        assertTrue(sources.getFilePaths().contains("src/main/plsql/foo/f2.sql"));

        // check tests
        FileMapperOptions tests = utplsqlMojo.buildTestsOptions();
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
    public void sourcesAndTestsParameterHaveNotIncludesTag() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/partial-source-and-test-tag/missing-includes/"), "test");
        assertNotNull(utplsqlMojo);

        // check sources
        FileMapperOptions sources = utplsqlMojo.buildSourcesOptions();
        assertEquals(2, sources.getFilePaths().size());
        assertTrue(sources.getFilePaths().contains("src/main/foo/f1.sql"));
        assertTrue(sources.getFilePaths().contains("src/main/foo/foo/f2.sql"));

        // check tests
        FileMapperOptions tests = utplsqlMojo.buildTestsOptions();
        assertEquals(2, tests.getFilePaths().size());
        assertTrue(tests.getFilePaths().contains("src/test/bar/foo/f1.pkg"));
        assertTrue(tests.getFilePaths().contains("src/test/bar/f2.pkg"));
    }

    /**
     * Default Console Behaviour
     */
    @Test
    public void defaultConsoleBehaviour() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/default-console-output-behaviour/"), "test");
        assertNotNull(utplsqlMojo);

        utplsqlMojo.execute();

        // Assert that we added only the necessary reporters to the writer.
        List<ReporterParameter> reporters = utplsqlMojo.reporters;
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
     */
    @Test
    public void defaultReporter() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/default-reporter/"), "test");
        assertNotNull(utplsqlMojo);

        utplsqlMojo.execute();

        assertEquals(1, utplsqlMojo.reporters.size());
        assertEquals("UT_DOCUMENTATION_REPORTER", utplsqlMojo.reporters.get(0).getName());
    }

    /**
     * Skip utPLSQL Tests
     */
    @Test
    public void skipUtplsqlTests() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/skip-utplsql-tests/"), "test");
        assertNotNull(utplsqlMojo);

        final ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));

        utplsqlMojo.execute();

        String standardOutput = console.toString();

        assertTrue(standardOutput.contains("utPLSQLTests are skipped."));
    }
}

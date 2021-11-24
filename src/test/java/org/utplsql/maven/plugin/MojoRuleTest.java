package org.utplsql.maven.plugin;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.utplsql.api.DBHelper;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.api.Version;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.api.reporter.ReporterFactory;
import org.utplsql.maven.plugin.io.ReportWriter;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DBHelper.class, ReporterFactory.class})
public class MojoRuleTest {

    @Rule
    public final MojoRule rule = new MojoRule();

    @Mock
    public Connection mockConnection;

    @Mock
    public Version mockVersion;

    @Mock
    public ReporterFactory mockReporterFactory;

    @Before
    public void setUp() throws Exception {
        mockStatic(DBHelper.class);
        when(DBHelper.getDatabaseFrameworkVersion(mockConnection)).thenReturn(mockVersion);

        mockStatic(ReporterFactory.class);
        when(ReporterFactory.createEmpty()).thenReturn(mockReporterFactory);
    }

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

        assertEquals("Invalid <SOURCES> in your pom.xml", exception.getMessage());
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

        assertEquals("Invalid <TESTS> in your pom.xml: Invalid <directory> bar in resource. Check your pom.xml", exception.getMessage());
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

        List<Reporter> reporterList = new ArrayList<>();
        when(mockReporterFactory.createReporter(anyString())).thenAnswer(invocation -> {
            Reporter mockReporter = mock(Reporter.class);
            reporterList.add(mockReporter);
            return mockReporter;
        });

        ReportWriter reportWriter = new ReportWriter(utplsqlMojo.targetDir, mockVersion, utplsqlMojo.getLog());
        utplsqlMojo.initReporters(mockConnection, reportWriter, mockReporterFactory);

        // Assert that we called the creation reporter with the correct parameters.
        verify(mockReporterFactory, times(2)).createReporter("UT_DOCUMENTATION_REPORTER");
        verify(mockReporterFactory).createReporter("UT_COVERAGE_SONAR_REPORTER");
        verify(mockReporterFactory).createReporter("UT_SONAR_TEST_REPORTER");
        verifyNoMoreInteractions(mockReporterFactory);

        // Assert that all reporters have been initialized.
        for (Reporter mockReporter : reporterList) {
            verify(mockReporter).init(mockConnection);
            verifyNoMoreInteractions(mockReporter);
        }

        // Assert that we added only the necessary reporters to the writer.
        List<Pair<Reporter, ReporterParameter>> reporters = reportWriter.getReporters();
        assertEquals(3, reporters.size());

        ReporterParameter reporterParameter1 = reporters.get(0).getRight();
        assertTrue(reporterParameter1.isConsoleOutput());
        assertFalse(reporterParameter1.isFileOutput());

        ReporterParameter reporterParameter2 = reporters.get(1).getRight();
        assertFalse(reporterParameter2.isConsoleOutput());
        assertTrue(reporterParameter2.isFileOutput());

        ReporterParameter reporterParameter3 = reporters.get(2).getRight();
        assertTrue(reporterParameter3.isConsoleOutput());
        assertTrue(reporterParameter3.isFileOutput());
    }

    /**
     * Add Default Reporter
     */
    @Test
    public void addDefaultReporter() throws Exception {
        UtPlsqlMojo utplsqlMojo = (UtPlsqlMojo) rule
                .lookupConfiguredMojo(new File("src/test/resources/unit-tests/default-console-output-behaviour/"), "test");
        assertNotNull(utplsqlMojo);

        List<Reporter> reporterList = new ArrayList<>();
        when(mockReporterFactory.createReporter(anyString())).thenAnswer(invocation -> {
            Reporter mockReporter = mock(Reporter.class);
            when(mockReporter.getTypeName()).thenReturn(invocation.getArgument(0));
            reporterList.add(mockReporter);
            return mockReporter;
        });

        List<ReporterParameter> reporterParameters = utplsqlMojo.reporters;
        reporterParameters.clear();

        ReportWriter reportWriter = new ReportWriter(utplsqlMojo.targetDir, mockVersion, utplsqlMojo.getLog());
        utplsqlMojo.initReporters(mockConnection, reportWriter, mockReporterFactory);

        assertEquals(1, reporterList.size());
        assertEquals("UT_DOCUMENTATION_REPORTER", reporterList.get(0).getTypeName());
        verify(reporterList.get(0)).init(mockConnection);
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

package org.utplsql.maven.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UtPLSQLMojoIT {

    private static String pluginVersion = null;

    @BeforeClass
    public static void setUp() throws VerificationException, IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));

        Verifier verifier = new Verifier(new File("").getAbsolutePath());

        // Install Plugin
        verifier.setAutoclean(false);
        verifier.addCliOption("-DskipTests");
        verifier.executeGoal("install");

        pluginVersion = model.getVersion();
    }

    @Test
    public void testSimpleProject() {
        try {
            final String PROJECT_NAME = "simple-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml", "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test of Definition " + e.getMessage());
        }
    }

    @Test
    public void testRegexProject() {
        try {
            final String PROJECT_NAME = "regex-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml", "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testTypeMappingProject() {
        try {
            final String PROJECT_NAME = "type-mapping-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml", "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testOwnerParameterProject() {
        try {
            final String PROJECT_NAME = "owner-param-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml", "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testMinimalistProject() {
        try {
            final String PROJECT_NAME = "minimalist-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testTagsProject() {
        try {
            final String PROJECT_NAME = "tags-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test of Definition " + e.getMessage());
        }
    }

    @Test
    public void testSkipUtplsqlTests() {
        try {
            final String PROJECT_NAME = "skip-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier = createVerifier(testProject);
            verifier.executeGoal("test");

            assertFalse(new File("target/test-classes/" + PROJECT_NAME + "/target").exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception running the test of Definition " + e.getMessage());
        }
    }

    /**
     * Duration is set to 1 before comparing contents as it is always different.
     * Path separator is set to "/" to ensure windows / linux / mac compatibility.
     * \r and \n are removed to provide simpler comparison.
     *
     * @param projectName Project name
     * @param files       Files to compare
     */
    private void checkReportsGenerated(String projectName, String... files) {
        for (String filename : files) {
            File outputFile = new File("target/test-classes/" + projectName + "/target", filename);
            File expectedOutputFile = new File("target/test-classes/" + projectName + "/expected-output", filename);

            assertTrue("The reporter for " + filename + " was not generated", outputFile.exists());

            try {
                try (Stream<String> stream = Files.lines(Paths.get("target", "test-classes", projectName, "target", filename))) {
                    String outputContent = stream
                            .filter(line -> !line.contains("<?xml"))
                            .map(line -> line.replaceAll("(duration=\"[0-9.]*\")", "duration=\"1\""))
                            .map(line -> line.replaceAll("\\\\", "/"))
                            .map(line -> line.replaceAll("\r", "").replaceAll("\n", ""))
                            .collect(Collectors.joining("\n"));
                    assertEquals("The files differ!",
                            FileUtils.readFileToString(expectedOutputFile, "utf-8")
                                    .replace("\r", "")
                                    .replace("\n", ""),
                            outputContent.replace("\n", ""));
                }
            } catch (IOException e) {
                e.printStackTrace();
                fail("Unexpected Exception running the test : " + e.getMessage());
            }
        }
    }

    private Verifier createVerifier(File testProject) throws VerificationException {
        Verifier verifier;
        verifier = new Verifier(testProject.getAbsolutePath());
        verifier.addCliOption("-N");
        verifier.addCliOption("-X");
        verifier.addCliOption("-Dutplsql-maven-plugin-version=" + pluginVersion);
        verifier.addCliOption("-DdbUrl=jdbc:oracle:thin:@127.0.0.1:1521:XE");
        verifier.addCliOption("-DdbUser=UT3");
        verifier.addCliOption("-DdbPass=UT3");
        return verifier;
    }

}

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
    public void simpleProject() throws IOException, VerificationException {
        final String testFolder = "integration-tests/simple-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");

        checkReports(testFolder, "utplsql/sonar-test-report.xml", "utplsql/coverage-sonar-report.xml");
    }

    @Test
    public void regexProject() throws IOException, VerificationException {
        final String testFolder = "integration-tests/regex-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");

        checkReports(testFolder, "utplsql/sonar-test-report.xml", "utplsql/coverage-sonar-report.xml");
    }

    @Test
    public void typeMappingProject() throws IOException, VerificationException {
        final String testFolder = "integration-tests/type-mapping-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");

        checkReports(testFolder, "utplsql/sonar-test-report.xml", "utplsql/coverage-sonar-report.xml");
    }

    @Test
    public void ownerParameterProject() throws IOException, VerificationException {
        final String testFolder = "integration-tests/owner-param-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");

        checkReports(testFolder, "utplsql/sonar-test-report.xml", "utplsql/coverage-sonar-report.xml");
    }

    @Test
    public void minimalistProject() throws IOException, VerificationException {
        final String testFolder = "integration-tests/minimalist-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");
    }

    @Test
    public void tagsProject() throws IOException, VerificationException {
        final String testFolder = "integration-tests/tags-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");

        checkReports(testFolder, "utplsql/sonar-test-report.xml");
    }

    @Test
    public void skipUtplsqlTests() throws IOException, VerificationException {
        final String testFolder = "integration-tests/skip-project";
        File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + testFolder);

        Verifier verifier = createVerifier(testProject);
        verifier.executeGoal("test");

        assertFalse(new File("target/test-classes/" + testFolder + "/target").exists());
    }

    /**
     * Duration is set to 1 before comparing contents as it is always different.
     * Path separator is set to "/" to ensure windows / linux / mac compatibility.
     * \r and \n are removed to provide simpler comparison.
     *
     * @param testFolder Project name
     * @param files      Files to compare
     */
    private void checkReports(String testFolder, String... files) throws IOException {
        for (String filename : files) {
            File outputFile = new File("target/test-classes/" + testFolder + "/target", filename);
            File expectedOutputFile = new File("target/test-classes/" + testFolder + "/expected-output", filename);

            assertTrue("The report for " + filename + " was not generated", outputFile.exists());

            try (Stream<String> stream = Files.lines(Paths.get("target", "test-classes", testFolder, "target", filename))) {
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

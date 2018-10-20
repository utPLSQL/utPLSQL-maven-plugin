package org.utpsql.maven.plugin.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtPLSQLMojoIT {

    private static String pluginVersion = null;

    @BeforeClass
    public static void setUp() throws VerificationException, IOException, XmlPullParserException {
        // Read plugin pom file
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("../pom.xml"));

        File pomFile = new File("../");
        Verifier verifier = new Verifier(pomFile.getAbsolutePath());

        // install plugin
        verifier.setAutoclean(false);
        verifier.addCliOption("-Dmaven.skip.test=true");
        verifier.addCliOption("-DskipITs");
        verifier.executeGoal("install");

        pluginVersion = model.getVersion();
    }

    @Test
    public void testSimpleProject() throws Exception {

        try {
            final String PROJECT_NAME = "simple-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier;
            verifier = new Verifier(testProject.getAbsolutePath());
            verifier.addCliOption("-N");
            verifier.addCliOption("-Dutplsql-maven-plugin-version=" + pluginVersion);
            verifier.addCliOption("-DdbUrl=" + System.getProperty("dbUrl"));
            verifier.addCliOption("-DdbUser=" + System.getProperty("dbUser"));
            verifier.addCliOption("-DdbPass=" + System.getProperty("dbPass"));

            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml",
                    "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test of Definition " + e.getMessage());
        }
    }

    @Test
    public void testRegexProject() throws Exception {
        try {
            final String PROJECT_NAME = "regex-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier;
            verifier = new Verifier(testProject.getAbsolutePath());
            verifier.addCliOption("-N");
            verifier.addCliOption("-Dutplsql-maven-plugin-version=" + pluginVersion);
            verifier.addCliOption("-DdbUrl=" + System.getProperty("dbUrl"));
            verifier.addCliOption("-DdbUser=" + System.getProperty("dbUser"));
            verifier.addCliOption("-DdbPass=" + System.getProperty("dbPass"));

            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml",
                    "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testTypeMappingProject() throws Exception {
        try {
            final String PROJECT_NAME = "type-mapping-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier;
            verifier = new Verifier(testProject.getAbsolutePath());
            verifier.addCliOption("-N");
            verifier.addCliOption("-Dutplsql-maven-plugin-version=" + pluginVersion);
            verifier.addCliOption("-DdbUrl=" + System.getProperty("dbUrl"));
            verifier.addCliOption("-DdbUser=" + System.getProperty("dbUser"));
            verifier.addCliOption("-DdbPass=" + System.getProperty("dbPass"));

            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml",
                    "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testOwnerParameterProject() throws Exception {
        try {
            final String PROJECT_NAME = "owner-param-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier;
            verifier = new Verifier(testProject.getAbsolutePath());
            verifier.addCliOption("-N");
            verifier.addCliOption("-Dutplsql-maven-plugin-version=" + pluginVersion);
            verifier.addCliOption("-DdbUrl=" + System.getProperty("dbUrl"));
            verifier.addCliOption("-DdbUser=" + System.getProperty("dbUser"));
            verifier.addCliOption("-DdbPass=" + System.getProperty("dbPass"));

            verifier.executeGoal("test");

            checkReportsGenerated(PROJECT_NAME, "utplsql/sonar-test-reporter.xml",
                    "utplsql/coverage-sonar-reporter.xml");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    @Test
    public void testMinimalistProject() throws Exception {
        try {
            final String PROJECT_NAME = "minimalist-project";
            File testProject = ResourceExtractor.simpleExtractResources(getClass(), "/" + PROJECT_NAME);

            Verifier verifier;
            verifier = new Verifier(testProject.getAbsolutePath());
            verifier.addCliOption("-N");
            verifier.addCliOption("-Dutplsql-maven-plugin-version=" + pluginVersion);
            verifier.addCliOption("-DdbUrl=" + System.getProperty("dbUrl"));
            verifier.addCliOption("-DdbUser=" + System.getProperty("dbUser"));
            verifier.addCliOption("-DdbPass=" + System.getProperty("dbPass"));

            verifier.executeGoal("test");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test : " + e.getMessage());
        }
    }

    /**
     * 
     * @param files
     */
    private void checkReportsGenerated(String projectName, String... files) {
        for (String filename : files) {
            File outputFile = new File("target/test-classes/" + projectName + "/target", filename);
            File expectedOutputFile = new File("target/test-classes/" + projectName + "/expected-output", filename);

            Assert.assertTrue("The reporter for " + filename + " was not generated", outputFile.exists());
            try {
                // Duration is set to 1 before comparing contents as it is always different.
                // Path separator is set to "/" to ensure windows / linux / mac compatibility
                Stream<String> stream = Files
                        .lines(Paths.get("target", "test-classes", projectName, "target", filename));

                String outputContent = stream
                        .map(line -> line.replaceAll("(encoding=\"[^\"]*\")", "encoding=\"WINDOWS-1252\""))
                        .map(line -> line.replaceAll("(duration=\"[0-9\\.]*\")", "duration=\"1\""))
                        .map(line -> line.replaceAll("\\\\", "/"))
                        .map(line -> line.replaceAll("\r", "").replaceAll("\n", "")).collect(Collectors.joining("\n"));

                stream.close();
                Assert.assertEquals("The files differ!",
                        FileUtils.readFileToString(expectedOutputFile, "utf-8").replace("\r", ""), outputContent);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Assert.fail("Unexpected Exception running the test : " + e.getMessage());
            }
        }
    }
}

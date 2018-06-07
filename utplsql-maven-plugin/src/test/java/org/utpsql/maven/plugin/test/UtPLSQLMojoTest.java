package org.utpsql.maven.plugin.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.utplsql.maven.plugin.UtPLSQLMojo;

public class UtPLSQLMojoTest
{
	public static final String TARGET_DIRECTORY = "target/test-classes";

	@Rule
	public MojoRule rule = new MojoRule();

	@Test
	public void testDefinition() throws Exception
	{

		try
		{
		    final String PROJECT_NAME =  "simple-project";
			UtPLSQLMojo myMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File(TARGET_DIRECTORY+"/"+PROJECT_NAME), "test");

			Assert.assertNotNull(myMojo);
			myMojo.execute();

			checkReportsGenerated(PROJECT_NAME,"utplsql/coverage-sonar-reporter.xml", "utplsql/sonar-test-reporter.xml");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail("Unexpected Exception running the test of Definition");
		}
	}
	
	@Test
    public void testRegexDefinition() throws Exception
    {
        try
        {
            final String PROJECT_NAME =  "regex-project";
            UtPLSQLMojo myMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File(TARGET_DIRECTORY+"/"+PROJECT_NAME), "test");

            Assert.assertNotNull(myMojo);
            myMojo.execute();

            checkReportsGenerated(PROJECT_NAME,"utplsql/coverage-sonar-reporter.xml", "utplsql/sonar-test-reporter.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test of Definition");
        }
    }
	
	@Test
    public void testTypeMappingDefinition() throws Exception
    {
        try
        {
            final String PROJECT_NAME =  "type-mapping-project";
            UtPLSQLMojo myMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File(TARGET_DIRECTORY+"/"+PROJECT_NAME), "test");

            Assert.assertNotNull(myMojo);
            myMojo.execute();

            checkReportsGenerated(PROJECT_NAME,"utplsql/coverage-sonar-reporter.xml", "utplsql/sonar-test-reporter.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected Exception running the test of Definition");
        }
    }

	/**
	 * 
	 * @param files
	 */
	private void checkReportsGenerated(String projectName, String... files)
	{
		for (String filename : files)
		{
		    File outputFile = new File(TARGET_DIRECTORY+"/"+projectName+"/target/", filename);
            File expectedOutputFile = new File(TARGET_DIRECTORY+"/"+projectName+"/expected-output/", filename);
            
            Assert.assertTrue("The reporter for " + filename + " was not generated", outputFile.exists());
            try {
                // Duration is set to 1 before comparing contents as it is always different.
                // Path separator is set to "/" to ensure windows / linux / mac compatibility  
                Stream<String> stream = Files.lines(Paths.get(TARGET_DIRECTORY,projectName,"target",filename));
                String outputContent = stream.map(line -> line.replaceAll("(duration=\"[0-9\\.]*\")", "duration=\"1\""))
                                                .map(line ->  line.replaceAll("\\\\", "/"))
                                                .map(line -> line.replaceAll("\r", "").replaceAll("\n", ""))
                                                .collect(Collectors.joining("\n"));
                                                
                stream.close();
                Assert.assertEquals("The files differ!", 
                        outputContent,
                        FileUtils.readFileToString(expectedOutputFile, "utf-8").replace("\r",""));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Assert.fail("Unexpected Exception running the test of Definition");
            }
		}
	}
}

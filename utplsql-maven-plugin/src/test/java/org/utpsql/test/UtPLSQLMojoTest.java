package org.utpsql.test;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.utplsql.UtPLSQLMojo;

public class UtPLSQLMojoTest
{
	public static final String POM_PATH = "src/test/resources/";

	public static final String OUTPUT_DIRECTORY = "target/test-classes";

	@Rule
	public MojoRule rule = new MojoRule();

	@Test
	public void testDefinition() throws Exception
	{

		try
		{
			UtPLSQLMojo myMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File(POM_PATH), "test");

			Assert.assertNotNull(myMojo);
			myMojo.execute();

			checkCoverReportsGenerated("utplsql/coverage-sonar-reporter.xml", "utplsql/sonar-test-reporter.xml");
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
	private void checkCoverReportsGenerated(String... files)
	{
		for (String filename : files)
		{
			File file = new File(OUTPUT_DIRECTORY, filename);
			Assert.assertTrue("The reporter for " + filename + " was not generated", file.exists());
		}
	}

}

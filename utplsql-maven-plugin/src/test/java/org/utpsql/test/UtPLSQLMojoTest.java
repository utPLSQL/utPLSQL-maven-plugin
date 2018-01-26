package org.utpsql.test;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.utplsql.UtPLSQLMojo;

public class UtPLSQLMojoTest
{
	public static final String POM_PATH = "src/test/resources/pom.xml";

	@Rule
	public MojoRule rule = new MojoRule()
	{
		@Override
		protected void before() throws Throwable
		{
		}

		@Override
		protected void after()
		{
		}
	};

	@Test
	public void testDefinition() throws Exception
	{
		UtPLSQLMojo myMojo = (UtPLSQLMojo) rule.lookupMojo("test", POM_PATH);
		Assert.assertNotNull(myMojo);
		myMojo.execute();
	}

}

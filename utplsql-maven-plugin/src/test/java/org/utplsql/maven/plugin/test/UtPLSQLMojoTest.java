package org.utplsql.maven.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;
import org.utplsql.api.FileMapperOptions;
import org.utplsql.maven.plugin.UtPLSQLMojo;

public class UtPLSQLMojoTest {

	@Rule
	public MojoRule rule = new MojoRule();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	/**
	 * 	testInvalidSourcesDirectory.
	 * 
	 *	Given 	: a pom.xml with invalid sources directory 
	 * 	When  	: pom is read and buildSourcesOptions is run
	 * 	Then 	: it should throw a MojoExecutionException
	 */
	@Test
	public void testInvalidSourcesDirectory() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/invalidTestsSourcesDirectories/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		// Expected exception
        thrown.expect(MojoExecutionException.class);
        // Excepted message
        thrown.expectMessage("Invalid <SOURCES> in your pom.xml");
        
        Whitebox.invokeMethod(utplsqlMojo, "buildSourcesOptions");
		
	}
	
	/**
	 * 	testInvalidTestsDirectory.
	 * 
	 *	Given 	: a pom.xml with invalid tests directory 
	 * 	When  	: pom is read and buildTestsOptions is run
	 * 	Then 	: it should throw a MojoExecutionException
	 */
	@Test
	public void testInvalidTestsDirectory() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/invalidTestsSourcesDirectories/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		// Expected exception
        thrown.expect(MojoExecutionException.class);
        // Excepted message
        thrown.expectMessage("Invalid <TESTS> in your pom.xml");
        
        Whitebox.invokeMethod(utplsqlMojo, "buildTestsOptions");
	}
	
	/**
	 * 	testSourcesTestsParameters.
	 * 
	 *	Given 	: a pom.xml with sources and tests with a lot of parameters
	 * 	When  	: pom is read and buildSourcesOptions / buildTestsOptions are run
	 * 	Then 	: it should fill all parameters correctly
	 */
	@Test
	public void testSourcesTestsParameters() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/testSourcesTestsParams/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		// TODO : move to another test about reporters
		List<String> reporters = Whitebox.<List<String>>getInternalState(utplsqlMojo, "reporters");
		assertEquals(reporters.size(), 2);
		
		// check sources
		FileMapperOptions sources = Whitebox.invokeMethod(utplsqlMojo, "buildSourcesOptions");
		assertEquals(1, sources.getFilePaths().size());
		assertEquals("srcs/foo.sql", sources.getFilePaths().get(0));
		assertEquals("code_owner", sources.getObjectOwner() );
		assertEquals(".*/\\w+/(\\w+)/(\\w+)\\.\\w{3}", sources.getRegexPattern());
		assertEquals(new Integer(9), sources.getNameSubExpression());
		assertEquals(new Integer(1), sources.getTypeSubExpression());
		assertEquals(new Integer(4), sources.getOwnerSubExpression());
		assertEquals(1, sources.getTypeMappings().size());
		assertEquals("bar", sources.getTypeMappings().get(0).getKey());
		assertEquals("foo", sources.getTypeMappings().get(0).getValue());
		
		// check tests
		FileMapperOptions tests = Whitebox.invokeMethod(utplsqlMojo, "buildTestsOptions");
		assertEquals(2, tests.getFilePaths().size());
		assertTrue(tests.getFilePaths().contains("te/st/file.bdy"));
		assertTrue(tests.getFilePaths().contains("te/st/spec.spc"));
		assertEquals("tests_owner", tests.getObjectOwner() );
		assertEquals(".*/\\w+/(\\w+)/(\\w+)\\.\\w{3}", tests.getRegexPattern());
		assertEquals(new Integer(54), tests.getNameSubExpression());
		assertEquals(new Integer(21), tests.getTypeSubExpression());
		assertEquals(new Integer(24), tests.getOwnerSubExpression());
		assertEquals(1, tests.getTypeMappings().size());
		assertEquals("def", tests.getTypeMappings().get(0).getKey());
		assertEquals("abc", tests.getTypeMappings().get(0).getValue());

	}
	
	/**
	 * 	testSourcesAndTestsParameterDoesNotExist.
	 * 
	 *	Given 	: a pom.xml with no sources / tests tags and default directory does not exist.
	 * 	When  	: pom is read and buildSourcesOptions / buildTestsOptions are run
	 * 	Then 	: it should not find any source files
	 */
	@Test
	public void testSourcesAndTestsParameterDoesNotExist() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/testNoSourcesTestsParams/directoryDoesNotExist/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		
		// check sources
		FileMapperOptions sources = Whitebox.invokeMethod(utplsqlMojo, "buildSourcesOptions");
		assertEquals(0, sources.getFilePaths().size());
		
		
		// check tests
		FileMapperOptions tests = Whitebox.invokeMethod(utplsqlMojo, "buildTestsOptions");
		assertEquals(0, tests.getFilePaths().size());
	}
	
	/**
	 * 	testSourcesAndTestsParameterDoesNotExistButDefaultDirectoryExists.
	 * 
	 *	Given 	: a pom.xml with no sources / tests tags but default directory  exists.
	 * 	When  	: pom is read and buildSourcesOptions / buildTestsOptions are run
	 * 	Then 	: it should find all sources/tests files in default directories
	 */
	@Test
	public void testSourcesAndTestsParameterDoesNotExistButDefaultDirectoryExists() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/testNoSourcesTestsParams/directoryExists/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		// check sources
		FileMapperOptions sources = Whitebox.invokeMethod(utplsqlMojo, "buildSourcesOptions");
		assertEquals(2, sources.getFilePaths().size());
		assertTrue(sources.getFilePaths().contains("src/main/plsql/f1.sql"));
		assertTrue(sources.getFilePaths().contains("src/main/plsql/f2.sql"));
		
		// check tests
		FileMapperOptions tests = Whitebox.invokeMethod(utplsqlMojo, "buildTestsOptions");
		assertEquals(2, tests.getFilePaths().size());
		assertTrue(tests.getFilePaths().contains("src/test/plsql/foo/f1.pkg"));
		assertTrue(tests.getFilePaths().contains("src/test/plsql/f2.pkg"));

	}
	
	/**
	 * 	testSourcesAndTestsParameterHaveNotDirectoryTag.
	 * 
	 *	Given 	: a pom.xml with source and test tag not containing a directory tag.
	 * 	When  	: pom is read and buildSourcesOptions / buildTestsOptions are run
	 * 	Then 	: it should find all sources/tests files in default directories
	 */
	@Test
	public void testSourcesAndTestsParameterHaveNotDirectoryTag() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/partialSourceAndTestTag/missingDirectory/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		// check sources
		FileMapperOptions sources = Whitebox.invokeMethod(utplsqlMojo, "buildSourcesOptions");
		assertEquals(2, sources.getFilePaths().size());
		assertTrue(sources.getFilePaths().contains("src/main/plsql/f1.sql"));
		assertTrue(sources.getFilePaths().contains("src/main/plsql/foo/f2.sql"));
		
		// check tests
		FileMapperOptions tests = Whitebox.invokeMethod(utplsqlMojo, "buildTestsOptions");
		assertEquals(3, tests.getFilePaths().size());
		assertTrue(tests.getFilePaths().contains("src/test/plsql/foo/f1.pkg"));
		assertTrue(tests.getFilePaths().contains("src/test/plsql/f2.pkg"));
		assertTrue(tests.getFilePaths().contains("src/test/plsql/foo/f1.sql"));
	}
	
	/**
	 * 	testSourcesAndTestsParameterHaveNotDirectoryTag.
	 * 
	 *	Given 	: a pom.xml with source and test tag not containing a directory tag.
	 * 	When  	: pom is read and buildSourcesOptions / buildTestsOptions are run
	 * 	Then 	: it should find all sources/tests files in default directories
	 */
	@Test
	public void testSourcesAndTestsParameterHaveNotIncludesTag() throws Exception {
		UtPLSQLMojo utplsqlMojo = (UtPLSQLMojo) rule.lookupConfiguredMojo(new File("src/test/resources/partialSourceAndTestTag/missingIncludes/"), "test");
		Assert.assertNotNull(utplsqlMojo);
		
		// check sources
		FileMapperOptions sources = Whitebox.invokeMethod(utplsqlMojo, "buildSourcesOptions");
		assertEquals(2, sources.getFilePaths().size());
		assertTrue(sources.getFilePaths().contains("src/main/foo/f1.sql"));
		assertTrue(sources.getFilePaths().contains("src/main/foo/foo/f2.sql"));
		
		// check tests
		FileMapperOptions tests = Whitebox.invokeMethod(utplsqlMojo, "buildTestsOptions");
		assertEquals(2, tests.getFilePaths().size());
		assertTrue(tests.getFilePaths().contains("src/test/bar/foo/f1.pkg"));
		assertTrue(tests.getFilePaths().contains("src/test/bar/f2.pkg"));
	}
	
	
}

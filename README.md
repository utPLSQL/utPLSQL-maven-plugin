# utPLSQL-maven-plugin
A maven plugin for running Unit Tests with utPLSQL v3+

### Compatibility

This plugin is compatible with the Java-API 3.1.0.

### Prerequisites
You have to be a fully utPLSQL environment available compatible with the Java API.


### Plugin Parameters 

* `dbUrl`
  * URL of the Connection to the database
* `dbUser`
  * Credential of the connection to the database
* `dbPass`
  * Password of the connection to the database
* `ignoreFailure`
  * Ignore or continue when a test fail
  * Default: `${maven.test.failure.ignore}`
* `skipCompatibilityCheck`
  * Skip the Compatibility Checks
  * `true` | `false` (default: `false`)
* `reporters`
  * List of the Reporters (Check the example below).
  * You can pass the name of the reporter and/or the output file of the reporter and/or if the report is logged to the console
  * This is a current list of the names of the reporters (See UtPLSQL documentation in order to check the final list): `UT_DOCUMENTATION_REPORTER`, `UT_COVERAGE_HTML_REPORTER`, `UT_TEAMCITY_REPORTER`, `UT_XUNIT_REPORTER`, `UT_COVERALLS_REPORTER`, `UT_COVERAGE_SONAR_REPORTER`,  `UT_SONAR_TEST_REPORTER`
  * Check the example below 
  
* `paths`
  * Paths of the resources
  
* `sources`
  * Path to project source files
* `sourcesOwner`
  * Owner of the tested code
* `sourcesRegexExpression`
  * utPLSQL will convert file paths into database objects using the following regular expression
* `sourcesOwnerSubexpression`
  * Object owner is identified by the expression with the specified set of brackets
* `sourcesNameSubexpression`
  * Object name is identified by the expression with the specified set of brackets    
* `sourcesTypeSubexpression`
  * Object Type is identified by the expression with the specified set of brackets
* `sourcesCustomTypeMapping`
  * List of Custom Type Mappings 
   
* `tests`
  * Path to project test files
* `testsOwner`
  * Owner of the testing code
* `testsRegexExpression`
  * utPLSQL will convert file paths into database objects using the following regular expression
* `testsOwnerSubexpression`
  * Owner is identified by the expression with the specified set of brackets
* `testsNameSubexpression`
  * Object name is identified by the expression with the specified set of brackets  
* `testsTypeSubexpression`
  * Object Type is identified by the expression with the specified set of brackets
* `testsCustomTypeMapping`
  * List of Custom Type Mappings


### Comparaison with the CLI

| CLI short parameter | CLI long parameter | maven XML path |
| --- | --- | --- |
| -c | --color | |
| | --failure-exit-code | |
| | | ignoreFailure |
| -f | --format | reporters.reporter.name |
| -o | | reporters.reporter.fileOutput |
| -s | | reporters.reporter.consoleOutput |
| -p | --path | paths.path |
| -scc | --skip-compatibility-check | skipCompatibilityCheck |
| -exclude | | excludeObject |
| -include | | includeObject |
| | | |
| -source_path | | sources.source.directory |
| -owner | | sourcesOwner |
| -regex_expression | | sourcesRegexExpression |
| -type_mapping | | list of testsCustomTypeMapping.customTypeMapping |
| -owner_subexpression | | sourcesOwnerSubexpression |
| -type_subexpression | | sourcesTypeSubexpression |
| -name_subexpression | | sourcesNameSubexpression |
| | | |
| -test_path | | tests.test.directory |
| -owner | | testsOwner |
| -regex_expression | | testsRegexExpression |
| -type_mapping | | list of testsCustomTypeMapping.customTypeMapping |
| -owner_subexpression | | testsOwnerSubexpression |
| -type_subexpression | | testsTypeSubexpression |
| -name_subexpression | | testsNameSubexpression |

### Sample of use
The next snippet is a sample of declaration of the pom
```xml
	<plugin>
		<groupId>org.utplsql</groupId>
		<artifactId>utplsql-maven-plugin</artifactId>
		<version>3.1.0</version>
		<goals>
			<goal>test</goal>
		</goals>
		<configuration>
			<dbUrl>url_of_connection</dbUrl>
			<dbUser>user</dbUser>
			<dbPass>password</dbPass>
			<ignoreFailure>false</ignoreFailure>
			<paths>
				<path>schema_name</path>
			</paths>
			<reporters>
				<reporter>
					<name>UT_COVERAGE_SONAR_REPORTER</name>
					<fileOutput>utplsql/coverage-sonar-reporter.xml</fileOutput>
					<consoleOutput>true</consoleOutput>
				</reporter>
				<reporter>
					<name>UT_SONAR_TEST_REPORTER</name>
					<fileOutput>utplsql/sonar-test-reporter.xml</fileOutput>
					<consoleOutput>false</consoleOutput>
				</reporter>
				<reporter>
					<name>UT_TEAMCITY_REPORTER</name>
				</reporter>
			</reporters>
			<sources>
				<source>
					<directory>src/test/resources/scripts/sources</directory>
					<includes>
						<include>**/*pkg</include>
						<include>**/*pkb</include>
					</includes>
				</source>
			</sources>
			<tests>
				<test>
					<directory>src/test/resources/scripts/test</directory>
					<includes>
						<include>**/*pkg</include>
						<include>**/*pkb</include>
					</includes>
				</test>
			</tests>                  
		</configuration>
	</plugin>
```

More project samples are available in the src/test/resources directory :
* simple-project : minimalist test project with standard project directory structure
* regex-project : override project directory structure and use additional parameters (sourcesRegexExpression, testsRegexExpression, ...) to tell utPLSQL how the project files are to be mapped into database objects.
* type-mapping-project : This project shows how to use regex and custom type parameters togethers.
* owner-param-project : This project demonstrates how to use sourcesOwner and testsOwner parameters.


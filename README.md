# utPLSQL-maven-plugin
A maven plugin for running Unit Tests with utPLSQL v3+

### Compatibility

This plugin is compatible with the Java-API 3.1.0.

### Prerequisites
You have to be a fully utPLSQL environment available compatible with the Java API.


### Plugin Parameters 

* `url`
  * URL of the Connection to the database
  * Default: `${dbURL}`
* `user`
  * Credential of the connection to the database
  * Default: `${dbUser}`
* `password`
  * Password of the connection to the database
  * Default: `${dbPass}`
* `version`
  * Version of the  utplsql
  * Default: `3.1.0`
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
  * Sources of the scripts at the style of the maven resources
* `tests`
  * Test fo the scripts at the style of the maven resources
* `targetDir`
  * Target dir, this is a readonly property
  * Default: `${project.build.directory}`
* `includeObject`
  * Include Object
* `excludeObject`
  * Exclude Objects



### Sample of use
The next snippet is a sample of declaration of the pom
```xml
		<plugin>
		<groupId>org.utplsql</groupId>
		<artifactId>utplsql-maven-plugin</artifactId>
		<version>1.0.0-SNAPSHOT</version>
        <goals>
            <goal>test</goal>
        </goals>
        <configuration>
			<url>url_of_connection</url>
			<user>user</user>
			<password>password</password>
			<version>3.1.0</version>
			<failOnErrors>false</failOnErrors>
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

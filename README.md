# utPLSQL-maven-plugin
A maven plugin for running Unit Tests with utPLSQL v3+


### Configuration

* `url`
  * URL of the Connection to the database
  * Default: `jdbc:oracle:thin:@localhost:1521:ut3`
* `user`
  * Credential of the connection to the database
  * Default: `ut3`
* `password`
  * Password of the connection to the database
  * Default: `XNtxj8eEgA6X6b6f`
* `ignoreFailure`
  * Ignore or continue when a test fail
  * Default: `${maven.test.failure.ignore}`
* `colorConsole`
  * Colors in the console
  * `true` | `false` (default: `false`)
* `skipCompatibilityCheck`
  * Skip the Compatibility Checks
  * `true` | `false` (default: `false`)
* `reporters`
  * List of the Reporters
  * Enumeration : `UT_DOCUMENTATION_REPORTER`, `UT_COVERAGE_HTML_REPORTER`, `UT_TEAMCITY_REPORTER`, `UT_XUNIT_REPORTER`, `UT_COVERALLS_REPORTER`, `UT_COVERAGE_SONAR_REPORTER`,  `UT_SONAR_TEST_REPORTER` 
  
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



### Example
```xml
	<plugin>
		<groupId>${pom.groupId}</groupId>
		<artifactId>utplsql-maven-plugin</artifactId>
		<version>${pom.version}</version>
        <goals>
            <goal>test</goal>
        </goals>
        <configuration>
                	<!--  Mandatory Attributes -->
			<url>url_of_connection</url>
			<user>user</user>
			<password>password</password>
                	
			<failOnErrors>false</failOnErrors>
			<colorConsole>true</colorConsole>
					
			<reporters>
				<reporter>UT_COVERAGE_SONAR_REPORTER</reporter>
				<reporter>UT_SONAR_TEST_REPORTER</reporter>
			</reporters>
			<sources>
				<source>
					<directory>SampleDirectory</directory>
					<includes>
						<include>**/*pkg</include>
						<include>**/*pkb</include>
					</includes>
            	</source>
				<source>
					<directory>SampleDirectory2</directory>
					<includes>
						<include>**/*pkb</include>
					</includes>
				</source>
			</sources>
			
			<tests>
				<test>
					<directory>src/main/test/test1</directory>
					<includes>
						<include>**/*pkg</include>
					</includes>
				</test>
			</tests>
                    
        </configuration>
      </plugin>
```

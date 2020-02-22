[![Build status](https://travis-ci.org/utPLSQL/utPLSQL-maven-plugin.svg?branch=develop)](https://travis-ci.org/utPLSQL/utPLSQL-maven-plugin)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.utplsql%3Autplsql-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.utplsql%3Autplsql-maven-plugin)

# utPLSQL-maven-plugin
* A maven plugin for running Unit Tests with utPLSQL v3+.

### Compatibility
* This plugin is compatible with utPLSQL 3.1.0+.

### Prerequisites
* Java SE Runtime Environment 8
* Maven Version 3.5+

The plugin requires Oracle JDBC driver (ojdbc8) as a maven dependency. 
In order to download the required driver, you need to configure access to Oracle's Maven Repository in your `settings.xml` file.

http://docs.oracle.com/middleware/1213/core/MAVEN/config_maven_repo.htm#MAVEN9010

Sections 6.1 and 6.5 are the more important ones, and the only ones you need if you're using the latest Maven version.

Below is an example of changes needed to your `settings.xml` file.

```xml
<settings>
 <servers>
    <server>
        <id>maven.oracle.com</id>
        <username>oracle_tech_net_user_name</username>
        <password>oracle_tech_net_password</password>
        <configuration>
            <basicAuthScope>
                <host>ANY</host>
                <port>ANY</port>
                <realm>OAM 11g</realm>
            </basicAuthScope>
            <httpConfiguration>
                <all>
                    <params>
                        <property>
                        <name>http.protocol.allow-circular-redirects</name>
                        <value>%b,true</value>
                        </property>
                    </params>
                </all>
            </httpConfiguration>
        </configuration>
    </server>
 </servers>
</settings>
```

### Usage

Please refer to the following usage example for the parameters descriptions.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.my_org</groupId>
    <artifactId>my-artifact-name</artifactId>
    <version>1.0.0</version>

    <properties>
        <!-- URL of the connection to the database. -->
        <!-- Can also be passed as execution parameters. -->
        <!-- -DdbUrl="jdbc:oracle:thin:@127.0.0.1:1521:xe" -->
        <dbUrl>url_of_connection</dbUrl>
        <!-- Database connection user. -->
        <!-- -DdbUser="user" -->
        <dbUser>user</dbUser>
        <!-- Database connection password. -->
        <!-- -DdbPassword="password" -->
        <dbPass>password</dbPass>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.utplsql</groupId>
                <artifactId>utplsql-maven-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>test</goal>
                        </goals>
                        <configuration>
                        
                            <!-- REQUIRED PARAMETERS -->
                            
                            <!-- A list of tests suite paths. -->
                            <!-- The path(s) can be in one of the following formats: -->
                            <!--     schema[.package[.procedure]] -->
                            <!--     schema:suite[.suite[.suite][...]][.procedure] -->
                            <!-- Both formats can be mixed in the list. -->
                            <!-- If only schema is provided, then all suites -->
                            <!-- owned by that schema are executed. -->
                            <paths>
                                <path>schema_name</path>
                            </paths>

                            <!-- A list of tags to run. -->
                            <tags>
                                <tag>test_tag</tag>
                            </tags>

                            <sources>
                                <source>
                                    <!-- Path to project source files. -->
                                    <directory>src/test/resources/scripts/sources</directory>
                                    <!-- File patterns to include. -->
                                    <includes>
                                        <include>**/*pkg</include>
                                        <include>**/*pkb</include>
                                    </includes>
                                </source>
                            </sources>

                            <tests>
                                <test>                                
                                    <!-- Path to project test files. -->
                                    <directory>src/test/resources/scripts/test</directory>
                                    <!-- File patterns to include. -->
                                    <includes>
                                        <include>**/*pkg</include>
                                        <include>**/*pkb</include>
                                    </includes>
                                </test>
                            </tests>
                                                        
                            <!-- OPTIONAL PARAMETERS -->
                        
                            <!-- Continue in case of failure. -->
                            <!-- Defaults to: ${maven.test.failure.ignore} -->
                            <ignoreFailure>false</ignoreFailure>
                            
                            <!-- Skip the utPLSQL version compatibility check. -->
                            <!-- Defaults to: false -->
                            <skipCompatibilityCheck>false</skipCompatibilityCheck>
                            
                            <!-- Comma-separated object list to include in the coverage report. -->
                            <!-- Format: [schema.]package[,[schema.]package ...]. -->
                            <!-- See coverage reporting options in framework documentation. -->
                            <includeObject>app.test1,app.test2</includeObject>
                            
                            <!-- Comma-separated object list to exclude from the coverage report. -->
                            <!-- Format: [schema.]package[,[schema.]package ...]. -->
                            <!-- See coverage reporting options in framework documentation. -->
                            <excludeObject>app.test1,app.test2</excludeObject>
                            
                            <!-- List of reporters. -->
                            <!-- You can pass the name of the reporter and/or the output file -->
                            <!-- of the reporter and/or if the report is logged to the console. -->
                            <!-- If you don't pass any reporter, UT_DOCUMENTATION_REPORTER will be used. -->
                            <!-- This is a current list of the names of the reporters, -->
                            <!-- see UtPLSQL documentation in order to check the final list: -->
                            <!--     UT_DOCUMENTATION_REPORTER -->
                            <!--     UT_COVERAGE_HTML_REPORTER -->
                            <!--     UT_TEAMCITY_REPORTER -->
                            <!--     UT_XUNIT_REPORTER -->
                            <!--     UT_COVERALLS_REPORTER -->
                            <!--     UT_COVERAGE_SONAR_REPORTER -->
                            <!--     UT_SONAR_TEST_REPORTER -->
                            <reporters>
                                <reporter>
                                    <name>UT_COVERAGE_SONAR_REPORTER</name>
                                    <!-- The file output path. -->
                                    <fileOutput>utplsql/coverage-sonar-reporter.xml</fileOutput>
                                    <!-- Indicates if should write to console. -->
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
                            
                            <!-- Custom source code mapping options. -->
                            <!-- See coverage reporting options in framework documentation. -->
                            <sourcesOwner>code_owner</sourcesOwner>
                            <sourcesRegexExpression>.*/\w+/(\w+)/(\w+)\.\w{3}</sourcesRegexExpression>
                            <sourcesOwnerSubexpression>app</sourcesOwnerSubexpression>
                            <sourcesNameSubexpression>2</sourcesNameSubexpression>
                            <sourcesTypeSubexpression>1</sourcesTypeSubexpression>
                            <sourcesCustomTypeMapping>
                                <customTypeMapping>
                                    <type>package body</type>
                                    <customMapping>package_bodies</customMapping>
                                </customTypeMapping>
                            </sourcesCustomTypeMapping>
                            
                            <!-- Custom test code mapping options. -->
                            <!-- See coverage reporting options in framework documentation. -->
                            <testsOwner>tests_owner</testsOwner>
                            <testsRegexExpression>.*/\w+/(\w+)/(\w+)\.\w{3}</testsRegexExpression>
                            <testsOwnerSubexpression>test</testsOwnerSubexpression>
                            <testsNameSubexpression>2</testsNameSubexpression>
                            <testsTypeSubexpression>1</testsTypeSubexpression>
                            <testsCustomTypeMapping>
                                <customTypeMapping>
                                    <type>package body</type>
                                    <customMapping>package_bodies</customMapping>
                                </customTypeMapping>
                            </testsCustomTypeMapping>

                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

More project samples are available in the src/test/resources directory:
* **simple-project:** minimalist test project with standard project directory structure.
* **regex-project:** overrides project directory structure and use additional parameters (sourcesRegexExpression, testsRegexExpression, ...), to tell utPLSQL how project files should be mapped into database objects.
* **type-mapping-project:** this project shows how to use regex and custom type parameters together.
* **owner-param-project:** this project demonstrates how to use sourcesOwner and testsOwner parameters.

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
| | --tags | tags.tag |
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

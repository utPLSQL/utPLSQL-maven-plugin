[![Build status](https://github.com/utPLSQL/utPLSQL-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/utPLSQL/utPLSQL-maven-plugin/actions/workflows/build.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.utplsql%3Autplsql-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.utplsql%3Autplsql-maven-plugin)

# utPLSQL-maven-plugin

A maven plugin for running Unit Tests with utPLSQL v3+.

## Compatibility

This plugin is compatible with utPLSQL 3.1.0+.

## Prerequisites

* Java SE Runtime Environment 8
* Maven Version 3.5+
* Oracle JDBC driver

```xml

<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.3.0.0</version>
</dependency>
```

## Usage

### Skipping Tests

To skip running the tests for a particular project, set the **skipUtplsqlTests** property to true.

```xml

<configuration>
    <skipUtplsqlTests>true</skipUtplsqlTests>
</configuration>
```

You can also skip the tests via the command line by executing the following command:

    mvn install -DskipTests

#### Skipping by Default

If you want to skip tests by default but want the ability to re-enable tests from the command line, you need to go via a
properties section in the pom:

```xml

<configuration>
    <skipUtplsqlTests>true</skipUtplsqlTests>
</configuration>
```

This will allow you to run with all tests disabled by default and to run them with this command:

    mvn install -DskipUtplsqlTests=false

### Configuration

Please refer to the following usage example for the parameters descriptions:

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
                <version>3.1.5</version>
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

                            <!-- Skip the tests -->
                            <!-- Defaults to: false -->
                            <skipUtplsqlTests>false</skipUtplsqlTests>

                            <!-- Enables DBMS_OUTPUT -->
                            <!-- Defaults to: false -->
                            <dbmsOutput>false</dbmsOutput>

                            <!-- A list of tags to run. -->
                            <tags>
                                <tag>test_tag</tag>
                            </tags>

                            <!-- Enables random order of test executions. -->
                            <randomTestOrder>true</randomTestOrder>
                            <!-- Sets the seed to use for random test execution order. If set, it sets -random to true. -->
                            <randomTestOrderSeed>5</randomTestOrderSeed>

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

More project samples are available in the `src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT`
directory:

* **simple-project:** Minimalist test project with standard project directory structure.
* **regex-project:** Overrides project directory structure and use additional parameters (sourcesRegexExpression,
  testsRegexExpression, ...), to tell utPLSQL how project files should be mapped into database objects.
* **type-mapping-project:** Example how to use regex and custom type parameters together.
* **owner-param-project:** Demonstrates how to use sourcesOwner and testsOwner parameters.

## Comparison with utPLSQL CLI

| CLI short parameter | CLI long parameter | Maven XML path |
| --- | --- | --- |
| -c | --color | |
| | --failure-exit-code | |
| -p | --path | paths.path |
| -f | --format | reporters.reporter.name |
| -o | | reporters.reporter.fileOutput |
| -s | | reporters.reporter.consoleOutput |
| | | ignoreFailure |
| -scc | --skip-compatibility-check | skipCompatibilityCheck |
| | --tags | tags.tag |
| -D | --dbms_output | dbmsOutput |
| -r | --random-test-order | randomTestOrder |
| -seed | --random-test-order-seed | randomTestOrderSeed |
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

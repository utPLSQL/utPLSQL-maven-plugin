[![latest-release](https://img.shields.io/github/release/utPLSQL/utPLSQL-maven-plugin.svg)](https://github.com/utPLSQL/utPLSQL-maven-plugin/releases)
[![license](https://img.shields.io/github/license/utPLSQL/utPLSQL-maven-plugin.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://github.com/utPLSQL/utPLSQL-maven-plugin/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/utPLSQL/utPLSQL-maven-plugin/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=utPLSQL_utPLSQL-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=utPLSQL_utPLSQL-maven-plugin)

# utPLSQL-maven-plugin

A Maven plugin that runs [utPLSQL](https://www.utplsql.org/) v3 unit tests in an Oracle database as part of the Maven
`test` phase, and writes test results and code coverage reports that CI servers and tools such as SonarQube can read.

For writing tests, reporters and code coverage, see the [utPLSQL documentation](https://www.utplsql.org/utPLSQL/latest/).

## Compatibility

The plugin works with utPLSQL 3.1.0 or newer installed in the database.

## Prerequisites

* Java 17 or newer
* Maven 3.9.9 or newer

## Quick start

Add the plugin to your `pom.xml` and provide the database connection:

```xml
<properties>
    <dbUrl>jdbc:oracle:thin:@//localhost:1521/FREEPDB1</dbUrl>
    <dbUser>app</dbUser>
    <dbPass>app_password</dbPass>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.utplsql</groupId>
            <artifactId>utplsql-maven-plugin</artifactId>
            <version>3.2.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>test</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Then run:

```bash
mvn test
```

With no further configuration, the plugin:

* runs all test suites in the schema of the connected user,
* maps source files from `src/main/plsql` (`**/*.*`) and test files from `src/test/plsql` (`**/*.pkg`) to database
  objects for coverage reporting,
* prints results to the console with `UT_DOCUMENTATION_REPORTER`,
* fails the build when any test fails or errors.

## Database connection

The connection is configured with three properties. Each can be set in the `pom.xml` or passed on the command line:

| Property | Description                                                                  | Example                                     |
|----------|------------------------------------------------------------------------------|---------------------------------------------|
| `dbUrl`  | JDBC URL of the database                                                     | `-DdbUrl=jdbc:oracle:thin:@//host:1521/svc` |
| `dbUser` | Database user; leave unset when using an [Oracle Wallet](#oracle-wallet-secure-external-password-store) | `-DdbUser=app`                              |
| `dbPass` | Password of `dbUser`                                                         | `-DdbPass=app_password`                     |

```bash
mvn test -DdbUrl=jdbc:oracle:thin:@//localhost:1521/FREEPDB1 -DdbUser=app -DdbPass=app_password
```

### Oracle Wallet (Secure External Password Store)

To keep the database password out of the `pom.xml` and the command line, store the credentials in an Oracle Wallet,
leave `dbUser` and `dbPass` unset, and point `dbUrl` to the TNS alias of the stored credential:

```xml
<properties>
    <dbUrl>jdbc:oracle:thin:@MYDATABASE</dbUrl>
</properties>
```

Setup example:

```bash
# create an auto-login wallet with credentials for TNS alias MYDATABASE
orapki wallet create -wallet $HOME/oracle/wallet -auto_login_local
mkstore -wrl $HOME/oracle/wallet -createCredential MYDATABASE someusername

# point the JDBC driver to the wallet
echo "oracle.net.wallet_location=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=$HOME/oracle/wallet)))" \
  > $HOME/oracle/network/admin/ojdbc.properties

# tnsnames.ora with the MYDATABASE entry must be in the same directory
export TNS_ADMIN=$HOME/oracle/network/admin
```

The JDBC driver looks for `tnsnames.ora` and `ojdbc.properties` in the directory given by, in order of precedence:

1. the `TNS_ADMIN` parameter in the URL, e.g. `jdbc:oracle:thin:@MYDATABASE?TNS_ADMIN=/path/to/network/admin`
2. the Java system property `oracle.net.tns_admin`, e.g. `export MAVEN_OPTS="-Doracle.net.tns_admin=/path/to/network/admin"`
3. the `TNS_ADMIN` environment variable

The TNS alias used in `dbUrl` must match the alias of the credential stored in the wallet.

## Selecting tests

By default, all suites in the schema of the connected user are run. Use `paths` to run specific schemas, suites,
packages or procedures, and `tags` to run only tests with the given tags:

```xml
<configuration>
    <paths>
        <path>app</path>
        <path>app:com.my_org.my_project</path>
    </paths>
    <tags>
        <tag>fast</tag>
    </tags>
</configuration>
```

A path has one of the formats `schema[.package[.procedure]]` or `schema:suite[.suite[.suite][...]][.procedure]`,
and both formats can be mixed. See [Running tests](https://www.utplsql.org/utPLSQL/latest/userguide/running-unit-tests.html)
and [Run by tags](https://www.utplsql.org/utPLSQL/latest/userguide/running-unit-tests.html#run-by-tags) in the utPLSQL
documentation.

To detect hidden dependencies between tests, run them in
[random order](https://www.utplsql.org/utPLSQL/latest/userguide/running-unit-tests.html#random-order) with
`randomTestOrder`. Set `randomTestOrderSeed` to repeat a particular order.

## Reporters

Reporters decide the format of the results. Each reporter writes to a file, to the console, or both:

```xml
<configuration>
    <reporters>
        <reporter>
            <name>UT_DOCUMENTATION_REPORTER</name>
        </reporter>
        <reporter>
            <name>UT_SONAR_TEST_REPORTER</name>
            <fileOutput>utplsql/sonar-test-report.xml</fileOutput>
        </reporter>
        <reporter>
            <name>UT_COVERAGE_SONAR_REPORTER</name>
            <fileOutput>utplsql/coverage-sonar-report.xml</fileOutput>
            <consoleOutput>true</consoleOutput>
        </reporter>
    </reporters>
</configuration>
```

* `name` is the name of a utPLSQL reporter (case-insensitive). Custom reporters installed in the database can be used
  as well.
* `fileOutput` is the report file. Relative paths are resolved against the build directory (`target` by default).
* `consoleOutput` prints the report to the console. It defaults to `true` when no `fileOutput` is given, and to `false`
  otherwise.
* Without any `reporters`, `UT_DOCUMENTATION_REPORTER` prints to the console.

Reporters provided by utPLSQL (see [Reporters](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html) for
details and the reporters available in your version):

| Reporter                         | Output                                                                                                 |
|----------------------------------|--------------------------------------------------------------------------------------------------------|
| `UT_DOCUMENTATION_REPORTER`      | [Human-readable test results](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#documentation-reporter) |
| `UT_JUNIT_REPORTER`              | [JUnit XML](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#junit-reporter)                |
| `UT_TFS_JUNIT_REPORTER`          | [JUnit XML for TFS / Azure DevOps](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#tfs-vsts-reporter) |
| `UT_TEAMCITY_REPORTER`           | [TeamCity service messages](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#teamcity-reporter) |
| `UT_SONAR_TEST_REPORTER`         | [SonarQube generic test execution](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#sonar-test-reporter) |
| `UT_TAP_REPORTER`                | [Test Anything Protocol](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#tap-reporter)     |
| `UT_DEBUG_REPORTER`              | [Diagnostic output of the test run](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#debug-reporter) |
| `UT_COVERAGE_HTML_REPORTER`      | [Code coverage as HTML](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#coverage-reporters) |
| `UT_COVERAGE_SONAR_REPORTER`     | [Code coverage for SonarQube](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#coverage-reporters) |
| `UT_COVERAGE_COBERTURA_REPORTER` | [Code coverage in Cobertura format](https://www.utplsql.org/utPLSQL/latest/userguide/reporters.html#coverage-reporters) |

## Code coverage

Code coverage is gathered whenever a coverage reporter is configured. How utPLSQL gathers and reports coverage is
described in [Coverage](https://www.utplsql.org/utPLSQL/latest/userguide/coverage.html).

### Choosing the objects to report

| Parameter           | Description                                                                          |
|---------------------|--------------------------------------------------------------------------------------|
| `includeObject`     | Comma-separated list of objects to include, format `[schema.]object[,[schema.]object ...]` |
| `excludeObject`     | Comma-separated list of objects to exclude, same format as `includeObject`           |
| `includeSchemaExpr` | Regular expression for the names of schemas to include                               |
| `excludeSchemaExpr` | Regular expression for the names of schemas to exclude                               |
| `includeObjectExpr` | Regular expression for the names of objects to include                               |
| `excludeObjectExpr` | Regular expression for the names of objects to exclude                               |

The regular expression filters need a utPLSQL version that supports them. See
[Coverage reporting options](https://www.utplsql.org/utPLSQL/latest/userguide/coverage.html#coverage-reporting-options).

### Mapping project files to database objects

For [project based coverage](https://www.utplsql.org/utPLSQL/latest/userguide/coverage.html#project-based-coverage),
utPLSQL reports coverage per source file instead of per database object. The plugin passes the files found by
`sources` and `tests` to utPLSQL, which maps each file to a database object by its path:

* `sources` / `tests` select the files (`directory` and `includes`). They default to `src/main/plsql` with `**/*.*` and
  `src/test/plsql` with `**/*.pkg`.
* `sourcesOwner` / `testsOwner` set the schema owning the objects.
* `sourcesRegexExpression`, `sourcesOwnerSubexpression`, `sourcesNameSubexpression` and `sourcesTypeSubexpression`
  (and their `tests...` counterparts) describe how the owner, name and type are read from the file path.
* `sourcesCustomTypeMapping` / `testsCustomTypeMapping` map directory names or file extensions to object types.

## Skipping tests

Set `skipUtplsqlTests` to `true` in the plugin configuration or on the command line:

```bash
mvn install -DskipUtplsqlTests=true
```

Maven's `-DskipTests` does not skip utPLSQL tests.

To skip the tests by default and enable them only when needed, set the property in the `pom.xml`:

```xml
<properties>
    <skipUtplsqlTests>true</skipUtplsqlTests>
</properties>
```

and override it on the command line:

```bash
mvn install -DskipUtplsqlTests=false
```

To run all tests but not fail the build on test failures, set `ignoreFailure` to `true`, or pass Maven's standard
`-Dmaven.test.failure.ignore=true`.

## Configuration reference

All parameters are optional:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.my_org</groupId>
    <artifactId>my-artifact-name</artifactId>
    <version>1.0.0</version>

    <properties>
        <!-- JDBC URL of the database. Command line: -DdbUrl=... -->
        <dbUrl>jdbc:oracle:thin:@//localhost:1521/FREEPDB1</dbUrl>
        <!-- Database user. Leave unset when using an Oracle Wallet. Command line: -DdbUser=... -->
        <dbUser>app</dbUser>
        <!-- Password of dbUser. Command line: -DdbPass=... -->
        <dbPass>app_password</dbPass>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.utplsql</groupId>
                <artifactId>utplsql-maven-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>test</goal>
                        </goals>
                        <configuration>
                            <!-- Suites to run, in the format schema[.package[.procedure]] -->
                            <!-- or schema:suite[.suite[.suite][...]][.procedure]. -->
                            <!-- Default: all suites in the schema of the connected user. -->
                            <paths>
                                <path>app</path>
                            </paths>

                            <!-- Run only tests with these tags. -->
                            <tags>
                                <tag>fast</tag>
                            </tags>

                            <!-- Run tests in random order, optionally with a fixed seed. -->
                            <!-- Setting randomTestOrderSeed also enables randomTestOrder. -->
                            <!-- Default: false, no seed. -->
                            <randomTestOrder>true</randomTestOrder>
                            <randomTestOrderSeed>5</randomTestOrderSeed>

                            <!-- Do not fail the build when tests fail. -->
                            <!-- Default: ${maven.test.failure.ignore} -->
                            <ignoreFailure>false</ignoreFailure>

                            <!-- Skip the tests. Command line: -DskipUtplsqlTests=true -->
                            <!-- Default: false -->
                            <skipUtplsqlTests>false</skipUtplsqlTests>

                            <!-- Skip the check of compatibility with the utPLSQL version in the database. -->
                            <!-- Default: false -->
                            <skipCompatibilityCheck>false</skipCompatibilityCheck>

                            <!-- Print DBMS_OUTPUT of the tests. -->
                            <!-- Default: false -->
                            <dbmsOutput>false</dbmsOutput>

                            <!-- Timeout in seconds around reporter creation, retrying when the database is not ready. -->
                            <!-- Default: 0 (no timeout) -->
                            <oraStuckTimeout>0</oraStuckTimeout>

                            <!-- Reporters and their output. -->
                            <!-- Default: UT_DOCUMENTATION_REPORTER to the console. -->
                            <reporters>
                                <reporter>
                                    <name>UT_DOCUMENTATION_REPORTER</name>
                                </reporter>
                                <reporter>
                                    <name>UT_SONAR_TEST_REPORTER</name>
                                    <!-- Relative paths are resolved against the build directory. -->
                                    <fileOutput>utplsql/sonar-test-report.xml</fileOutput>
                                    <!-- Default: true without fileOutput, false with fileOutput. -->
                                    <consoleOutput>false</consoleOutput>
                                </reporter>
                                <reporter>
                                    <name>UT_COVERAGE_SONAR_REPORTER</name>
                                    <fileOutput>utplsql/coverage-sonar-report.xml</fileOutput>
                                </reporter>
                            </reporters>

                            <!-- Objects to include in / exclude from the coverage report. -->
                            <!-- Format: [schema.]object[,[schema.]object ...] -->
                            <includeObject>app.pkg_orders,app.pkg_customers</includeObject>
                            <excludeObject>app.pkg_logging</excludeObject>

                            <!-- Regular expressions for schema and object names to include in / exclude from -->
                            <!-- the coverage report. -->
                            <includeSchemaExpr>^APP$</includeSchemaExpr>
                            <excludeSchemaExpr>^APP_TEST$</excludeSchemaExpr>
                            <includeObjectExpr>^PKG_</includeObjectExpr>
                            <excludeObjectExpr>_TMP$</excludeObjectExpr>

                            <!-- Source files, mapped to database objects for coverage reporting. -->
                            <!-- Default: src/main/plsql, **/*.* -->
                            <sources>
                                <source>
                                    <directory>src/main/plsql</directory>
                                    <includes>
                                        <include>**/*.pks</include>
                                        <include>**/*.pkb</include>
                                    </includes>
                                </source>
                            </sources>

                            <!-- How owner, type and name of the database object are read from a source file path, -->
                            <!-- e.g. src/main/plsql/app/package_bodies/pkg_orders.pkb is package body APP.PKG_ORDERS. -->
                            <!-- For one owner of all source files, set sourcesOwner instead of sourcesOwnerSubexpression. -->
                            <sourcesRegexExpression>.*/(\w+)/(\w+)/(\w+)\.\w{3}</sourcesRegexExpression>
                            <sourcesOwnerSubexpression>1</sourcesOwnerSubexpression>
                            <sourcesTypeSubexpression>2</sourcesTypeSubexpression>
                            <sourcesNameSubexpression>3</sourcesNameSubexpression>
                            <sourcesCustomTypeMapping>
                                <customTypeMapping>
                                    <type>package body</type>
                                    <customMapping>package_bodies</customMapping>
                                </customTypeMapping>
                            </sourcesCustomTypeMapping>

                            <!-- Test files, mapped the same way as source files. -->
                            <!-- Default: src/test/plsql, **/*.pkg -->
                            <tests>
                                <test>
                                    <directory>src/test/plsql</directory>
                                    <includes>
                                        <include>**/*.pks</include>
                                        <include>**/*.pkb</include>
                                    </includes>
                                </test>
                            </tests>

                            <!-- For one owner of all test files, set testsOwner instead of testsOwnerSubexpression. -->
                            <testsRegexExpression>.*/(\w+)/(\w+)/(\w+)\.\w{3}</testsRegexExpression>
                            <testsOwnerSubexpression>1</testsOwnerSubexpression>
                            <testsTypeSubexpression>2</testsTypeSubexpression>
                            <testsNameSubexpression>3</testsNameSubexpression>
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

## Sample projects

The plugin's integration tests double as examples, in
[`src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT):

* [`minimalist`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/minimalist): no plugin configuration,
  only the connection.
* [`simple`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/simple): standard project directory
  structure with Sonar reporters.
* [`regex`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/regex): custom directory structure, mapped
  to database objects with `sourcesRegexExpression`, `testsRegexExpression` and related parameters.
* [`type_mapping`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/type_mapping): regular expressions
  combined with custom type mappings.
* [`owner_param`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/owner_param): `sourcesOwner` and
  `testsOwner`.
* [`tags`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/tags): running tests by tag.
* [`include_object`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/include_object),
  [`exclude_object`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/exclude_object),
  [`include_object_expr`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/include_object_expr) and
  [`exclude_object_expr`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/exclude_object_expr):
  choosing the objects in the coverage report.
* [`ora_stuck_timeout`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/ora_stuck_timeout):
  `oraStuckTimeout`.
* [`skip`](src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/skip): `skipUtplsqlTests`.

## Comparison with utPLSQL-cli

[utPLSQL-cli](https://github.com/utPLSQL/utPLSQL-cli) runs the same tests from the command line. The table maps its
[`run` command options](https://github.com/utPLSQL/utPLSQL-cli#run) to the plugin configuration:

| utPLSQL-cli option                 | Maven configuration                                      |
|------------------------------------|----------------------------------------------------------|
| `<ConnectionURL>`                  | `dbUrl`, `dbUser`, `dbPass`                              |
| `-p`, `--path`                     | `paths.path`                                             |
| `--tags`                           | `tags.tag`                                               |
| `-f`, `--format`                   | `reporters.reporter.name`                                |
| `-o`                               | `reporters.reporter.fileOutput`                          |
| `-s`                               | `reporters.reporter.consoleOutput`                       |
| `-c`, `--color`                    | follows Maven's console color setting                    |
| `--failure-exit-code`              | not available; use `ignoreFailure` to not fail the build |
| `-scc`, `--skip-compatibility-check` | `skipCompatibilityCheck`                               |
| `-D`, `--dbms_output`              | `dbmsOutput`                                             |
| `-r`, `--random-test-order`        | `randomTestOrder`                                        |
| `-seed`, `--random-test-order-seed` | `randomTestOrderSeed`                                   |
| `--ora-stuck-timeout`              | `oraStuckTimeout`                                        |
| `-include`                         | `includeObject`                                          |
| `-exclude`                         | `excludeObject`                                          |
| not available                      | `includeSchemaExpr`, `excludeSchemaExpr`, `includeObjectExpr`, `excludeObjectExpr` |
| not available                      | `skipUtplsqlTests`                                       |
| `--coverage-schemes`               | not available                                            |
| `-t`, `--timeout`                  | not available                                            |
| `-source_path`                     | `sources.source.directory`                               |
| `-owner`                           | `sourcesOwner`                                           |
| `-regex_expression`                | `sourcesRegexExpression`                                 |
| `-type_mapping`                    | `sourcesCustomTypeMapping.customTypeMapping`             |
| `-owner_subexpression`             | `sourcesOwnerSubexpression`                              |
| `-type_subexpression`              | `sourcesTypeSubexpression`                               |
| `-name_subexpression`              | `sourcesNameSubexpression`                               |
| `-test_path`                       | `tests.test.directory`                                   |
| `-owner`                           | `testsOwner`                                             |
| `-regex_expression`                | `testsRegexExpression`                                   |
| `-type_mapping`                    | `testsCustomTypeMapping.customTypeMapping`               |
| `-owner_subexpression`             | `testsOwnerSubexpression`                                |
| `-type_subexpression`              | `testsTypeSubexpression`                                 |
| `-name_subexpression`              | `testsNameSubexpression`                                 |

In utPLSQL-cli, `-owner`, `-regex_expression` and the other mapping options apply to the preceding `-source_path` or
`-test_path`.

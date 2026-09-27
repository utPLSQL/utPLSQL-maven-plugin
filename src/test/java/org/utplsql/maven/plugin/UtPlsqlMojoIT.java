package org.utplsql.maven.plugin;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static org.utplsql.maven.plugin.ReportChecker.assertThatReportsAreGeneratedAsExpected;
import static org.utplsql.maven.plugin.ReportChecker.reportWasGenerated;

@MavenGoal("test")
@MavenJupiterExtension
class UtPlsqlMojoIT {

    @MavenTest
    void minimalist(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
    }

    @MavenTest
    void owner_param(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void regex(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void simple(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void skip(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThat(result.getMavenLog()).info().contains("utPLSQLTests are skipped.");

        assertThat(reportWasGenerated(result, "sonar-test-report.xml")).isFalse();
    }

    @MavenTest
    void tags(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml");
    }

    @MavenTest
    void type_mapping(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void exclude_object(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void include_object(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void ora_stuck_timeout(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void exclude_object_expr(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void include_object_expr(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThatReportsAreGeneratedAsExpected(result, "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    @SystemProperty(value = "skipTests", content = "true")
    void skip_tests(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThat(result.getMavenLog()).info().contains("utPLSQLTests are skipped.");
    }

    @MavenTest
    @SystemProperty(value = "maven.test.skip", content = "true")
    void maven_test_skip(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThat(result.getMavenLog()).info().contains("utPLSQLTests are skipped.");
    }

    @MavenTest
    @SystemProperty(value = "skipTests", content = "true")
    @SystemProperty(value = "skipUtplsqlTests", content = "false")
    void skip_tests_overridden_by_skip_utplsql_tests(MavenExecutionResult result) {
        assertThat(result).isSuccessful();

        assertThat(result.getMavenLog()).info().doesNotContain("utPLSQLTests are skipped.");
    }

}

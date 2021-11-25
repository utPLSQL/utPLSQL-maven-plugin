package org.utplsql.maven.plugin;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import java.io.IOException;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.utplsql.maven.plugin.ReportChecker.checkReports;
import static org.utplsql.maven.plugin.ReportChecker.reportExists;

@MavenJupiterExtension
public class UtPlsqlMojoIT {

    @MavenTest
    void minimalist(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
    }

    @MavenTest
    void owner_param(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        checkReports(this.getClass(), "owner_param", "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void regex(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        checkReports(this.getClass(), "regex", "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void simple(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        checkReports(this.getClass(), "simple", "sonar-test-report.xml", "coverage-sonar-report.xml");
    }

    @MavenTest
    void skip(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        assertFalse(reportExists(this.getClass(), "skip", "sonar-test-report.xml"));
    }

    @MavenTest
    void tags(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        checkReports(this.getClass(), "tags", "sonar-test-report.xml");
    }

    @MavenTest
    void type_mapping(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        checkReports(this.getClass(), "type_mapping", "sonar-test-report.xml", "coverage-sonar-report.xml");
    }
}

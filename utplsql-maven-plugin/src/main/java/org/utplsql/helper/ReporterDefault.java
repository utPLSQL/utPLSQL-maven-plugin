package org.utplsql.helper;

public enum ReporterDefault {

    UT_DOCUMENTATION_REPORTER(""),
    UT_COVERAGE_HTML_REPORTER("utplsql/coverage-html-reporter.html"),
    UT_TEAMCITY_REPORTER("utplsql/teamcity-reporter.txt"),
    UT_XUNIT_REPORTER("utplsql/xunit-reporter.xml"),
    UT_COVERALLS_REPORTER("utplsql/coveralls-reporter.json"),
    UT_COVERAGE_SONAR_REPORTER("utplsql/coverage-sonar-reporter.xml"),
    UT_SONAR_TEST_REPORTER("utplsql/sonar-test-reporter.xml");

    private String outputFile;

    private ReporterDefault(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }
}

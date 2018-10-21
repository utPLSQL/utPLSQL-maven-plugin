package org.utplsql.maven.plugin.helper;

import org.utplsql.api.reporter.Reporter;

/**
 * This class is an enumeration of all the known reporter in {@code utPLSQL}.
 * It further more defines the default output file for each {@link Reporter}.
 * In case the output file is set to {@code -}, it will mean the stdout of the
 * process.
 * 
 * @author Alberto Hernández
 */
public enum ReporterDefault {

    UT_DOCUMENTATION_REPORTER("-"),
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

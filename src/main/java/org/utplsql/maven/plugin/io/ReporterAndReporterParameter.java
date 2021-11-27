package org.utplsql.maven.plugin.io;

import org.utplsql.api.reporter.Reporter;
import org.utplsql.maven.plugin.model.ReporterParameter;

public class ReporterAndReporterParameter {

    private final Reporter reporter;
    private final ReporterParameter reporterParameter;

    public ReporterAndReporterParameter(Reporter reporter, ReporterParameter reporterParameter) {
        this.reporter = reporter;
        this.reporterParameter = reporterParameter;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public ReporterParameter getReporterParameter() {
        return reporterParameter;
    }
}

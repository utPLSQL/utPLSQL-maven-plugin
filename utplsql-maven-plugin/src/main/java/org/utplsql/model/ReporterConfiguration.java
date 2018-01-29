package org.utplsql.model;

public class ReporterConfiguration {

    private String outputFile;

    public ReporterConfiguration() {
        super();
    }

    public ReporterConfiguration(String outputFile) {
        super();
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

}

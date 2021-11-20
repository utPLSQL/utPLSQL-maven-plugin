package org.utplsql.maven.plugin.reporter;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.utplsql.api.Version;
import org.utplsql.api.outputBuffer.OutputBuffer;
import org.utplsql.api.outputBuffer.OutputBufferProvider;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class ReporterWriter {

    private final List<Pair<Reporter, ReporterParameter>> listReporters;

    private final String outputDirectory;

    private final Version databaseVersion;

    private final Log log;

    /**
     * Constructor of the reporter writer.
     *
     * @param outputDirectory the reporter output directory
     * @param databaseVersion the utPLSQL framework version
     * @param log             the Maven log
     */
    public ReporterWriter(String outputDirectory, Version databaseVersion, Log log) {
        this.listReporters = new ArrayList<>();
        this.outputDirectory = outputDirectory;
        this.databaseVersion = databaseVersion;
        this.log = log;
    }

    /**
     * Adds a new reporter to the writter.
     *
     * @param parameter the reporter parameter
     * @param reporter  the reporter Object
     */
    public void addReporter(ReporterParameter parameter, Reporter reporter) {
        listReporters.add(Pair.of(reporter, parameter));
    }

    /**
     * Writes the reporters to the output.
     *
     * @param connection the database connection
     * @throws MojoExecutionException if any exception happens
     */
    public void writeReporters(Connection connection) throws MojoExecutionException {
        for (Pair<Reporter, ReporterParameter> pair : listReporters) {
            writeReports(connection, pair.getLeft(), pair.getRight());
        }
    }

    private void writeReports(Connection connection, Reporter reporter, ReporterParameter reporterParameter)
            throws MojoExecutionException {
        List<PrintStream> printStreams = new ArrayList<>();
        FileOutputStream fout = null;

        //
        try {
            OutputBuffer buffer = OutputBufferProvider.getCompatibleOutputBuffer(databaseVersion, reporter, connection);

            if (reporterParameter.isFileOutput()) {

                File file = new File(reporterParameter.getFileOutput());
                if (!file.isAbsolute()) {
                    file = new File(outputDirectory, reporterParameter.getFileOutput());
                }

                if (!file.getParentFile().exists()) {
                    log.debug("Creating directory for reporter file " + file.getAbsolutePath());
                    file.getParentFile().mkdirs();
                }

                fout = new FileOutputStream(file);
                log.info(format("Writing report %s to %s", reporter.getTypeName(), file.getAbsolutePath()));

                // Added to the Report
                printStreams.add(new PrintStream(fout));
            }

            if (reporterParameter.isConsoleOutput()) {
                log.info(format("Writing report %s to Console", reporter.getTypeName()));
                printStreams.add(System.out);
            }
            buffer.printAvailable(connection, printStreams);
        } catch (Exception e) {
            throw new MojoExecutionException("Unexpected error opening file ouput ", e);
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    log.info(format("Failed to closing the reporting %s", reporterParameter.getClass()));
                }
            }
        }
    }
}

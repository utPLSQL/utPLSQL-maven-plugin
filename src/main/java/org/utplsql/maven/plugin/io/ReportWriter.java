package org.utplsql.maven.plugin.io;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.utplsql.api.Version;
import org.utplsql.api.outputBuffer.OutputBuffer;
import org.utplsql.api.outputBuffer.OutputBufferProvider;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.maven.plugin.model.ReporterParameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * Writes the reports
 *
 * @author Alberto Hernández
 * @author Simon Martinelli
 */
public class ReportWriter {

    private final List<Pair<Reporter, ReporterParameter>> reporters;

    private final String outputDirectory;

    private final Version databaseVersion;

    private final Log log;

    /**
     * Constructor of the reporter writer.
     *
     * @param outputDirectory the report output directory
     * @param databaseVersion the utPLSQL framework {@link Version}
     * @param log             the Maven log
     */
    public ReportWriter(String outputDirectory, Version databaseVersion, Log log) {
        this.reporters = new ArrayList<>();
        this.outputDirectory = outputDirectory;
        this.databaseVersion = databaseVersion;
        this.log = log;
    }

    /**
     * Adds a new reporter to the writer.
     *
     * @param parameter the {@link ReporterParameter}
     * @param reporter  the {@link Reporter}
     */
    public void addReporter(ReporterParameter parameter, Reporter reporter) {
        reporters.add(Pair.of(reporter, parameter));
    }

    /**
     * Writes the reports to the output.
     *
     * @param connection The database {@link Connection}
     * @throws MojoExecutionException if any exception happens
     */
    public void writeReports(Connection connection) throws MojoExecutionException {
        for (Pair<Reporter, ReporterParameter> pair : reporters) {
            writeReports(connection, pair.getLeft(), pair.getRight());
        }
    }

    public List<Pair<Reporter, ReporterParameter>> getReporters() {
        return reporters;
    }

    private void writeReports(Connection connection, Reporter reporter, ReporterParameter reporterParameter) throws MojoExecutionException {
        FileOutputStream fileOutputStream = null;
        try {
            OutputBuffer buffer = OutputBufferProvider.getCompatibleOutputBuffer(databaseVersion, reporter, connection);
            List<PrintStream> printStreams = new ArrayList<>();

            if (reporterParameter.isFileOutput()) {
                File file = new File(reporterParameter.getFileOutput());
                if (!file.isAbsolute()) {
                    file = new File(outputDirectory, reporterParameter.getFileOutput());
                }

                if (!file.getParentFile().exists()) {
                    log.debug("Creating directory for report file " + file.getAbsolutePath());
                    file.getParentFile().mkdirs();
                }

                fileOutputStream = new FileOutputStream(file);
                log.info(format("Writing report %s to %s", reporter.getTypeName(), file.getAbsolutePath()));

                printStreams.add(new PrintStream(fileOutputStream));
            }

            if (reporterParameter.isConsoleOutput()) {
                log.info(format("Writing report %s to Console", reporter.getTypeName()));
                printStreams.add(System.out);
            }

            buffer.printAvailable(connection, printStreams);

        } catch (FileNotFoundException | SQLException e) {
            throw new MojoExecutionException("Unexpected error opening file output ", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.info(format("Failed to closing the report %s", reporterParameter.getClass()));
                }
            }
        }
    }
}

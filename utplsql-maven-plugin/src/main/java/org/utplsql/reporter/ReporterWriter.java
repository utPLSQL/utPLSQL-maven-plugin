package org.utplsql.reporter;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.utplsql.api.OutputBuffer;
import org.utplsql.api.reporter.Reporter;
import org.utplsql.model.ReporterParameter;

public class ReporterWriter
{
	private static final Log LOG = new SystemStreamLog();

	// Reporter Parameters
	private List<Pair<Reporter, ReporterParameter>> listReporters;

	// Output Directory
	private String outputDirectory;

	/**
	 * 
	 */
	public ReporterWriter(String outputDirectory)
	{
		listReporters = new ArrayList<>();
		this.outputDirectory = outputDirectory;
	}

	/**
	 * 
	 * @param connection
	 * @throws MojoExecutionException
	 */
	public void writeReporters(Connection connection) throws MojoExecutionException
	{
		for (Pair<Reporter, ReporterParameter> pair : listReporters)
		{
			writeReports(connection, pair.getLeft(), pair.getRight());
		}
	}

	/**
	 * 
	 * @param id
	 * @throws MojoExecutionException
	 */
	private void writeReports(Connection connection, Reporter reporter, ReporterParameter reporterParameter)
			throws MojoExecutionException
	{
		List<PrintStream> printStreams = new ArrayList<>();
		FileOutputStream fout = null;

		//
		try
		{
			OutputBuffer buffer = new OutputBuffer(reporter);

			if (reporterParameter.isFileOutput())
			{

				File file = new File(reporterParameter.getFileOutput());
				if (!file.isAbsolute())
				{
					file = new File(outputDirectory, reporterParameter.getFileOutput());
				}

				if (!file.getParentFile().exists())
				{
					LOG.debug("Creating directory for reporter file " + file.getAbsolutePath());
					file.getParentFile().mkdirs();
				}

				fout = new FileOutputStream(file);
				LOG.info(format("Writing report %s to %s", reporter.getSelfType(), file.getAbsolutePath()));

				// Added to the Report
				printStreams.add(new PrintStream(fout));
			}

			if (reporterParameter.isConsoleOutput())
			{
				LOG.info(format("Writing report %s to Console", reporter.getSelfType()));
				printStreams.add(System.out);
			}
			buffer.printAvailable(connection, printStreams);
		}
		catch (Exception e)
		{
			throw new MojoExecutionException("Unexpected error opening file ouput ", e);
		}
		finally
		{
			if (fout != null)
			{
				try
				{
					fout.close();
				}
				catch (IOException e)
				{
					LOG.info(format("Failed to closing the reporting %s", reporterParameter.getClass()));
				}
			}
		}

	}

	/**
	 * 
	 */
	public void addReporter(ReporterParameter parameter, Reporter reporter)
	{
		listReporters.add(Pair.of(reporter, parameter));
	}

}

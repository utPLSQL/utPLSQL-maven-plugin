package org.utplsql.model;

import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author Alberto Hdez
 *
 */
public class ReporterParameter
{

	// Name of the registered reported in UtPLSQL
	private String name;

	// File Output of the reporter
	private String fileOutput;

	// Writes the report to console
	private boolean consoleOutput;

	/**
	 * 
	 */
	public ReporterParameter()
	{
		super();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the fileOutput
	 */
	public String getFileOutput()
	{
		return fileOutput;
	}

	/**
	 * @param fileOutput
	 *            the fileOutput to set
	 */
	public void setFileOutput(String fileOutput)
	{
		this.fileOutput = fileOutput;
	}

	/**
	 * @return the consoleOutput
	 */
	public boolean isConsoleOutput()
	{
		return consoleOutput;
	}

	/**
	 * @param consoleOutput
	 *            the consoleOutput to set
	 */
	public void setConsoleOutput(boolean consoleOutput)
	{
		this.consoleOutput = consoleOutput;
	}

	/**
	 * @return the consoleOutput
	 */
	public boolean isFileOutput()
	{
		return StringUtils.isNotBlank(fileOutput);
	}

}

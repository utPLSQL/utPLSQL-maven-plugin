package org.utplsql.maven.plugin.model;

import org.codehaus.plexus.util.StringUtils;

/**
 * Represents a reporter parameter in the pom file.
 *
 * {@code
 * <reporter>
 *     <name>...</name>
 *     <fileOutput>...</fileOutput>
 *     <consoleOutput>...</consoleOutput>
 * </reporter>
 * }
 *
 * @author Alberto Hernández
 */
public class ReporterParameter {

    private String name;
    private String fileOutput;
    private Boolean consoleOutput;

    /**
     * Returns the reporter name.
     *
     * @return the reporter name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the reporter name.
     *
     * @param name the reporter name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns reporter output file.
     *
     * @return the output file name
     */
    public String getFileOutput() {
        return fileOutput;
    }

    /**
     * Returns whether the file output is enabled or not.
     *
     * @return true if the file output is enabled, false otherwise
     */
    public boolean isFileOutput() {
        return StringUtils.isNotBlank(fileOutput);
    }

    /**
     * Sets the output file.
     *
     * @param fileOutput the output file name
     */
    public void setFileOutput(String fileOutput) {
        this.fileOutput = fileOutput;
    }

    /**
     * Returns the console output option.
     *
     * @return the console output option
     */
    public Boolean getConsoleOutput() {
        return consoleOutput;
    }

    /**
     * Returns whether the console output should be enabled or not.
     *
     * @return true if console output is enable, false otherwise
     */
    public Boolean isConsoleOutput() {
        return null != consoleOutput && consoleOutput;
    }

    /**
     * Sets the console output option.
     *
     * @param consoleOutput the console output option
     */
    public void setConsoleOutput(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
    }
}

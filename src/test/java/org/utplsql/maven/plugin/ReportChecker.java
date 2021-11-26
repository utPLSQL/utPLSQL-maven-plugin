package org.utplsql.maven.plugin;

import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.soebes.itf.extension.assertj.MavenProjectResultAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ReportChecker {

    private ReportChecker() {
    }

    /**
     * Duration is set to 1 before comparing contents as it is always different.
     * Path separator is set to "/" to ensure windows / linux / mac compatibility.
     * \r and \n are removed to provide simpler comparison.
     *
     * @param result {@link MavenExecutionResult}
     * @param files  Files to compare
     */
    public static void assertThatReportsAreGeneratedAsExpected(MavenExecutionResult result, String... files) {
        for (String filename : files) {
            File expectedOutputFile = new File(result.getMavenProjectResult().getTargetProjectDirectory(), "/expected-output/utplsql/" + filename);
            File outputFile = new File(result.getMavenProjectResult().getTargetProjectDirectory(), "/target/utplsql/" + filename);

            assertThat(result.getMavenProjectResult()).withFile("/utplsql/" + filename).exists();

            try (Stream<String> stream = Files.lines(outputFile.toPath())) {
                String outputContent = stream
                        .filter(line -> !line.contains("<?xml"))
                        .map(line -> line.replaceAll("(duration=\"[0-9.]*\")", "duration=\"1\""))
                        .map(line -> line.replaceAll("\\\\", "/"))
                        .map(line -> line.replaceAll("\r", "").replaceAll("\n", ""))
                        .collect(Collectors.joining("\n"));

                assertEquals(
                        FileUtils.readFileToString(expectedOutputFile, "utf-8")
                                .replace("\r", "")
                                .replace("\n", ""),
                        outputContent.replace("\n", ""), "The files differ!");
            } catch (IOException e) {
                fail(e);
            }
        }
    }

    /**
     * Check if the report was generated
     *
     * @param result   {@link MavenExecutionResult}
     * @param filename File Name
     * @return if report exits
     */
    public static boolean reportWasGenerated(MavenExecutionResult result, String filename) {
        File outputFile = new File(result.getMavenProjectResult().getTargetProjectDirectory(), "/target/utplsql/" + filename);
        return outputFile.exists();
    }
}

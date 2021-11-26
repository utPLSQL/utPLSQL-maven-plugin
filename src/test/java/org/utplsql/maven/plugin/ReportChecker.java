package org.utplsql.maven.plugin;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportChecker {

    private ReportChecker() {
    }

    /**
     * Duration is set to 1 before comparing contents as it is always different.
     * Path separator is set to "/" to ensure windows / linux / mac compatibility.
     * \r and \n are removed to provide simpler comparison.
     *
     * @param testClass  Class under test
     * @param testFolder Folder name
     * @param files      Files to compare
     */
    public static void checkReports(Class<?> testClass, String testFolder, String... files) throws IOException {
        String fullyQualifiedClassNameDirectory = testClass.getName().replace(".", "/");

        for (String filename : files) {
            File expectedOutputFile = new File("target/maven-it/" + fullyQualifiedClassNameDirectory + "/" + testFolder + "/project/expected-output/utplsql", filename);
            File outputFile = new File("target/maven-it/" + fullyQualifiedClassNameDirectory + "/" + testFolder + "/project/target/utplsql", filename);
            assertTrue(outputFile.exists(), "The report " + outputFile.getPath() + " was not generated");

            try (Stream<String> stream = Files.lines(outputFile.toPath())) {
                String outputContent = stream
                        .filter(line -> !line.contains("<?xml"))
                        .map(line -> line.replaceAll("(duration=\"[0-9.]*\")", "duration=\"1\""))
                        .map(line -> line.replaceAll("\\\\", "/"))
                        .map(line -> line.replaceAll("\r", "").replaceAll("\n", ""))
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(
                        FileUtils.readFileToString(expectedOutputFile, "utf-8")
                                .replace("\r", "")
                                .replace("\n", ""),
                        outputContent.replace("\n", ""), "The files differ!");
            }
        }
    }

    /**
     * Check if a report file exits
     *
     * @param testClass  Class under test
     * @param testFolder Folder name
     * @param filename   File Name
     * @return true or false
     */
    public static boolean reportExists(Class<?> testClass, String testFolder, String filename) {
        String fullyQualifiedClassNameDirectory = testClass.getName().replace(".", "/");

        File outputFile = new File("target/maven-it/" + fullyQualifiedClassNameDirectory + "/" + testFolder + "/project/target/utplsql", filename);
        return outputFile.exists();
    }
}

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UtPLSQLMojoTest extends AbstractMojoTestCase {

    public static final String POM_PATH = "src/test/resources/pom.xml";

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void test_mojo_definition() throws Exception {
        File pom = getTestFile(POM_PATH);
        assertThat(pom.exists()).isTrue();

        Mojo testMojo = lookupMojo("test", pom);
        assertThat(testMojo).isNotNull();
    }

    public void test_mapping_suite_paths() throws Exception {
        File pom = getTestFile("src/test/resources/pom.xml");
        Mojo testMojo = lookupMojo("test", pom);

        List<String> paths = (List<String>) getVariableValueFromObject(testMojo, "suitePaths");

        assertThat(paths).containsExactly("suite_path1", "suite_path2");
    }

    public void test_mapping_source_paths() throws Exception {
        File pom = getTestFile("src/test/resources/pom.xml");
        Mojo testMojo = lookupMojo("test", pom);

        List<String> paths = (List<String>) getVariableValueFromObject(testMojo, "sourcePaths");

        assertThat(paths).containsExactly("source_path1", "source_path2", "source_path3");
    }

    public void test_mapping_test_paths() throws Exception {
        File pom = getTestFile("src/test/resources/pom.xml");
        Mojo testMojo = lookupMojo("test", pom);

        List<String> paths = (List<String>) getVariableValueFromObject(testMojo, "testPaths");

        assertThat(paths).containsExactly("test_path1", "test_path2");
    }
}

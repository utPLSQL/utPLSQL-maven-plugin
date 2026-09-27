package org.utplsql.maven.plugin;

import oracle.security.pki.OracleSecretStore;
import oracle.security.pki.OracleWallet;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.utplsql.maven.plugin.UtPlsqlMojoTest.UNIT_TESTS;

/**
 * Tests of the database connection configuration.
 * <p>
 * Unlike {@link UtPlsqlMojoTest}, no connection is given as mojo parameters,
 * so the mojo falls back to the dbUrl, dbUser and dbPass system properties.
 */
@MojoTest
@MojoParameter(name = "targetDir", value = "target/unit-tests")
class UtPlsqlMojoConnectionTest {

    private static final String WALLET_TNS_ALIAS = "UTPLSQL_MAVEN_WALLET";

    @TempDir
    Path tempDir;

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty("dbUrl");
        System.clearProperty("dbUser");
        System.clearProperty("dbPass");
    }

    /**
     * DB configuration from System Properties
     * <p>
     * Given : a pom.xml without dbUrl, dbUser and dbPass configured
     * When : pom is read
     * Then : System Properties must be used to configure database
     */
    @Test
    @Basedir(UNIT_TESTS + "db_config_using_system_properties")
    void db_config_using_system_properties(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        System.setProperty("dbUrl", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");
        System.setProperty("dbUser", "UT3");
        System.setProperty("dbPass", "ut3");

        utPlsqlMojo.execute();
    }

    /**
     * Connection using Oracle Wallet (Secure External Password Store)
     * <p>
     * Given : a pom.xml without dbUser and dbPass and a dbUrl pointing to a TNS alias stored in an Oracle Wallet
     * When : tests are run
     * Then : credentials are taken from the wallet
     */
    @Test
    @Basedir(UNIT_TESTS + "wallet_connection")
    void wallet_connection_without_credentials(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        System.setProperty("dbUrl", walletDbUrl());

        utPlsqlMojo.execute();
    }

    /**
     * Connection using Oracle Wallet with empty credentials
     * <p>
     * Given : a dbUrl pointing to a TNS alias stored in an Oracle Wallet and empty dbUser and dbPass (e.g. -DdbUser= -DdbPass=)
     * When : tests are run
     * Then : empty credentials are ignored and credentials are taken from the wallet
     */
    @Test
    @Basedir(UNIT_TESTS + "wallet_connection")
    void wallet_connection_with_empty_credentials(@InjectMojo(goal = "test", pom = "pom.xml") UtPlsqlMojo utPlsqlMojo) throws Exception {
        System.setProperty("dbUrl", walletDbUrl());
        System.setProperty("dbUser", "");
        System.setProperty("dbPass", "");

        utPlsqlMojo.execute();
    }

    /**
     * Creates an auto-login Oracle Wallet holding the UT3 credentials for {@link #WALLET_TNS_ALIAS},
     * with tnsnames.ora and ojdbc.properties next to it, so no Oracle client tooling (mkstore/orapki) is needed.
     *
     * @return JDBC URL of the TNS alias, with the generated directory as TNS_ADMIN
     */
    private String walletDbUrl() throws Exception {
        Path tnsAdmin = Files.createDirectories(tempDir.resolve("tns_admin"));

        OracleWallet wallet = new OracleWallet();
        wallet.create("Wallet_Pwd_123".toCharArray());
        OracleSecretStore secretStore = wallet.getSecretStore();
        secretStore.createCredential(WALLET_TNS_ALIAS.toCharArray(), "UT3".toCharArray(), "ut3".toCharArray());
        wallet.setSecretStore(secretStore);
        wallet.saveAs(tnsAdmin.toString());
        wallet.createSSO();
        wallet.saveSSO();

        Files.writeString(tnsAdmin.resolve("tnsnames.ora"),
                WALLET_TNS_ALIAS + " = (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))"
                        + "(CONNECT_DATA = (SERVICE_NAME = FREEPDB1)))\n");
        // ${TNS_ADMIN} is resolved by the JDBC driver, the same way as in wallets downloaded for Oracle Autonomous Database
        Files.writeString(tnsAdmin.resolve("ojdbc.properties"),
                "oracle.net.wallet_location=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=${TNS_ADMIN})))\n");

        return "jdbc:oracle:thin:@" + WALLET_TNS_ALIAS + "?TNS_ADMIN=" + tnsAdmin.toAbsolutePath().toString().replace('\\', '/');
    }
}

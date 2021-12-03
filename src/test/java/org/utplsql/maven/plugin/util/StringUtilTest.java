package org.utplsql.maven.plugin.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.utplsql.maven.plugin.util.StringUtil.isBlank;
import static org.utplsql.maven.plugin.util.StringUtil.isEmpty;
import static org.utplsql.maven.plugin.util.StringUtil.isNotBlank;
import static org.utplsql.maven.plugin.util.StringUtil.isNotEmpty;

class StringUtilTest {

    @Test
    void is_empty() {
        assertTrue(isEmpty(""));
    }

    @Test
    void is_not_empty() {
        assertTrue(isNotEmpty("abc"));
    }

    @Test
    void is_blank() {
        assertTrue(isBlank(" "));
    }

    @Test
    void is_not_blank() {
        assertTrue(isNotBlank("abc"));
    }
}

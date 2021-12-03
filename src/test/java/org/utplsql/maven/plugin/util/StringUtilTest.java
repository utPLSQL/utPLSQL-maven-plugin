package org.utplsql.maven.plugin.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.utplsql.maven.plugin.util.StringUtil.isBlank;
import static org.utplsql.maven.plugin.util.StringUtil.isEmpty;
import static org.utplsql.maven.plugin.util.StringUtil.isNotBlank;
import static org.utplsql.maven.plugin.util.StringUtil.isNotEmpty;

class StringUtilTest {

    @Test
    void isEmpty_with_empty() {
        assertTrue(isEmpty(""));
    }

    @Test
    void isEmpty_with_null() {
        assertTrue(isEmpty(null));
    }

    @Test
    void isEmpty_with_string() {
        assertFalse(isEmpty("abc"));
    }

    @Test
    void isNotEmpty_with_string() {
        assertTrue(isNotEmpty("abc"));
    }

    @Test
    void isNotEmpty_with_empty() {
        assertFalse(isNotEmpty(""));
    }

    @Test
    void isNotEmpty_with_blank() {
        assertTrue(isNotEmpty(" "));
    }

    @Test
    void isBlank_with_blank() {
        assertTrue(isBlank(" "));
    }

    @Test
    void isBlank_with_null() {
        assertTrue(isBlank(null));
    }

    @Test
    void isBlank_with_string() {
        assertFalse(isBlank("abc"));
    }

    @Test
    void isBlank_with_whitespaces_before_string() {
        assertFalse(isBlank("   abc"));
    }

    @Test
    void isNotBlank_with_string() {
        assertTrue(isNotBlank("abc"));
    }

    @Test
    void isNotBlank_with_empty() {
        assertFalse(isNotBlank(""));
    }
}

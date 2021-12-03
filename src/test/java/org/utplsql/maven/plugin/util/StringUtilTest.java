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
    void is_empty_true() {
        assertTrue(isEmpty(""));
    }

    @Test
    void is_empty_false() {
        assertFalse(isEmpty("abc"));
    }

    @Test
    void is_not_empty_true() {
        assertTrue(isNotEmpty("abc"));
    }

    @Test
    void is_not_empty_false() {
        assertFalse(isNotEmpty(""));
    }

    @Test
    void is_blank_true() {
        assertTrue(isBlank(" "));
    }

    @Test
    void is_blank_false() {
        assertFalse(isBlank("abc"));
    }

    @Test
    void is_not_blank_true() {
        assertTrue(isNotBlank("abc"));
    }

    @Test
    void is_not_blank_false() {
        assertFalse(isNotBlank(""));
    }
}

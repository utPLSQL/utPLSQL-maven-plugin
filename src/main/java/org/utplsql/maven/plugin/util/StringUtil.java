package org.utplsql.maven.plugin.util;

public class StringUtil {

    private StringUtil() {
    }

    public static boolean isEmpty(CharSequence cs) {
        if (cs == null) {
            return true;
        }
        if (cs.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null) {
            if ((strLen = cs.length()) != 0) {
                for (int i = 0; i < strLen; ++i) {
                    if (!Character.isWhitespace(cs.charAt(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }
}

package org.atechtrade.rent.util;

import java.util.StringJoiner;

public class TextUtils {

    private TextUtils() {
        //empty constructor, because this is "utility" class with only static methods
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    public static String concatFullName(final String firstName, final String middleName, final String lastName) {
        StringJoiner joiner = new StringJoiner(" ");

        if (firstName != null && !firstName.isEmpty()) {
            joiner.add(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            joiner.add(middleName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            joiner.add(lastName);
        }

        return joiner.toString();
    }
}

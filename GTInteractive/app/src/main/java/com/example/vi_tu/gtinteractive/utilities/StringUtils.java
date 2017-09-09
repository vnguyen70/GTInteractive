package com.example.vi_tu.gtinteractive.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static String tokenize(String input) {
        return input != null ? input.replaceAll("\\W", " ").toLowerCase() : null;
    }

    public static List<String> tokenizeList(String input) {
        return new ArrayList<>(Arrays.asList(tokenize(input).split("\\s+"))); // // TODO: check regex expression
    }

    public static boolean isInteger(String str) {
        if (str == null) { return false; }
        int length = str.length();
        if (length == 0) { return false; }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) { return false; }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') { return false; }
        }
        return true;
    }

}

package com.nanmu.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtil {

    public static String toCamelCase(String words) {
        return Arrays.stream(words.split("_")).map(StringUtil::toUpperCaseFirstLetter).collect(Collectors.joining());
    }

    public static String toUpperCaseFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

}

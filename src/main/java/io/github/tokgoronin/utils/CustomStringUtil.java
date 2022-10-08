package io.github.tokgoronin.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomStringUtil {

    public static String toCamelCase(String words, boolean upperFirst) {
        String camel = Arrays.stream(words.split("_")).map(CustomStringUtil::toUpperCaseFirstLetter)
                             .collect(Collectors.joining());
        if (upperFirst) {
            return camel;
        }
        return camel.substring(0, 1).toLowerCase() + camel.substring(1);
    }

    public static String toUpperCaseFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

}

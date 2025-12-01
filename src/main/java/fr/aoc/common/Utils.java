package fr.aoc.common;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {

    public static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    public static final String REGEX_NEW_LINE = "(\r\n|\r|\n)";
    public static final String REGEX_EMPTY_LINE = REGEX_NEW_LINE + "{2}";

    public static final String RESOURCE = "src/main/resources/%s/%s/%s";

    public static List<String> readInputSplitOnNewLines(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE)).toList();
        }
    }
    public static String readInputJoinOnNewLines(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return java.lang.String.join("", IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE));
        }
    }

    public static List<String> readInputSplitOnEmptyLines(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_EMPTY_LINE)).toList();
        }
    }

    public static String[] removeEmptyStrsFromArray(String[] strsArray) {
        return Arrays.stream(strsArray)
                .filter(str -> !str.isEmpty())
                .toArray(String[]::new);
    }

}

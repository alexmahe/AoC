package fr.aoc.common;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static final String REGEX_NEW_LINE = "(\r\n|\r|\n)";

    public static List<String> readInputSplitOnNewLines(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE)).toList();
        }
    }
    public static String readInputJoinOnNewLines(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return String.join("", IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE));
        }
    }

}

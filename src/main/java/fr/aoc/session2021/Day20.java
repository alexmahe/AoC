package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day20 {

    private static final Integer[][] neighbors = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private String enhancement;
    private ArrayList<ArrayList<String>> photo;

    public static void main(String[] args) {
        Day20 day20 = new Day20();
        day20.readInput("src/main/resources/2021/day20/input.txt");

        for (int nbTour = 1; nbTour <= 50; nbTour++) {
            day20.photo = day20.enhance(day20.photo, nbTour % 2 == 1 ? "." : "#");

            if (nbTour == 2) log.info("Part 1 answer : {}", day20.countLitPixel(day20.photo));
        }

        log.info("Part 2 answer : {}", day20.countLitPixel(day20.photo));
    }

    private void readInput(String filePath) {
        StringBuilder enhancementBuilder = new StringBuilder();
        photo = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String numbersStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            AtomicBoolean isEnhancement = new AtomicBoolean(true);
            Arrays.stream(numbersStr.split(REGEX_NEW_LINE)).forEach(line -> {
                if (line == null || line.isEmpty() || line.trim().isEmpty()) {
                    isEnhancement.set(false);
                } else {
                    if (isEnhancement.get()) {
                        enhancementBuilder.append(line);
                    } else {
                        photo.add(Arrays.stream(line.split("")).collect(Collectors.toCollection(ArrayList::new)));
                    }
                }
            });
            enhancement = enhancementBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<String>> enhance(ArrayList<ArrayList<String>> photo, String paddingChar) {
        padPhoto(photo, paddingChar);
        ArrayList<ArrayList<String>> enhancedPhoto = new ArrayList<>();

        for (int indexLine = 0; indexLine < photo.size(); indexLine++) {
            ArrayList<String> enhancedLine = new ArrayList<>();

            for (int indexCol = 0; indexCol < photo.get(indexLine).size(); indexCol++) {
                StringBuilder neighborsStr = new StringBuilder();

                for (Integer[] neighbor : neighbors) {
                    int line = indexLine + neighbor[0];
                    int col = indexCol + neighbor[1];
                    String currentSymbol = "";

                    try {
                        currentSymbol = photo.get(line).get(col);
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        currentSymbol = paddingChar;
                    }

                    if ("#".equals(currentSymbol)) neighborsStr.append(1);
                    else neighborsStr.append(0);
                }

                enhancedLine.add(Character.toString(enhancement.charAt(Integer.parseInt(neighborsStr.toString(), 2))));
            }

            enhancedPhoto.add(enhancedLine);
        }

        return enhancedPhoto;
    }

    private void padPhoto(ArrayList<ArrayList<String>> photo, String paddingChar) {
        int length = photo.get(0).size();
        ArrayList<String> paddingTop = Stream.generate(() -> paddingChar).limit(length + 2L).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> paddingBottom = Stream.generate(() -> paddingChar).limit(length + 2L).collect(Collectors.toCollection(ArrayList::new));

        photo.forEach(line -> {
            line.add(0, paddingChar);
            line.add(paddingChar);
        });
        photo.add(0, paddingTop);
        photo.add(paddingBottom);
    }

    private long countLitPixel(ArrayList<ArrayList<String>> photo) {
        return photo.stream()
                .map(line -> line.stream()
                        .filter("#"::equals)
                        .map(pixel -> 1L)
                        .reduce(0L, Long::sum)
                )
                .reduce(0L, Long::sum);
    }

    private static String formattingArrayForLog(ArrayList<ArrayList<String>> array) {
        return array.stream().map(boardLine -> boardLine.stream().map(String::valueOf).collect(Collectors.joining(" "))).collect(Collectors.joining("\n"));
    }

}

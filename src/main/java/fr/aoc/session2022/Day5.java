package fr.aoc.session2022;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day5 {

    private List<ArrayList<String>> crates;

    public static void main(String[] args) throws IOException {
        Day5 day5 = new Day5();

        String input = day5.readInput("src/main/resources/2022/day5/input.txt");
        String initialStateInput = input.split(REGEX_NEW_LINE + "{2}")[0];
        String[] instructions = input.split(REGEX_NEW_LINE + "{2}")[1].split(REGEX_NEW_LINE);

        day5.setInitialState(initialStateInput);
        day5.processInstructions(instructions, true);
        String answer1 = day5.getAnswer();

        day5.setInitialState(initialStateInput);
        day5.processInstructions(instructions, false);
        String answer2 = day5.getAnswer();

        log.info("Result answer1 : {}", answer1);
        log.info("Result answer2 : {}", answer2);
    }

    private String getAnswer() {
        return this.crates.stream()
                .map(col -> col.get(0))
                .collect(Collectors.joining());
    }

    private String readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8);
        }
    }

    private void setInitialState(String input) {
        var cratesInit = Arrays.stream(input.split(REGEX_NEW_LINE)).toList();
        int colNb = Arrays.stream(cratesInit.get(cratesInit.size() - 1).split("\\D")).filter(Predicate.not(String::isBlank)).mapToInt(Integer::parseInt).max().getAsInt();
        crates = Stream.generate(() -> (ArrayList<String>) new ArrayList()).limit(colNb).toList();
        Pattern cratePattern = Pattern.compile("(\\[\\w]|\\s\\s\\s)\\s?");

        for (int lineIndex = 0; lineIndex < cratesInit.size() - 1; lineIndex++) {
            Matcher crateMatcher = cratePattern.matcher(cratesInit.get(lineIndex));
            int crateCol = 0;
            while (crateMatcher.find()) {
                String crate = crateMatcher.group();
                if (crate.matches("\\[\\w]\\s?")) {
                    crates.get(crateCol).add(crate.substring(1,2));
                }
                crateCol++;
            }
        }
    }

    private void processInstructions(String[] instructions, boolean isaAnswer1) {
        Arrays.stream(instructions).forEach(move -> {
            // Tableau d'instruction pour le mouvement de la caisse [how many, from, to]
            var moveInfos = Arrays.stream(move.split("\\D")).filter(Predicate.not(String::isBlank)).mapToInt(Integer::parseInt).boxed().toList();
            for (int nbMove = 0; nbMove < moveInfos.get(0); nbMove++) {
                this.moveCrate(moveInfos.get(1) - 1, moveInfos.get(2) - 1, isaAnswer1 ? 0 : moveInfos.get(0) - nbMove - 1);
            }
        });
    }

    private void moveCrate(int from, int to, int depth) {
        String crate = crates.get(from).get(depth);
        crates.get(to).add(0, crate);
        crates.get(from).remove(depth);
    }

}

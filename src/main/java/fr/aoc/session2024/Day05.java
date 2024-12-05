package fr.aoc.session2024;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day05 {

    private static List<String> RULES;

    public static void main(String[] args) throws IOException {
        var today = new Day05();
        var input = Utils.readInputSplitOnEmptyLines("src/main/resources/2024/day05/input_test");
        RULES = Arrays.stream(input.get(0).split(REGEX_NEW_LINE)).toList();
        var updates = Arrays.stream(input.get(1).split(REGEX_NEW_LINE))
                .map(pagesToUpdate -> Arrays.stream(pagesToUpdate.split(",")).toList())
                .toList();

        var enrichedUpdates = updates.stream()
                .map(today::processUpdate)
                .toList();
        var sum = enrichedUpdates.stream()
                .filter(EnrichedUpdate::correct)
                .map(enrichedUpdate -> enrichedUpdate.update.get(enrichedUpdate.update.size() / 2))
                .mapToLong(Long::parseLong)
                .sum();
        log.info("Sum of middle page numbers: {}", sum);

        var incorrectUpdate = enrichedUpdates.stream().filter(Predicate.not(EnrichedUpdate::correct))
                .toList();
        var stop = "stop";
    }

    private EnrichedUpdate processUpdate(List<String> update) {
        var ruleSubset = RULES.stream()
                .map(rule -> rule.split("\\|"))
                .filter(pairToCompare -> update.contains(pairToCompare[0]) && update.contains(pairToCompare[1]))
                .toList();
        var isCorrect = ruleSubset.stream()
                .allMatch(rule -> respectRule(update, rule));
        return new EnrichedUpdate(update, ruleSubset, isCorrect);
    }

    private boolean respectRule(List<String> pagesToUpdate, String[] rule) {
        return pagesToUpdate.indexOf(rule[0]) < pagesToUpdate.indexOf(rule[1]);
    }

    private record EnrichedUpdate(
            List<String> update,
            List<String[]> concernedRuleSubset,
            boolean correct
    ) {}

}

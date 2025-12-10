package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day06 {

    public static void main(String[] args) throws IOException {
        var today = new Day06();
        var pattern = Pattern.compile("[\\w+*]+");
        var input = Utils.readInputSplitOnNewLines(RESOURCE.formatted("2025", "day06", "input"));
        var regexedLines = input.stream()
            .map(line -> pattern.matcher(line).results().map(MatchResult::group).toList())
            .toList();
        var problems = today.parsePart1(regexedLines);
        var sum = problems.stream()
            .mapToLong(Problem::result)
            .sum();
        log.info("sum: {}", sum);

        var problemsPart2 = today.parsePart2(input);
        sum = problemsPart2.stream()
            .mapToLong(Problem::result)
            .sum();
        log.info("sum: {}", sum);
    }

    private List<Problem> parsePart2(List<String> input) {
        var pattern = Pattern.compile("[+*]\\s*");
        var inputSize = input.size();
        var operationLineLength = input.get(inputSize - 1).length();

        return pattern.matcher(input.get(inputSize - 1)).results()
            .map(result -> {
                var numbersAsString = input.stream()
                    .limit(inputSize - 1)
                    .map(line -> line.substring(result.start(), result.end() == operationLineLength ? line.length() : result.end() - 1))
                    .toList();
                var operation = Operation.getOperationFromSymbol(result.group().trim());
                return Pair.of(operation, numbersAsString);
            })
            .map(pair -> Pair.of(pair.getLeft(), parseVerticalNumbersList(pair.getRight())))
            .map(pair -> new Problem(pair.getRight(), pair.getLeft()))
            .toList();
    }

    private List<Long> parseVerticalNumbersList(List<String> input) {
        var result = new ArrayList<Long>();
        for (int numberIndex = 0; numberIndex < input.get(0).length(); numberIndex++) {
            int finalNumberIndex = numberIndex;
            var strBldr = new StringBuilder();
            input.stream().map(str -> str.charAt(finalNumberIndex)).forEach(strBldr::append);
            result.add(Long.parseLong(strBldr.toString().trim()));
        }
        return result;
    }

    private List<Problem> parsePart1(List<List<String>> input) {
        var problems = new ArrayList<Problem>();

        for (int problemIndex = 0; problemIndex < input.get(0).size(); problemIndex++) {
            int finalProblemIndex = problemIndex;
            var numbers = input.stream()
                .limit(input.size() - 1)
                .map(line -> line.get(finalProblemIndex))
                .map(Long::parseLong)
                .toList();
            var operation = Operation.getOperationFromSymbol(input.get(input.size() - 1).get(finalProblemIndex));
            problems.add(new Problem(numbers, operation));
        }

        return problems;
    }

    private record Problem(
        List<Long> numbers,
        Operation operation
    ) {
        private Long result() {
            return numbers.stream()
                .reduce(operation.getIdentity(), operation.getOperation());
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Operation {
        PLUS("+", Long::sum, 0L),
        MULT("*", (a, b) -> a * b, 1L);

        private final String symbol;
        private final BinaryOperator<Long> operation;
        private final Long identity;

        private static Operation getOperationFromSymbol(String symbol) {
            return Arrays.stream(Operation.values())
                .filter(operation -> operation.symbol.equals(symbol))
                .findFirst().orElseThrow(UnsupportedOperationException::new);
        }
    }

}

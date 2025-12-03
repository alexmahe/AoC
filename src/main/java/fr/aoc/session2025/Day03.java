package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day03 {

    public static void main(String[] args) throws IOException {
        var today = new Day03();

        var banks = Utils.readInputSplitOnNewLines(RESOURCE.formatted("2025", "day03", "input")).stream()
                .toList();

        var sumOfJolts = banks.stream()
                .mapToLong(bank -> today.processBank(bank, 2))
                .sum();

        log.info("Sum of jolts for 2: {}", sumOfJolts);

        sumOfJolts = banks.stream()
                .mapToLong(bank -> today.processBank(bank, 12))
                .sum();

        log.info("Sum of jolts for 12: {}", sumOfJolts);
    }

    private long processBank(String bank, int nbBatteriesWanted) {
        if (bank.length() == nbBatteriesWanted) return Long.parseLong(bank);

        var queue = bankToQueue(bank.substring(0, bank.length() - nbBatteriesWanted + 1));
        int highestJolt = queue.poll();

        if (nbBatteriesWanted == 1) return highestJolt;

        int indexOfHighest = bank.indexOf(String.valueOf(highestJolt));
        return (highestJolt * (long) Math.pow(10, nbBatteriesWanted - 1)) + processBank(bank.substring(indexOfHighest + 1), nbBatteriesWanted - 1);
    }

    private int gildedProcessBank(String bank) {
        var queue = bankToQueue(bank);
        int highestJolt = queue.poll();
        PriorityQueue<Integer> remainderQueue;

        int indexOfHighest = bank.indexOf(String.valueOf(highestJolt));

        if (indexOfHighest != bank.length() - 1) {
            remainderQueue = bankToQueue(bank.substring(indexOfHighest + 1));
        } else {
            highestJolt = queue.poll();
            remainderQueue = bankToQueue(bank.substring(bank.indexOf(String.valueOf(highestJolt)) + 1));
        }

        int highestRemainingJolt = remainderQueue.poll();

        return (highestJolt * 10) + highestRemainingJolt;
    }

    private PriorityQueue<Integer> bankToQueue(String bank) {
        var batteriesJolt = Arrays.stream(bank.split(""))
                .mapToInt(Integer::parseInt)
                .boxed().toList();
        var queue = new PriorityQueue<Integer>(Collections.reverseOrder());
        queue.addAll(batteriesJolt);
        return queue;
    };

}

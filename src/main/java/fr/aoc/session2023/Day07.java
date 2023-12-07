package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public class Day07 {

    private final List<String> VALUES_NO_JOKERS = List.of("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2");
    private final List<String> VALUES_JOKERS = List.of("A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J");
    private final String FIVE_OF_A_KIND = "fiveOfAKind";
    private final String FOUR_OF_A_KIND = "fourOfAKind";
    private final String THREE_OF_A_KIND = "threeOfAKind";
    private final String FULL_HOUSE = "fullHouse";
    private final String TWO_PAIRS = "twoPairs";
    private final String ONE_PAIR = "onePair";
    private final String HIGH_CARDS = "highCards";

    public static void main(String[] args) throws IOException {
        var today = new Day07();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day07/input");

        var handsList = input.stream()
                .map(hand -> {
                    var infos = hand.split("\\s");
                    return new Hand(infos[0], Long.parseLong(infos[1]));
                }).toList();
        var handsWithoutJoker = new HashMap<String, List<Hand>>();
        handsList.forEach(hand -> today.filterHandType(handsWithoutJoker, hand, false));
        handsWithoutJoker.forEach((k, v) -> v.sort(today.compareHandWithoutJokers));

        var scoreWithoutJoker = today.calcHandsScore(handsWithoutJoker);
        log.info("Score total without joker : {}", scoreWithoutJoker);

        var handsWithJoker = new HashMap<String, List<Hand>>();
        handsList.forEach(hand -> today.filterHandType(handsWithJoker, hand, true));
        handsWithJoker.forEach((k, v) -> v.sort(today.compareHandWithJokers));

        var scoreWithJoker = today.calcHandsScore(handsWithJoker);
        log.info("Score total with joker : {}", scoreWithJoker);
    }

    private void filterHandType(Map<String, List<Hand>> hands, Hand hand, boolean consideringJokers) {
        var occurencesOfChar = Arrays.stream(hand.hand().split(""))
                .collect(Collectors.groupingBy(s -> s, HashMap::new, Collectors.counting()));

        if (occurencesOfChar.containsValue(5L)) {
            hands.compute(FIVE_OF_A_KIND, getOrInitList).add(hand);
        } else if (occurencesOfChar.containsValue(4L)) {
            if (hand.hand().contains("J") && consideringJokers) {
                hands.compute(FIVE_OF_A_KIND, getOrInitList).add(hand);
            } else {
                hands.compute(FOUR_OF_A_KIND, getOrInitList).add(hand);
            }
        } else if (occurencesOfChar.containsValue(3L)) {
            if (occurencesOfChar.containsValue(2L)) {
                if (hand.hand().contains("J") && consideringJokers) {
                    hands.compute(FIVE_OF_A_KIND, getOrInitList).add(hand);
                } else {
                    hands.compute(FULL_HOUSE, getOrInitList).add(hand);
                }
            } else {
                if (hand.hand().contains("J") && consideringJokers) {
                    hands.compute(FOUR_OF_A_KIND, getOrInitList).add(hand);
                } else {
                    hands.compute(THREE_OF_A_KIND, getOrInitList).add(hand);
                }
            }
        } else if (occurencesOfChar.containsValue(2L)) {
            if (occurencesOfChar.size() == 3) {
                if (hand.hand().contains("J") && consideringJokers) {
                    if (occurencesOfChar.get("J") == 2) {
                        hands.compute(FOUR_OF_A_KIND, getOrInitList).add(hand);
                    } else {
                        hands.compute(FULL_HOUSE, getOrInitList).add(hand);
                    }
                } else {
                    hands.compute(TWO_PAIRS, getOrInitList).add(hand);
                }
            } else {
                if (hand.hand().contains("J") && consideringJokers) {
                    hands.compute(THREE_OF_A_KIND, getOrInitList).add(hand);
                } else {
                    hands.compute(ONE_PAIR, getOrInitList).add(hand);
                }
            }
        } else {
            if (hand.hand().contains("J") && consideringJokers) {
                hands.compute(ONE_PAIR, getOrInitList).add(hand);
            } else {
                hands.compute(HIGH_CARDS, getOrInitList).add(hand);
            }
        }
    }

    private final BiFunction<String, List<Hand>, List<Hand>> getOrInitList = (key, value) -> {
        value = value != null ? value : new ArrayList<>();
        return value;
    };

    private final Comparator<Hand> compareHandWithoutJokers = (hand1, hand2) -> {
        for (int cardIndex = 0; cardIndex < hand1.hand().length(); cardIndex++) {
            var cardCompare = Integer.compare(VALUES_NO_JOKERS.indexOf(String.valueOf(hand1.hand().charAt(cardIndex))), VALUES_NO_JOKERS.indexOf(String.valueOf(hand2.hand().charAt(cardIndex))));
            if (cardCompare != 0) return cardCompare;
        }
        return 0;
    };

    private final Comparator<Hand> compareHandWithJokers = (hand1, hand2) -> {
        for (int cardIndex = 0; cardIndex < hand1.hand().length(); cardIndex++) {
            var cardCompare = Integer.compare(VALUES_JOKERS.indexOf(String.valueOf(hand1.hand().charAt(cardIndex))), VALUES_JOKERS.indexOf(String.valueOf(hand2.hand().charAt(cardIndex))));
            if (cardCompare != 0) return cardCompare;
        }
        return 0;
    };

    private long calcHandsScore(Map<String, List<Hand>> hands) {
        var allHands = hands.getOrDefault(FIVE_OF_A_KIND, new ArrayList<>());
        allHands.addAll(hands.getOrDefault(FOUR_OF_A_KIND, new ArrayList<>()));
        allHands.addAll(hands.getOrDefault(FULL_HOUSE, new ArrayList<>()));
        allHands.addAll(hands.getOrDefault(THREE_OF_A_KIND, new ArrayList<>()));
        allHands.addAll(hands.getOrDefault(TWO_PAIRS, new ArrayList<>()));
        allHands.addAll(hands.getOrDefault(ONE_PAIR, new ArrayList<>()));
        allHands.addAll(hands.getOrDefault(HIGH_CARDS, new ArrayList<>()));
        var score = 0L;

        for (int handIndex = 0; handIndex < allHands.size(); handIndex++) {
            score += allHands.get(handIndex).bid() * (allHands.size() - handIndex);
        }

        return score;
    }


    private record Hand(String hand, long bid) {
    }

    ;
}

package fr.aoc.session2015;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Day3 {

    private static final Map<Coordonnees, Integer> visitedHousesP1 = new HashMap<Coordonnees, Integer>();
    private static final Map<Coordonnees, Integer> visitedHousesP2 = new HashMap<Coordonnees, Integer>();

    public static void main(String[] args) throws IOException {
        var startingHouse = new Coordonnees(0, 0);
        visitedHousesP1.put(startingHouse, 1);
        visitedHousesP2.put(startingHouse, 2);

        var deliveryPath = Utils.readInputJoinOnNewLines("src/main/resources/2015/Day3/input.txt");
        deliver(deliveryPath, startingHouse, visitedHousesP1);

        var splitPaths = splitDeliveryPath(deliveryPath);
        deliver(splitPaths[0], startingHouse, visitedHousesP2);
        deliver(splitPaths[1], startingHouse, visitedHousesP2);

        log.info("[Partie 1] Number of houses visited : {}", visitedHousesP1.size());
        log.info("[Partie 1] All houses visited : {}", visitedHousesP1);
        log.info("[Partie 2] Number of houses visited : {}", visitedHousesP2.size());
        log.info("[Partie 2] All houses visited : {}", visitedHousesP2);
    }

    private static void deliver(String deliveryPath, Coordonnees startingHouse, Map<Coordonnees, Integer> visitedHouses) {
        var directions = Arrays.stream(deliveryPath.split(""))
                .map(Direction::bySign)
                .toList();
        var currentHouse = startingHouse;

        for (Direction direction : directions) {
            currentHouse = currentHouse.move(direction);
            if (visitedHouses.containsKey(currentHouse))
                visitedHouses.put(currentHouse, visitedHouses.get(currentHouse) + 1);
            else visitedHouses.put(currentHouse, 1);
        }
    }

    private static String[] splitDeliveryPath(String deliveryPath) {
        var deliveryPathArray = deliveryPath.split("");
        var santa = new StringBuilder(deliveryPathArray[0]);
        var robo = new StringBuilder(deliveryPathArray[1]);

        for (int i = 2; i < deliveryPathArray.length - 2; i++) {
            if (i % 2 == 0) {
                santa.append(deliveryPathArray[i]);
            } else {
                robo.append(deliveryPathArray[i]);
            }
        }

        return new String[]{santa.toString(), robo.toString()};
    }

    @AllArgsConstructor
    private enum Direction {
        NORTH("^", 0, 1),
        SOUTH("v", 0, -1),
        WEST("<", -1, 0),
        EAST(">", 1, 0);

        private final String sign;
        private final long xMove;
        private final long yMove;

        public static Direction bySign(String sign) {
            return Arrays.stream(values()).filter(direction -> direction.sign.equals(sign))
                    .findAny().orElseThrow();
        }
    }

    @Data
    @AllArgsConstructor
    private static class Coordonnees {
        private long x;
        private long y;

        public Coordonnees move(Direction direction) {
            return new Coordonnees(x + direction.xMove, y + direction.yMove);
        }
    }
}

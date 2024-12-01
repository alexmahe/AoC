package fr.aoc.session2021;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day16 {

    public static void main(String[] args) {
        Day16 day16 = new Day16();
        String binaryStr = day16.readInput("src/main/resources/2021/day16/input.txt");
        Packet packet = day16.decodePacket(binaryStr);
        BigInteger result = packet.getValue();
        log.info("Sum of versions for packetTest : {}", packet.sumOfVersion());
        log.info("Result : {}", result);
    }

    private static String hexToBin(String hexStr) {
        StringBuilder binary = new StringBuilder(new BigInteger(hexStr, 16).toString(2));
        int binaryStrLength = binary.length();

        for (int padding = 0; padding < hexStr.length() * 4 - binaryStrLength; padding++) {
            binary.insert(0, "0");
        }

        return binary.toString();
    }

    private String readInput(String filePath) {
        String hexStr = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            hexStr = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hexToBin(hexStr);
    }

    private Packet decodePacket(String input) {
        Packet packet = new Packet();
        packet.setVersion(new BigInteger(input.substring(0, 0 + 3), 2));

        packet.setType(new BigInteger(input.substring(3, 3 + 3), 2));
        if (packet.getType().intValue() == 4) {
            int startIndex = 6;
            StringBuilder numberStrBuilder = new StringBuilder();
            String currentNumberSubStr = input.substring(startIndex, startIndex += 5);
            boolean lastWord = false;

            while (!lastWord) {
                numberStrBuilder.append(currentNumberSubStr.substring(1));
                if (currentNumberSubStr.startsWith("0")) lastWord = true;
                else currentNumberSubStr = input.substring(startIndex, startIndex += 5);
            }

            packet.setLength(new BigInteger(String.valueOf(startIndex)));
            packet.setValue(new BigInteger(numberStrBuilder.toString(), 2));

            return packet;
        } else {
            packet.setLengthTypeID(new BigInteger(input.substring(6, 6 + 1), 2));
            if (packet.getLengthTypeID().intValue() == 0) {
                int numberOfBitsInSubpackets = Integer.parseInt(input.substring(7, 7 + 15), 2);
                int numberOfBitsProcessed = 0;
                int startOfSubpacket = 22;
                String subpacketsToProcess = input.substring(22, 22 + numberOfBitsInSubpackets);

                while (numberOfBitsProcessed < numberOfBitsInSubpackets) {
                    Packet subpacket = decodePacket(subpacketsToProcess);
                    packet.addSubpacket(subpacket);

                    startOfSubpacket += subpacket.getLength().intValue();
                    numberOfBitsProcessed += subpacket.getLength().intValue();
                    subpacketsToProcess = input.substring(startOfSubpacket);
                }

                packet.setLength(new BigInteger(String.valueOf(22 + packet.getSubpacketsLength())));
                return packet;
            } else {
                int numberOfSubpackets = Integer.parseInt(input.substring(7, 7 + 11), 2);
                int startOfSubpacket = 18;
                String subpacketsToProcess = input.substring(startOfSubpacket);

                for (int subpacketProcessed = 0; subpacketProcessed < numberOfSubpackets;  subpacketProcessed++) {
                    Packet subpacket = decodePacket(subpacketsToProcess);
                    packet.addSubpacket(subpacket);

                    startOfSubpacket += subpacket.getLength().intValue();
                    subpacketsToProcess = input.substring(startOfSubpacket);
                }

                packet.setLength(new BigInteger(String.valueOf(18 + packet.getSubpacketsLength())));
                return packet;
            }
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    private class Packet {
        private ArrayList<Packet> subpackets = new ArrayList<>();
        private BigInteger length = new BigInteger("0");
        private BigInteger lengthTypeID = new BigInteger("-1");
        private BigInteger type = new BigInteger("-1");
        private BigInteger value = new BigInteger("-1");
        private BigInteger version = new BigInteger("0");

        public void addSubpacket(Packet packet) {
            subpackets.add(packet);
        }

        public int sumOfVersion() {
            return version.intValue() + subpackets.stream().map(Packet::sumOfVersion).reduce(0, Integer::sum);
        }

        public int getSubpacketsLength() {
            return subpackets.stream()
                    .map(Packet::getLength)
                    .mapToInt(BigInteger::intValue)
                    .reduce(0, Integer::sum);
        }

        public BigInteger getValue() {
            if (this.value.intValue() != -1) return this.value;
            else return interpret();
        }

        public BigInteger interpret() {
            return switch (this.type.intValue())  {
                case 0 -> this.subpackets.stream().map(Packet::interpret).reduce(BigInteger.ZERO, BigInteger::add);
                case 1 -> this.subpackets.stream().map(Packet::interpret).reduce(BigInteger.ONE, BigInteger::multiply);
                case 2 -> this.subpackets.stream().map(Packet::interpret).min(BigInteger::compareTo).orElse(BigInteger.ZERO);
                case 3 -> this.subpackets.stream().map(Packet::interpret).max(BigInteger::compareTo).orElse(BigInteger.ZERO);
                case 5 -> this.subpackets.get(0).interpret().compareTo(this.subpackets.get(1).interpret()) > 0 ? BigInteger.ONE : BigInteger.ZERO;
                case 6 -> this.subpackets.get(0).interpret().compareTo(this.subpackets.get(1).interpret()) < 0 ? BigInteger.ONE : BigInteger.ZERO;
                case 7 -> this.subpackets.get(0).interpret().compareTo(this.subpackets.get(1).interpret()) == 0 ? BigInteger.ONE : BigInteger.ZERO;
                case 4 -> this.getValue();
                default -> BigInteger.ZERO;
            };
        }
    }

}

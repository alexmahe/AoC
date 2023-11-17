package fr.aoc.session2015;

import fr.aoc.common.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day4 {

    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final MessageDigest DIGEST;

    static {
        try {
            DIGEST = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        var input = "iwrupvqb";
        var number = 1;
        var hash = DigestUtils.md5Hex(input + number);

        while (!hash.startsWith("000000")) {
            number++;
            hash = DigestUtils.md5Hex(input + number);
        }

        LOGGER.info("First Number found : {}", number);
    }
}

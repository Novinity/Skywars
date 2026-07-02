package dev.novinity.skywars.utils;

import java.util.Random;

public class RandomStringGenerator {
    public static String generateRandomString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }

        return sb.toString();
    }
}

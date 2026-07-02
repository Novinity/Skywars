package dev.novinity.skywars.utils;

public class NumberUtils {
    public static Integer tryParseInt(String str) {
        try {
            Integer parsed = Integer.parseInt(str);
            return parsed;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double tryParseDouble(String str) {
        try {
            Double parsed = Double.parseDouble(str);
            return parsed;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String formatTimeNoHours(int seconds) {
        if (seconds < 0) {
            seconds = 0;
        }
        int minutes = (int) Math.floor(seconds / 60D);
        int remainingSeconds = seconds - (minutes * 60);
        if (remainingSeconds < 0) {
            remainingSeconds = 0;
        }

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}

package net.efrei.android.geodressr.timer;

import android.annotation.SuppressLint;

public class TimerUtils {

    public TimerUtils() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressLint("DefaultLocale")
    public static String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }


    @SuppressLint("DefaultLocale")
    public static String formatTimeMinSecs(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02dmin %02ds", minutes, remainingSeconds);
    }
}

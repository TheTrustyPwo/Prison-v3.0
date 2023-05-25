package net.evilkingdom.prison.utils.numbers;

import java.text.NumberFormat;

public class Numbers {
    public static String formatLetters(final double number) {
        if (number < 1000) return String.valueOf(number);
        final String[] units = new String[]{"K", "M", "B", "T", "Q", "Qt"};
        final int exp = (int) Math.floor(Math.log10(number) / 3);
        return String.format("%.2f%s", number / Math.pow(1000, exp), units[exp - 1]);
    }

    public static String formatLetters(final long number) {
        if (number < 1000) return String.valueOf(number);
        final String[] units = new String[]{"K", "M", "B", "T", "Q", "Qt"};
        final int exp = (int) Math.floor(Math.log10(number) / 3);
        return String.format("%.2f%s", number / Math.pow(1000, exp), units[exp - 1]);
    }

    public static String format(final double number, final NumberFormatType numberFormatType) {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (numberFormatType == NumberFormatType.MULTIPLIER) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(0);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.COMMAS) {
            numberFormat.setGroupingUsed(true);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.LETTERS) {
            if (number < 1000) return String.valueOf(number);
            final String[] units = new String[]{"K", "M", "B", "T", "Q", "Qt"};
            final int exp = (int) Math.floor(Math.log10(number) / 3);
            return String.format("%.2f%s", number / Math.pow(1000, exp), units[exp - 1]);
        }
        return String.valueOf(number);
    }

    public static String format(final long number, final NumberFormatType numberFormatType) {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (numberFormatType == NumberFormatType.MULTIPLIER) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(0);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.COMMAS) {
            numberFormat.setGroupingUsed(true);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.LETTERS) {
            if (number < 1000) return String.valueOf(number);
            final String[] units = new String[]{"K", "M", "B", "T", "Q", "Qt"};
            final int exp = (int) Math.floor(Math.log10(number) / 3);
            return String.format("%.2f%s", number / Math.pow(1000, exp), units[exp - 1]);
        }
        return String.valueOf(number);
    }
}

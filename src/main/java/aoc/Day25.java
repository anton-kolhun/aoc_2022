package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day25 {

    public static void main(String[] args) throws IOException {
        task1();
    }

    private static void task1() throws IOException {
        List<String> lines = FilesUtilS.readFile("day25.txt");
        long sum = 0;
        for (String line : lines) {
            String snafu = line;
            long decimal = convertToDecimal(line);
            sum += decimal;
        }
        System.out.println(sum);

        String snafu = backToSnafu(sum);
        System.out.println(snafu);
    }

    private static String backToSnafu(long sum) {
        StringBuffer reversed5Bit = new StringBuffer();
        var tsile = sum;

        while (tsile != 0) {
            var newTsile = tsile;
            newTsile = tsile / 5;
            var remaining = tsile % 5;
            reversed5Bit.append(remaining);
            tsile = newTsile;
        }

        System.out.println(new StringBuffer(new String(reversed5Bit)).reverse());


        char[] charArray = reversed5Bit.toString().toCharArray();
        long sumTest = 0;
        for (int i = 0; i < charArray.length; i++) {
            int val = Character.getNumericValue(charArray[i]);
            sumTest = sumTest + val * (long) Math.pow(5, i);

        }
        System.out.println(sumTest);


        int toAdd = 0;
        StringBuffer snafuReversed = new StringBuffer();
        for (char c : reversed5Bit.toString().toCharArray()) {
            int val = Character.getNumericValue(c) + toAdd;
            if (val <= 2) {
                toAdd = 0;
                snafuReversed.append(val);
            } else if (val == 3) {
                snafuReversed.append("=");
                toAdd = 1;
            } else if (val == 4) {
                snafuReversed.append("-");
                toAdd = 1;
            } else {
                snafuReversed.append("0");
                toAdd = 1;
            }
        }
        if (toAdd != 0) {
            snafuReversed.append(toAdd);
        }

        System.out.println(snafuReversed.reverse());
        return "";
    }

    private static long convertToDecimal(String line) {
        int currentDegree = 0;
        long sum = 0;
        char[] charArray = line.toCharArray();
        for (int i = charArray.length - 1; i >= 0; i--) {
            char c = charArray[i];
            long currentVal = 0;
            if (Character.isDigit(c)) {
                long value = Character.getNumericValue(c);
                currentVal = (long) (Math.pow(5, currentDegree) * value);
            } else if (c == '-') {
                currentVal = (long) (Math.pow(5, currentDegree) * -1);
            } else if (c == '=') {
                currentVal = (long) (Math.pow(5, currentDegree) * -2);
            }
            sum += currentVal;
            currentDegree++;
        }
        return sum;
    }

}
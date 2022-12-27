package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day1 {

    public static void main(String[] args) throws IOException {
        task1();
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day1.txt");
        List<Integer> allTotals = new ArrayList<>();
        int currentSum = 0;
        for (String line : lines) {
            if ("".equals(line)) {
                allTotals.add(currentSum);
                currentSum = 0;
            } else {
                currentSum += Integer.parseInt(line);
            }
        }
        allTotals.add(currentSum);

        allTotals.sort(Comparator.comparingInt(o -> o));
        System.out.println(allTotals.get(allTotals.size() - 1));
    }
    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day1.txt");
        List<Integer> allTotals = new ArrayList<>();
        int currentSum = 0;
        for (String line : lines) {
            if ("".equals(line)) {
                allTotals.add(currentSum);
                currentSum = 0;
            } else {
                currentSum += Integer.parseInt(line);
            }
        }
        allTotals.add(currentSum);

        allTotals.sort((o1, o2) -> o2 - o1);
        long val = allTotals.stream()
                .limit(3)
                .mapToInt(value -> value)
                .sum();
        System.out.println(val);
    }
}

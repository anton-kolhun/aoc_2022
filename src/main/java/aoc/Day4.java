package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day4 {

    public static void main(String[] args) throws IOException {
        task2();
    }


    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day4.txt");
        int counter = 0;
        for (String line : lines) {
            List<Integer> group = new ArrayList<>();
            String[] rangeExp = line.split(",");
            addRangesTOoGroup(group, rangeExp[0]);
            addRangesTOoGroup(group, rangeExp[1]);
            if (rangeFullyContained(group)) {
                counter++;
            }
        }
        System.out.println(counter);
    }

    private static boolean rangeFullyContained(List<Integer> group) {
        if (group.get(0) >= group.get(2) && group.get(1) <= group.get(3)) {
            return true;
        }
        if (group.get(0) <= group.get(2) && group.get(1) >= group.get(3)) {
            return true;
        }
        return false;
    }

    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day4.txt");
        int counter = 0;
        for (String line : lines) {
            List<Integer> group = new ArrayList<>();
            String[] rangeExp = line.split(",");
            addRangesTOoGroup(group, rangeExp[0]);
            addRangesTOoGroup(group, rangeExp[1]);
            if (rangeFullyContained(group) || rangeOverlap(group)) {
                counter++;
            }
        }
        System.out.println(counter);
    }

    private static boolean rangeOverlap(List<Integer> group) {
        if (group.get(0) <= group.get(2) && group.get(0) >= group.get(3)) {
            return true;
        }
        if (group.get(0) >= group.get(2) && group.get(0) <= group.get(3)) {
            return true;
        }

        if (group.get(1) <= group.get(2) && group.get(1) >= group.get(3)) {
            return true;
        }
        if (group.get(1) >= group.get(2) && group.get(1) <= group.get(3)) {
            return true;
        }
        return false;
    }


    private static void addRangesTOoGroup(List<Integer> group, String rangeExpr) {
        String[] range = rangeExpr.split("-");
        Integer start = Integer.valueOf(range[0]);
        Integer end = Integer.valueOf(range[1]);
        group.add(start);
        group.add(end);
    }
}

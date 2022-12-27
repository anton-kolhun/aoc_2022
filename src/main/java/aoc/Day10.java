package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day10 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day10.txt");

        int cycleNumber = 0;
        Map<Integer, Integer> cycleToValue = new HashMap<>();
        int value = 1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("addx")) {
                cycleToValue.put(cycleNumber + 1, value);
                var currentValue = Integer.parseInt(line.split(" ")[1]);
                value += currentValue;
                cycleToValue.put(cycleNumber + 2, value);
                cycleNumber += 2;
            } else {
                cycleNumber++;
                cycleToValue.put(cycleNumber, value);
            }
        }
        for (int i = 0; i < 150; i++) {
            int cycle = i + 1;
            System.out.println("cycle = " + cycle + " value = " + cycleToValue.getOrDefault(i, -1));
        }
        int res = calculateSum(List.of(20, 60, 100, 140, 180, 220), cycleToValue);
        System.out.println(res);

    }

    private static int calculateSum(List<Integer> vals, Map<Integer, Integer> cycleToValue) {
        int total = 0;
        for (Integer val : vals) {
            total = val * cycleToValue.get(val - 1) + total;
        }
        return total;

    }


    private static void task2() throws IOException {
        List<String> lines = FilesUtilS.readFile("day10.txt");

        int cycleNumber = 0;
        Map<Integer, Integer> cycleToValue = new HashMap<>();
        int value = 1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("addx")) {
                cycleToValue.put(cycleNumber + 1, value);
                var currentValue = Integer.parseInt(line.split(" ")[1]);
                value += currentValue;
                cycleToValue.put(cycleNumber + 2, value);
                cycleNumber += 2;
            } else {
                cycleNumber++;
                cycleToValue.put(cycleNumber, value);
            }
        }

        int spritePos = 0;
        Map<Integer, String> posToCrt = new HashMap<>();
        for (int i = 0; i < 300; i++) {
            int cursor = i % 40;
            if (cursor == 0) {
                System.out.println();
            }
            String val;
            cursor = cursor + 1;
            if (cursor == spritePos || cursor == spritePos + 1 || cursor == spritePos + 2) {
                val = "#";
            } else {
                val = ".";
            }
            posToCrt.put(i, val);
            int cycle = i + 1;
            spritePos = cycleToValue.getOrDefault(cycle, -1);
            System.out.print(val);
        }

     /*   for (int i = 0; i < 150; i++) {
            System.out.print(posToCrt.get(i));
            if (i != 0 && (i % 39) == 0) {
                System.out.println();
            }
        }*/



       /* for (int i = 0; i < 150; i++) {
            int cycle = i + 1;
            System.out.println("cycle = " + cycle + " value = " + cycleToValue.getOrDefault(i, -1));
        }
        int res = calculateSum(List.of(20, 60, 100, 140, 180, 220), cycleToValue);
        System.out.println(res);*/

    }
}

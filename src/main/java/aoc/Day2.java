package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Day2 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        Map<String, Integer> game = Map.of(
                "A X", 4, "A Y", 8, "A Z", 3,
                "B X", 1, "B Y", 5, "B Z", 9,
                "C X", 7, "C Y", 2, "C Z", 6);
        List<String> lines = FilesUtilS.readFile("day2.txt");
        int counter = 0;
        for (String line : lines) {
            counter += game.get(line);
        }
        System.out.println(counter);
    }

    private static void task2() {
        Map<String, Integer> game = Map.of(
                "A X", 3, "A Y", 4, "A Z", 8,
                "B X", 1, "B Y", 5, "B Z", 9,
                "C X", 2, "C Y", 6, "C Z", 7);
        List<String> lines = FilesUtilS.readFile("day2.txt");
        int counter = 0;
        for (String line : lines) {
            counter += game.get(line);
        }
        System.out.println(counter);
    }
}
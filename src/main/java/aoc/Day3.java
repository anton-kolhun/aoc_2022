package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day3.txt");
        List<Character> missmatches = new ArrayList<>();
        for (String line : lines) {
            Set<Character> rowMissmatches = new HashSet<>();
            char[] charArray = line.toCharArray();
            Set<Character> firstHalf = new HashSet<>();
            for (int i = 0; i < charArray.length / 2; i++) {
                firstHalf.add(charArray[i]);
            }
            for (int i = charArray.length / 2; i < charArray.length; i++) {
                if (firstHalf.contains(charArray[i])) {
                    rowMissmatches.add(charArray[i]);
                }
            }
            missmatches.addAll(rowMissmatches);

        }
        int sum = 0;
        for (Character missmatch : missmatches) {
            int val;
            if (Character.isLowerCase(missmatch)) {
                val = missmatch - 96;
            } else {
                val = missmatch - 64 + 26;
            }
            System.out.println(missmatch + " - " + val);
            sum += val;
        }
        System.out.println(sum);
    }


    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day3.txt");
        List<Character> groupChars = new ArrayList<>();
        List<Set<Character>> groupData = new ArrayList<>();
        for (int j = 0; j < lines.size(); j++) {
            if (j != 0 && (j % 3) == 0) {
                for (Character character : groupData.get(0)) {
                    if (groupData.get(1).contains(character) &&
                            groupData.get(2).contains(character)) {
                        groupChars.add(character);
                        break;
                    }
                }
                groupData = new ArrayList<>();
            }
            String line = lines.get(j);
            Set<Character> values = new HashSet<>();
            for (char c : line.toCharArray()) {
                values.add(c);
            }
            groupData.add(values);

        }
        for (Character character : groupData.get(0)) {
            if (groupData.get(1).contains(character) &&
                    groupData.get(2).contains(character)) {
                groupChars.add(character);
                break;
            }
        }


        int sum = 0;
        for (Character groupChar : groupChars) {
            int val;
            if (Character.isLowerCase(groupChar)) {
                val = groupChar - 96;
            } else {
                val = groupChar - 64 + 26;
            }
            System.out.println(groupChar + " - " + val);
            sum += val;
        }
        System.out.println(sum);
    }
}

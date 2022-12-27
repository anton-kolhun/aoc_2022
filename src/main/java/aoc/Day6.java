package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day6 {

    public static void main(String[] args) throws IOException {
        task1();

    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day6.txt");
        int total = 0;
        for (String line : lines) {
            int charsNumber = processLine(line);
            total += charsNumber;
        }
        System.out.println(total);
    }

    private static int processLine(String line) {
        Map<Character, Integer> charToPos = new HashMap<>();
        char[] charArray = line.toCharArray();
        int startWindow = 0;
        int uniqueInRow = 0;
        int cursor;
        for (cursor = 0; cursor < charArray.length; cursor++) {
            Character c = charArray[cursor];
            int pos = charToPos.getOrDefault(c, -1);
            if (pos >= startWindow) {
                startWindow = pos + 1;
                uniqueInRow = cursor - startWindow + 1;
                charToPos.put(c, cursor);
            } else {
                uniqueInRow++;
                charToPos.put(c, cursor);
                if (uniqueInRow == 14) {
                    break;
                }
            }
        }

        return cursor + 1;

    }
}

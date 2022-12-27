package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day8 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day8.txt");
        int rows = lines.size();
        int cols = lines.get(0).length();
        Map<Coordintate, Integer> coordToVals = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                coordToVals.put(new Coordintate(row, col), Character.getNumericValue(c));
            }
        }
        Set<Coordintate> visible = new HashSet<>();
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < cols - 1; col++) {
                var coord = new Coordintate(row, col);
                coord.value = coordToVals.get(coord);
                boolean vis = isVisible(coord, coordToVals, rows, cols);
                if (vis) {
                    visible.add(coord);
                }
            }
        }
        int perimeter = (rows + cols) * 2 - 4;
        int total = perimeter + visible.size();
        System.out.println("total visible = " + total);
    }

    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day8.txt");
        int rows = lines.size();
        int cols = lines.get(0).length();
        Map<Coordintate, Integer> coordToVals = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                coordToVals.put(new Coordintate(row, col), Character.getNumericValue(c));
            }
        }
        Map<Coordintate, Integer> coordToScore = new HashMap<>();
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < cols - 1; col++) {
                var coord = new Coordintate(row, col);
                coord.value = coordToVals.get(coord);
                int score = calculateScore(coord, coordToVals, rows, cols);
                coordToScore.put(coord, score);
            }
        }

        Coordintate max = new Coordintate(0,0);
        int maxVal = 0;
        for (Map.Entry<Coordintate, Integer> entry : coordToScore.entrySet()) {
            if (entry.getValue() > maxVal) {
                max = entry.getKey();
                maxVal = entry.getValue();
            }
        }

        System.out.printf("best coord row=%s, col=%s; Value = %s ", max.row, max.column, maxVal);
    }

    private static int calculateScore(Coordintate coordintate, Map<Coordintate, Integer> coordToVals, int rows, int cols) {

        int scoreFromTop = 0;
        for (int row = coordintate.row - 1; row >= 0; row--) {
            scoreFromTop++;
            if (coordToVals.get(new Coordintate(row, coordintate.column)) >= coordToVals.get(coordintate)) {
                break;
            }
        }


        int scoreFromBottom = 0;
        for (int row = coordintate.row + 1; row < rows; row++) {
            scoreFromBottom++;
            if (coordToVals.get(new Coordintate(row, coordintate.column)) >= coordToVals.get(coordintate)) {
                break;
            }
        }

        int scoreFromLeft = 0;
        for (int col = coordintate.column - 1; col >= 0; col--) {
            scoreFromLeft++;
            if (coordToVals.get(new Coordintate(coordintate.row, col)) >= coordToVals.get(coordintate)) {
                break;
            }
        }

        int scoreFromRight = 0;
        for (int col = coordintate.column + 1; col < cols; col++) {
            scoreFromRight++;
            if (coordToVals.get(new Coordintate(coordintate.row, col)) >= coordToVals.get(coordintate)) {
                break;
            }
        }
        int total = scoreFromTop * scoreFromBottom * scoreFromRight * scoreFromLeft;
        return total;
    }

    private static boolean isVisible(Coordintate coordintate, Map<Coordintate, Integer> coordToVals, int rows, int cols) {
        boolean visibleFromTop = true;
        for (int row = coordintate.row - 1; row >= 0; row--) {
            if (coordToVals.get(new Coordintate(row, coordintate.column)) >= coordToVals.get(coordintate)) {
                visibleFromTop = false;
                break;
            }
        }
        if (visibleFromTop) {
            return true;
        }

        boolean visibleFromBottom = true;
        for (int row = coordintate.row + 1; row < rows; row++) {
            if (coordToVals.get(new Coordintate(row, coordintate.column)) >= coordToVals.get(coordintate)) {
                visibleFromBottom = false;
                break;
            }
        }
        if (visibleFromBottom) {
            return true;
        }

        boolean visibleFromLeft = true;
        for (int col = coordintate.column - 1; col >= 0; col--) {
            if (coordToVals.get(new Coordintate(coordintate.row, col)) >= coordToVals.get(coordintate)) {
                visibleFromLeft = false;
                break;
            }
        }
        if (visibleFromLeft) {
            return true;
        }

        boolean visibleFromRight = true;
        for (int col = coordintate.column + 1; col < cols; col++) {
            if (coordToVals.get(new Coordintate(coordintate.row, col)) >= coordToVals.get(coordintate)) {
                visibleFromRight = false;
                break;
            }
        }
        if (visibleFromRight) {
            return true;
        }
        return false;
    }

    private static class Coordintate {
        int row;
        int column;
        int value;

        public Coordintate(int row, int column, int value) {
            this.row = row;
            this.column = column;
            this.value = value;
        }

        public Coordintate(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Coordintate that = (Coordintate) o;

            if (row != that.row) return false;
            return column == that.column;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + column;
            return result;
        }
    }

}
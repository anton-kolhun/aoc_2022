package aoc;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day14 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day14.txt");
        Map<Coordinate, String> coordToVal = new HashMap<>();
        for (String line : lines) {
            String[] values = line.split(" -> ");
            for (int i = 0; i < values.length - 1; i++) {
                String val1 = values[i];
                String[] coords1 = val1.split(",");
                int coord1Row = Integer.parseInt(coords1[1]);
                int coord1Col = Integer.parseInt(coords1[0]);
                var coord1 = new Coordinate(coord1Row, coord1Col);
                String val2 = values[i + 1];
                String[] coords2 = val2.split(",");
                int coord2Row = Integer.parseInt(coords2[1]);
                int coord2Col = Integer.parseInt(coords2[0]);
                var coord2 = new Coordinate(coord2Row, coord2Col);
                fillCoordinates(coordToVal, coord1, coord2);
            }
        }

        handleSand(coordToVal);


    }

    private static void fillCoordinates(Map<Coordinate, String> coordToVal, Coordinate coord1, Coordinate coord2) {
        if (coord1.row == coord2.row) {
            Coordinate move = new Coordinate(0, 1);
            Comparator<Coordinate> comp = Comparator.comparing(coord -> coord.col);
            int value = comp.compare(coord1, coord2);
            if (value <= 0) {
                fillCoords(move, coord1, Math.abs(coord1.col - coord2.col), coordToVal);
            } else {
                fillCoords(move, coord2, Math.abs(coord1.col - coord2.col), coordToVal);
            }
        } else {
            Coordinate move = new Coordinate(1, 0);
            Comparator<Coordinate> comp = Comparator.comparing(coord -> coord.row);
            int value = comp.compare(coord1, coord2);
            if (value <= 0) {
                fillCoords(move, coord1, Math.abs(coord1.row - coord2.row), coordToVal);
            } else {
                fillCoords(move, coord2, Math.abs(coord1.row - coord2.row), coordToVal);
            }
        }
    }

    private static void handleSand(Map<Coordinate, String> coordToVal) {
        int step = 0;
        var sandSource = new Coordinate(0, 500);
        boolean shouldContinue = true;
        while (shouldContinue) {
            var res = moveRecursively(sandSource, coordToVal);
            if (res == null || res == Coordinate.endless) {
                shouldContinue = false;
                continue;
            }
            step++;
            System.out.println(step);
            //print(coordToVal);
        }

    }

    private static Coordinate moveRecursively(Coordinate cursor, Map<Coordinate, String> coordToVal) {
        //comment out for task2
       /* if (cursor.row > 9) {
            return Coordinate.endless;
        }*/
        if (coordToVal.containsKey(cursor)) {
            return null;
        }
        var nextCursor = new Coordinate(cursor.row + 1, cursor.col);
        var res = moveRecursively(nextCursor, coordToVal);
        if (res != null) {
            return res;
        }

        nextCursor = new Coordinate(cursor.row + 1, cursor.col - 1);
        res = moveRecursively(nextCursor, coordToVal);
        if (res != null) {
            return res;
        }
        nextCursor = new Coordinate(cursor.row + 1, cursor.col + 1);
        res = moveRecursively(nextCursor, coordToVal);
        if (res != null) {
            return res;
        }
        coordToVal.put(cursor, "0");
        return cursor;
    }


    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day14.txt");
        Map<Coordinate, String> coordToVal = new HashMap<>();
        int maxRow = 0;
        for (String line : lines) {
            String[] values = line.split(" -> ");
            for (int i = 0; i < values.length - 1; i++) {
                String val1 = values[i];
                String[] coords1 = val1.split(",");
                int coord1Row = Integer.parseInt(coords1[1]);
                int coord1Col = Integer.parseInt(coords1[0]);
                var coord1 = new Coordinate(coord1Row, coord1Col);
                String val2 = values[i + 1];
                String[] coords2 = val2.split(",");
                int coord2Row = Integer.parseInt(coords2[1]);
                int coord2Col = Integer.parseInt(coords2[0]);
                var coord2 = new Coordinate(coord2Row, coord2Col);
                fillCoordinates(coordToVal, coord1, coord2);
                var max = Math.max(coord1Row, coord2Row);
                if (max > maxRow) {
                    maxRow = max;
                }
            }
        }

        for (int col = -1000; col < 1000; col++) {
            var toFill = new Coordinate(maxRow + 2, col);
            coordToVal.put(toFill, "#");
        }

        handleSand(coordToVal);


    }


    private static void print(Map<Coordinate, String> coordToVal) {

        for (int row = 0; row < 10; row++) {
            for (int col = 494; col <= 503; col++) {
                String value = coordToVal.getOrDefault(new Coordinate(row, col), ".");
                System.out.print(value);
            }
            System.out.println();
        }
    }

    private static void fillCoords(Coordinate move, Coordinate start, int steps, Map<Coordinate, String> coordToVal) {
        coordToVal.put(start, "#");
        var cursor = start;

        for (int i = 0; i < steps; i++) {
            var next = new Coordinate(cursor.row + move.row, cursor.col + move.col);
            coordToVal.put(next, "#");
            cursor = next;
        }
    }

    @ToString
    static class Coordinate {

        private static Coordinate endless = new Coordinate(-5000, -5000);

        int row;
        int col;
        int value;

        public Coordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public Coordinate(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Coordinate that = (Coordinate) o;

            if (row != that.row) return false;
            return col == that.col;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }
    }
}

package aoc;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day22Part1 {


    public static void main(String[] args) throws IOException {
        task1();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day22.txt");
        Map<Coordinate, String> coordToValue = new HashMap<>();
        Map<Integer, Integer> rowToColStart = new HashMap<>();
        Map<Integer, Integer> rowToColEnd = new HashMap<>();
        int row;
        int maxCol = 0;
        for (row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            if (line.isEmpty()) {
                break;
            }
            char[] charArray = line.toCharArray();
            boolean started = false;
            maxCol = Math.max(maxCol, charArray.length);
            int col;
            for (col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                String value = String.valueOf(c).trim();
                if (!value.isEmpty()) {
                    coordToValue.put(new Coordinate(row, col), value);
                    if (!started) {
                        rowToColStart.put(row, col);
                    }
                    started = true;
                }
            }
            rowToColEnd.put(row, col - 1);
        }

        Map<Integer, Integer> colToRowStart = new HashMap<>();
        Map<Integer, Integer> colToRowEnd = new HashMap<>();
        int maxRow = row;
        populateColToRowStart(coordToValue, colToRowStart, colToRowEnd, maxRow, maxCol);

        String pass = lines.get(row + 1);
        List<String> moves = parsePass(pass);
        System.out.println(coordToValue);
        int colStart = rowToColStart.get(0);


        navigate(new Coordinate(0, colStart), coordToValue, rowToColStart, rowToColEnd, moves, Position.RIGHT,
                colToRowStart, colToRowEnd, maxRow, maxCol);

    }

    private static void populateColToRowStart(Map<Coordinate, String> coordToValue, Map<Integer, Integer> colToRowStart,
                                              Map<Integer, Integer> colToRowEnd, int maxRow, int maxCol) {
        for (int col = 0; col <= maxCol; col++) {
            boolean started = false;
            for (int row = 0; row <= maxRow; row++) {
                if (!started && coordToValue.get(new Coordinate(row, col)) != null) {
                    colToRowStart.put(col, row);
                    started = true;
                    continue;
                }
                if (started && !coordToValue.containsKey(new Coordinate(row, col))) {
                    colToRowEnd.put(col, row - 1);
                    break;
                }
            }
        }
    }

    private static void navigate(Coordinate current, Map<Coordinate, String> coordToValue, Map<Integer, Integer> rowToColStart,
                                 Map<Integer, Integer> rowToColEnd, List<String> moves, Position initialPos,
                                 Map<Integer, Integer> colToRowStart, Map<Integer, Integer> colToRowEnd, int maxRow, int maxCol) {
        var pos = initialPos;
        for (String move : moves) {
            try {
                int steps = Integer.parseInt(move);
                current = doSteps(current, pos, steps, coordToValue, rowToColStart, rowToColEnd, colToRowStart, colToRowEnd);
                //printMatrix(current, coordToValue, maxRow, maxCol, pos);
                //System.out.printf("Steps = %s, Position = %s", steps, pos.toString());
                //System.out.println();
            } catch (Exception e) {
                int newVal;
                if (move.equals("R")) {
                    newVal = pos.value + 1;
                } else {
                    newVal = pos.value - 1;
                }
                pos = Position.fromValue(newVal);
            }
        }
        int formual = 1000 * (current.row + 1) + 4 * (current.column + 1) + pos.value;
        System.out.println(formual);
    }

    public static void printMatrix(Coordinate current, Map<Coordinate, String> coordToValue, int maxRow, int maxCol, Position pos) {
        System.out.println();
        for (int row = 0; row <= maxRow; row++) {
            for (int col = 0; col <= maxCol; col++) {
                var coord = new Coordinate(row, col);
                if (coord.equals(current)) {
                    if (pos == Position.RIGHT) {
                        System.out.print(">");
                    } else if (pos == Position.LEFT) {
                        System.out.print("<");
                    } else if (pos == Position.UP) {
                        System.out.print("^");
                    } else if (pos == Position.DOWN) {
                        System.out.print("v");
                    }
                    continue;
                }
                var value = coordToValue.getOrDefault(new Coordinate(row, col), " ");
                System.out.print(value);
            }
            System.out.println();
        }
    }


    private static Coordinate doSteps(Coordinate current, Position pos, int steps, Map<Coordinate, String> coordToValue,
                                      Map<Integer, Integer> rowToColStart, Map<Integer, Integer> rowToColEnd,
                                      Map<Integer, Integer> colToRowStart, Map<Integer, Integer> colToRowEnd) {
        Coordinate move = null;
        if (pos == Position.RIGHT) {
            move = new Coordinate(0, 1);
        } else if (pos == Position.LEFT) {
            move = new Coordinate(0, -1);
        } else if (pos == Position.DOWN) {
            move = new Coordinate(1, 0);
        } else if (pos == Position.UP) {
            move = new Coordinate(-1, 0);
        }
        for (int i = 0; i < steps; i++) {
            var nextCoord = new Coordinate(current.row + move.row, current.column + move.column);
            var value = coordToValue.getOrDefault(nextCoord, "teleport");
            if (value.equals("#")) {
                return current;
            }
            if (value.equals("teleport")) {
                Coordinate teleported = null;
                if (pos == Position.RIGHT) {
                    teleported = new Coordinate(current.row, rowToColStart.get(current.row));
                } else if (pos == Position.LEFT) {
                    teleported = new Coordinate(current.row, rowToColEnd.get(current.row));
                } else if (pos == Position.DOWN) {
                    teleported = new Coordinate(colToRowStart.get(current.column), current.column);
                } else if (pos == Position.UP) {
                    teleported = new Coordinate(colToRowEnd.get(current.column), current.column);
                }
                if (coordToValue.get(teleported).equals("#")) {
                    return current;
                }
                current = teleported;
            } else {
                current = nextCoord;
            }
        }

        return current;
    }

    private static List<String> parsePass(String pass) {
        int cursorDigit = 0;
        int lastLetterIndex = 0;
        List<String> moves = new ArrayList<>();
        char[] charArray = pass.toCharArray();
        int i;
        for (i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isLetter(c)) {
                String previous = pass.substring(cursorDigit, i);
                moves.add(previous);
                moves.add(String.valueOf(c));
                lastLetterIndex = i;
                cursorDigit = i + 1;
            }
        }
        String lastNumber = pass.substring(lastLetterIndex + 1, pass.length());
        moves.add(lastNumber);
        return moves;
    }

    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Coordinate {
        int row;
        int column;
    }

    public enum Position {
        LEFT(2), RIGHT(0), DOWN(1), UP(3);

        public int value;

        Position(int value) {
            this.value = value;
        }

        static Position fromValue(int value) {
            if (value < 0) {
                value = values().length + value;
            }
            value = value % values().length;
            for (Position position : values()) {
                if (position.value == value) {
                    return position;
                }
            }
            throw new RuntimeException("bad");
        }

    }
}

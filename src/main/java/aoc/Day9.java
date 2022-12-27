package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day9 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<Coordinate> path = new ArrayList<>();
        path.add(new Coordinate(0, 0));
        Coordinate head = new Coordinate(0, 0);
        List<String> lines = FilesUtilS.readFile("day9.txt");
        for (String line : lines) {
            String[] moveData = line.split(" ");
            int steps = Integer.parseInt(moveData[1]);
            var move = resolveMove(moveData);
            head = makeSteps(move, steps, head, path);
        }
        Set<Coordinate> unique = new HashSet<>(path);
        System.out.println(unique.size());
    }

    private static Coordinate makeSteps(Coordinate move, int steps, Coordinate head, List<Coordinate> path) {
        var tail = path.get(path.size() - 1);
        for (int i = 0; i < steps; i++) {
            var previousHead = head;
            head = new Coordinate(head.row + move.row, head.column + move.column);
            if (head.row == tail.row && (Math.abs(head.column - tail.column) > 1)) {
                tail = previousHead;
                path.add(tail);
                continue;
            }
            if (head.column == tail.column && (Math.abs(head.row - tail.row) > 1)) {
                tail = previousHead;
                path.add(tail);
                continue;
            }
            if (Math.abs(head.row - tail.row) + Math.abs(head.column - tail.column) > 2) {
                tail = previousHead;
                path.add(tail);
                continue;
            }
        }
        return head;
    }

    private static Coordinate resolveMove(String[] moveData) {
        Coordinate move = null;
        if (moveData[0].equals("R")) {
            move = new Coordinate(0, 1);
        } else if (moveData[0].equals("L")) {
            move = new Coordinate(0, -1);
        } else if (moveData[0].equals("D")) {
            move = new Coordinate(-1, 0);
        } else if (moveData[0].equals("U")) {
            move = new Coordinate(1, 0);
        }
        return move;
    }


    private static void task2() {
        List<List<Coordinate>> path = new ArrayList<>();
        List<Coordinate> state = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            state.add(new Coordinate(0, 0, i));
        }
        path.add(state);
        List<String> lines = FilesUtilS.readFile("day9.txt");
        for (String line : lines) {
            String[] moveData = line.split(" ");
            int steps = Integer.parseInt(moveData[1]);
            var move = resolveMove(moveData);
            var states = makeSteps2(move, steps, path.get(path.size() - 1));
            path.addAll(states);
        }


        Set<Coordinate> unique = new HashSet<>();
        for (List<Coordinate> coords : path) {
            Coordinate coord = coords.get(9);
            unique.add(coord);
        }

        System.out.println(unique.size());
    }

    private static void printPath(List<Coordinate> coords) {
        Map<Coordinate, List<Integer>> coordToValue = new HashMap<>();
        for (Coordinate coordintate : coords) {
            coordToValue.put(coordintate, new ArrayList<>());
        }
        for (Coordinate coordintate : coords) {
            var list = coordToValue.get(coordintate);
            var newList = new ArrayList<>(list);
            list.add(coordintate.value);
            coordToValue.put(coordintate, list);
        }
        for (int row = 15; row >= -15; row--) {
            for (int col = -15; col <= 15; col++) {
                List<Integer> indexes = coordToValue.getOrDefault(new Coordinate(row, col), new ArrayList<>());
                var min = Integer.MAX_VALUE;
                for (Integer index : indexes) {
                    if (index < min) {
                        min = index;
                    }
                }

                String val;
                if (min == Integer.MAX_VALUE) {
                    val = ".";
                } else if (min == 0) {
                    val = "H";
                } else {
                    val = String.valueOf(min);
                }
                System.out.print(val);
            }
            System.out.println();
        }
    }

    private static List<List<Coordinate>> makeSteps2(Coordinate move, int steps, List<Coordinate> state) {
        List<List<Coordinate>> allStates = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            var previosState = state;
            state = clone(state);
            for (int j = 0; j < state.size() - 1; j++) {
                Coordinate coordAhead = state.get(j);
                Coordinate coordBehind = state.get(j + 1);
                if (j == 0) {
                    coordAhead.row = coordAhead.row + move.row;
                    coordAhead.column = coordAhead.column + move.column;
                }
                if (coordAhead.row == coordBehind.row && (Math.abs(coordAhead.column - coordBehind.column) > 1)) {
                    coordBehind.column = previosState.get(coordAhead.value).column;
                    continue;
                }
                if (coordAhead.column == coordBehind.column && (Math.abs(coordAhead.row - coordBehind.row) > 1)) {
                    coordBehind.row = previosState.get(coordAhead.value).row;
                    continue;
                }
                if (Math.abs(coordAhead.row - coordBehind.row) + Math.abs(coordAhead.column - coordBehind.column) > 2) {
                    int rowMove = 0;
                    if (coordAhead.row > coordBehind.row) {
                        rowMove = 1;
                    } else if (coordAhead.row < coordBehind.row) {
                        rowMove = -1;
                    }
                    coordBehind.row = coordBehind.row + rowMove;

                    int colMove = 0;
                    if (coordAhead.column > coordBehind.column) {
                        colMove = 1;
                    } else if (coordAhead.column < coordBehind.column) {
                        colMove = -1;
                    }
                    coordBehind.column = coordBehind.column + colMove;
                }
            }
            allStates.add(state);
//            printPath(state);
//            System.out.println();
        }
        return allStates;
    }

    private static List<Coordinate> clone(List<Coordinate> state) {
        List<Coordinate> newState = new ArrayList<>();
        for (Coordinate coordintate : state) {
            newState.add(new Coordinate(coordintate.row, coordintate.column, coordintate.value));
        }
        return newState;
    }


    private static Coordinate makeSteps3(Coordinate move, int steps, Coordinate head, List<Coordinate> path,
                                         Coordinate tail) {
        for (int i = 0; i < steps; i++) {
            var previousHead = head;
            head = new Coordinate(head.row + move.row, head.column + move.column);
            if (head.row == tail.row && (Math.abs(head.column - tail.column) > 1)) {
                tail = previousHead;
                path.add(tail);
                continue;
            }
            if (head.column == tail.column && (Math.abs(head.row - tail.row) > 1)) {
                tail = previousHead;
                path.add(tail);
                continue;
            }
            if (Math.abs(head.row - tail.row) + Math.abs(head.column - tail.column) > 2) {
                tail = previousHead;
                path.add(tail);
                continue;
            }
        }
        return head;
    }

    private static void updateTail(Coordinate previousHead, List<List<Coordinate>> path) {
        var tail = path.get(path.size() - 1);
        List<Coordinate> newTail = new ArrayList<>();
        for (Coordinate coordintate : tail) {

        }

    }


    private static class Coordinate {
        int row;
        int column;
        int value;

        public Coordinate(int row, int column, int value) {
            this.row = row;
            this.column = column;
            this.value = value;
        }

        public Coordinate(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Coordinate that = (Coordinate) o;

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

package aoc;

import lombok.ToString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day17Part1 {

    public static void main(String[] args) throws IOException {
        task();
    }


    private static void task() throws IOException {
        List<String> lines = FilesUtilS.readFile("day17.txt");

        int floorRow = 4;

        String motions = lines.get(0);
        Set<Coordinate> floor = new HashSet<>();
        for (int width = 0; width < 7; width++) {
            floor.add(new Coordinate(floorRow, width));
        }

        List<Shape> shapes = List.of(new HorizontalLine(), new Cross(), new LShape(), new VerticalLine(), new Square());
        var initialShift = new Move(0, 2);
        int absoluteCounter = 0;
        int previous = 0;
        int remaining = 0;
        for (int step = 0; step < 3000; step++) {
            var shape = shapes.get(step % 5);
            shape = shape.move(initialShift);
            //follow motion

            absoluteCounter = moveShape(motions, floor, absoluteCounter, shape, floorRow, step);
            if (step == 1220) {
                int score = calculateHeight(floor);
                System.out.println("step:" + step);
                System.out.println("initial:" + score);
                // draw(floor, step);
            }
            if (step == (1220 + 1700)) {
                int score = calculateHeight(floor);
                System.out.println("your_score:" + (score - 1912 - 1));
            }
            if ((step - 1220) % 1740 == 0) {
                int score = calculateHeight(floor);
                System.out.println("step:" + step);
                System.out.println("end of cycle:" + (score - previous));
                previous = score;
                //draw(floor, step);
            }
            int height = calculateHeight(floor);

        }

        int height = calculateHeight(floor);
        System.out.println(height);

    }

    private static int calculateHeight(Set<Coordinate> floor) {
        int maxRow = Integer.MIN_VALUE;
        for (Coordinate coordinate : floor) {
            if (coordinate.row > maxRow) {
                maxRow = coordinate.row;
            }
        }
        int height = maxRow - 4;
        return height;
    }

    private static Integer moveShape(String motions, Set<Coordinate> floor, int absoluteCountr, Shape shape, int floorRow, int step) {
        boolean shouldStop = false;
        Shape updated = shape;
        while (!shouldStop) {
            shape = updated;
            Move move = resolveMove(motions, absoluteCountr);
            updated = shape.move(move);
            if (isValidMoveRightOrLeft(updated, floor)) {
                shape = updated;
            }
            //move down
            updated = shape.move(new Move(1, 0));
            shouldStop = shouldShapeStop(updated, floor);
            absoluteCountr++;
        }

        int highestCoord = Integer.MAX_VALUE;
        for (Coordinate coordinate : updated.getCoordinates()) {
            if (coordinate.row < highestCoord) {
                highestCoord = coordinate.row;
            }
        }
        rebuildFloor(floor, shape, floorRow);
        //draw(floor, step);

        return absoluteCountr;
    }

    private static void draw(Set<Coordinate> floor, int step) {
        System.out.println("---- STEP " + step + "---------------");
        System.out.println("score = " + calculateHeight(floor));
        // calculateHeight(floor);
        for (int row = 4; row < 2000; row++) {
            for (int col = 0; col < 7; col++) {
                String val = ".";
                if (floor.contains(new Coordinate(row, col))) {
                    val = "#";
                }
                System.out.print(val);
            }
            System.out.println();
        }
        System.out.println();

    }

    private static void rebuildFloor(Set<Coordinate> floor, Shape state, int floorRow) {
        int highestCoordValue = Integer.MAX_VALUE;
        Coordinate highestCoord = null;
        for (Coordinate coordinate : state.getCoordinates()) {
            if (coordinate.row < highestCoordValue) {
                highestCoordValue = coordinate.row;
                highestCoord = coordinate;
            }
        }
        Set<Coordinate> newFloor = new HashSet<>();
        if (highestCoord.row < floorRow) {
//        if (highestCoord.row <= floorRow && !suitPerfectly(floor, state)) {
            for (Coordinate coordinate : state.getCoordinates()) {
                var diffFromTop = coordinate.row - highestCoord.row;
                newFloor.add(new Coordinate(floorRow + diffFromTop, coordinate.col));
            }
            for (Coordinate coordinate : floor) {
                var diff = coordinate.row - highestCoord.row;
                newFloor.add(new Coordinate(floorRow + diff, coordinate.col));
            }
        } else {
            newFloor.addAll(floor);
            newFloor.addAll(state.getCoordinates());
        }


        floor.clear();
        floor.addAll(newFloor);
    }

    private static boolean suitPerfectly(Set<Coordinate> floor, Shape state) {
        for (Coordinate coordinate : state.getCoordinates()) {
            if (floor.contains(coordinate)) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldShapeStop(Shape state, Set<Coordinate> floor) {
        for (Coordinate coordinate : state.getCoordinates()) {
            if (floor.contains(new Coordinate(coordinate.row, coordinate.col))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidMoveRightOrLeft(Shape state, Set<Coordinate> floor) {
        for (Coordinate coordinate : state.getCoordinates()) {
            if (isBeyondWidth(coordinate)) {
                return false;
            }
            if (floor.contains(coordinate)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isBeyondWidth(Coordinate coordinate) {
        if (coordinate.col < 0 || coordinate.col > 6) {
            return true;
        }
        return false;
    }

    private static Move resolveMove(String motions, int step) {
        if (step > motions.length()) {
            System.out.print("");
        }
        step = step % motions.length();
        char motion = motions.charAt(step);
        if (motion == '>') {
            return new Move(0, 1);
        } else {
            return new Move(0, -1);
        }
    }


    static abstract class Shape {
        abstract public List<Coordinate> getCoordinates();

        abstract public Shape move(Move move);

        public List<Coordinate> doMoveCoords(Move move) {
            List<Coordinate> newCoords = new ArrayList<>();
            for (Coordinate coordinate : getCoordinates()) {
                var newCoord = new Coordinate();
                newCoord.col = coordinate.col + move.col;
                newCoord.row = coordinate.row + move.row;
                newCoords.add(newCoord);
            }
            return newCoords;
        }
    }


    private static class Square extends Shape {

        @Override
        public Shape move(Move move) {
            List<Coordinate> newCoords = doMoveCoords(move);
            return new Square(newCoords);
        }

        private List<Coordinate> coords = new ArrayList<>();


        public Square() {
            coords = List.of(
                    new Coordinate(0, 0),
                    new Coordinate(-1, 0),
                    new Coordinate(0, 1),
                    new Coordinate(-1, 1));
        }

        public Square(List<Coordinate> coords) {
            this.coords = coords;
        }

        @Override
        public List<Coordinate> getCoordinates() {
            return coords;
        }
    }

    private static class VerticalLine extends Shape {

        @Override
        public Shape move(Move move) {
            List<Coordinate> newCoords = doMoveCoords(move);
            return new VerticalLine(newCoords);
        }

        private List<Coordinate> coords = new ArrayList<>();


        public VerticalLine() {
            coords = List.of(
                    new Coordinate(0, 0),
                    new Coordinate(-1, 0),
                    new Coordinate(-2, 0),
                    new Coordinate(-3, 0));
        }

        public VerticalLine(List<Coordinate> coords) {
            this.coords = coords;
        }

        @Override
        public List<Coordinate> getCoordinates() {
            return coords;
        }
    }


    private static class LShape extends Shape {

        @Override
        public Shape move(Move move) {
            List<Coordinate> newCoords = doMoveCoords(move);
            return new LShape(newCoords);
        }

        private List<Coordinate> coords = new ArrayList<>();


        public LShape() {
            coords = List.of(
                    new Coordinate(0, 0),
                    new Coordinate(0, 1),
                    new Coordinate(0, 2),
                    new Coordinate(-1, 2),
                    new Coordinate(-2, 2));
        }

        public LShape(List<Coordinate> coords) {
            this.coords = coords;
        }

        @Override
        public List<Coordinate> getCoordinates() {
            return coords;
        }
    }

    private static class HorizontalLine extends Shape {

        @Override
        public Shape move(Move move) {
            List<Coordinate> newCoords = doMoveCoords(move);
            return new HorizontalLine(newCoords);
        }

        private List<Coordinate> coords = new ArrayList<>();


        public HorizontalLine() {
            coords.add(new Coordinate(0, 0));
            coords.add(new Coordinate(0, 1));
            coords.add(new Coordinate(0, 2));
            coords.add(new Coordinate(0, 3));
        }

        public HorizontalLine(List<Coordinate> coords) {
            this.coords = coords;
        }

        @Override
        public List<Coordinate> getCoordinates() {
            return coords;
        }
    }

    private static class Cross extends Shape {

        @Override
        public Shape move(Move move) {
            List<Coordinate> coords = doMoveCoords(move);
            return new Cross(coords);
        }

        private List<Coordinate> coords = new ArrayList<>();


        public Cross(List<Coordinate> coords) {
            this.coords = coords;
        }

        public Cross() {
            coords.add(new Coordinate(0, 1));
            coords.add(new Coordinate(-1, 0));
            coords.add(new Coordinate(-1, 1));
            coords.add(new Coordinate(-1, 2));
            coords.add(new Coordinate(-2, 1));
        }

        @Override
        public List<Coordinate> getCoordinates() {
            return coords;
        }
    }

    @ToString
    private static class Coordinate {
        private int row;
        private int col;


        public Coordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public Coordinate() {
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


    @ToString
    private static class Move {
        private int row;
        private int col;


        public Move(int row, int col) {
            this.row = row;
            this.col = col;
        }

    }
}

package aoc;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Day24 {

    static List<Coordinate> stub = new ArrayList<>();

    public static List<Coordinate> moves = List.of(
            new Coordinate(0, 1),
            new Coordinate(1, 0),
            new Coordinate(0, 0),
            new Coordinate(-1, 0),
            new Coordinate(0, -1)
    );

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() throws IOException {
        List<String> lines = FilesUtilS.readFile("day24.txt");
        int maxRow = -1;
        Map<Coordinate, List<Blizzard>> coordToBlizzard = new HashMap<>();
        Set<Coordinate> possibleCoords = new HashSet<>();
        for (int row = 1; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 1; col < charArray.length; col++) {
                String val = String.valueOf(charArray[col]);
                var dir = Direction.fromValue(val);
                if (dir.isPresent()) {
                    var coord = new Coordinate(row, col);
                    var bliz = new Blizzard(coord, dir.get());
                    coordToBlizzard.put(coord, List.of(bliz));
                } else if (val.equals(".")) {
                    possibleCoords.add(new Coordinate(row, col));
                }
            }

            if (line.startsWith("##")) {
                maxRow = row;
                break;
            }
        }
        int maxCol = lines.get(0).length() - 1;
        possibleCoords.addAll(coordToBlizzard.keySet());
        var start = new Coordinate(0, 1);
        possibleCoords.add(start); //start;
        var end = new Coordinate(maxRow, maxCol - 1);
        possibleCoords.add(end);

        List<Coordinate> path = findBestPath(possibleCoords, coordToBlizzard, start, end, maxCol - 1, maxRow - 1, 0);

        System.out.println(path.size() - 1);
    }

    private static List<Coordinate> findBestPath(Set<Coordinate> coords, Map<Coordinate, List<Blizzard>> coordToBlizzard, Coordinate start, Coordinate end,
                                                 int areaWidth, int areaHeight, int step) {
        List<Coordinate> path = new ArrayList<>();
        path.add(start);
        Map<Integer, Map<Coordinate, List<Blizzard>>> timeToState = new HashMap<>();

        int cycleNumber = lcm(areaHeight, areaWidth);
        for (int time = 0; time < cycleNumber; time++) {
            Map<Coordinate, List<Blizzard>> statePerTime = new HashMap<>();
            for (Map.Entry<Coordinate, List<Blizzard>> entry : coordToBlizzard.entrySet()) {
                var initCoord = entry.getKey();
                for (Blizzard initBliz : entry.getValue()) {
                    var nextCoord = new Coordinate();
                    if (initBliz.dir == Direction.RIGHT) {
                        processHorizonRight(time, areaWidth, 1, statePerTime, initCoord, initBliz, nextCoord);
                    } else if (initBliz.dir == Direction.LEFT) {
                        processHorizonLeft(time, areaWidth, 1, statePerTime, initCoord, initBliz, nextCoord);
                    } else if (initBliz.dir == Direction.DOWN) {
                        processVerticalDown(time, areaHeight, 1, statePerTime, initCoord, initBliz, nextCoord);
                    } else if (initBliz.dir == Direction.UP) {
                        processVerticalUp(time, areaHeight, 1, statePerTime, initCoord, initBliz, nextCoord);
                    }
                }
            }
            timeToState.put(time, statePerTime);
        }


        List<Coordinate> res = findIt(path, coords, timeToState, end, step, areaWidth, areaHeight, cycleNumber, new HashMap<>());
        return res;
    }

    private static List<Coordinate> findIt(List<Coordinate> path, Set<Coordinate> coords, Map<Integer,
            Map<Coordinate, List<Blizzard>>> timeToState, Coordinate end, int time, int areadWidth,
                                           int areaHeight, int cycleNumber, Map<DiscoveryInfo, List<Coordinate>> timeToTailCache) {
        var curent = path.get(path.size() - 1);

        if (timeToTailCache.containsKey(new DiscoveryInfo(curent, time))) {
            var tail = timeToTailCache.get(new DiscoveryInfo(curent, time));
            if (tail == stub) {
                return stub;
            } else {
                List<Coordinate> finalPath = new ArrayList<>(path);
                finalPath.addAll(tail);
                return finalPath;
            }
        }
        if (path.size() >= cycleNumber * 4) {
            return stub;
        }

       /* if (path.size() > cycleNumber) {
            Coordinate prevCycleCoord = path.get(path.size() - cycleNumber);
            if (prevCycleCoord.equals(curent)) {
                return Collections.emptyList();
            }
        }*/
    /*    if (!discovered.add(new DiscoveryInfo(curent, time))) {
            return Collections.emptyList();
        }*/

        if (!coords.contains(curent)) {
            return stub;
        }
        if (curent.equals(end)) {
            return path;
        }
        Map<Coordinate, List<Blizzard>> dangerousPoints = timeToState.get(time % cycleNumber);
        //printTable(coords, areadWidth, areaHeight, curent, dangerousPoints.keySet(), time);

        if (dangerousPoints.containsKey(curent)) {
            return stub;
        }
    /*    if (isCrossedWithBliz(path, timeToState, time % cycleNumber)) {
            return Collections.emptyList();
        }*/

        int minSize = Integer.MAX_VALUE;
        List<Coordinate> minPath = stub;
        for (int i = 0; i < moves.size(); i++) {
            Coordinate move = moves.get(i);
            var nextCoord = new Coordinate(curent.row + move.row, curent.col + move.col);
            List<Coordinate> nextPath = new ArrayList<>(path);
            nextPath.add(nextCoord);
            List<Coordinate> res = findIt(nextPath, coords, timeToState,
                    end, time + 1, areadWidth, areaHeight, cycleNumber, timeToTailCache);
            if (res.size() > 0 && res.size() < minSize) {
                minSize = res.size();
                minPath = res;
            }
        }
        if (minPath == stub) {
            timeToTailCache.put(new DiscoveryInfo(curent, time), stub);
        } else {
            var tail = minPath.subList(path.size(), minPath.size());
            timeToTailCache.put(new DiscoveryInfo(curent, time), tail);
        }
        return minPath;

    }

    private static boolean isCrossedWithBliz(List<Coordinate> path, Map<Integer, Map<Coordinate, List<Blizzard>>> timeToState, int time) {
        if (path.size() < 2) {
            return false;
        }
        Coordinate prev = path.get(path.size() - 2);
        var currentState = timeToState.get(time);
        if (!currentState.containsKey(prev)) {
            return false;
        }
        var overlappedBliz = currentState.get(prev);
        Direction dir = resolveDirection(path.get(path.size() - 1), path.get(path.size() - 2));
        for (Blizzard blizzard : overlappedBliz) {
            if (blizzard.dir == dir.reverse()) {
                return true;
            }
        }
        return false;
    }

    private static Direction resolveDirection(Coordinate current, Coordinate previous) {
        if (current.col > previous.col) {
            return Direction.RIGHT;
        }
        if (current.col < previous.col) {
            return Direction.LEFT;
        }
        if (current.row > previous.row) {
            return Direction.DOWN;
        }
        if (current.row < previous.row) {
            return Direction.UP;
        }
        return Direction.STOP;
    }

    private static void printTable(Set<Coordinate> coords, int areadWidth, int areaHeight, Coordinate curent, Set<Coordinate> dangerousPoints, int time) {
        System.out.printf("Time = %s", time);
        System.out.println();
        for (int row = 0; row < areaHeight + 2; row++) {
            for (int col = 0; col < areadWidth + 2; col++) {
                if (curent.equals(new Coordinate(row, col))) {
                    System.out.print("E");
                }
                if (dangerousPoints.contains((new Coordinate(row, col)))) {
                    System.out.print("*");
                } else if (coords.contains(new Coordinate(row, col))) {
                    System.out.print(".");
                } else {
                    System.out.print("#");
                }
            }
            System.out.println();
        }
    }

    private static void processHorizonRight(int time, int areaSize, int shift, Map<Coordinate, List<Blizzard>> dangerousPoints,
                                            Coordinate initCoord, Blizzard initBliz, Coordinate nextCoord) {
        nextCoord.row = initCoord.row;
        int relativeCol = initCoord.col - shift + time;
        int colShift = relativeCol % areaSize;
        nextCoord.col = colShift + shift;
        var bliz = new Blizzard(nextCoord, initBliz.dir);
        List<Blizzard> blizz = new ArrayList<>();
        blizz.add(bliz);
        dangerousPoints.merge(nextCoord, blizz, (b1, b2) -> {
            b1.addAll(b2);
            return b1;
        });
    }

    private static void processHorizonLeft(int time, int areaSize, int shift, Map<Coordinate, List<Blizzard>> dangerousPoints,
                                           Coordinate initCoord, Blizzard initBliz, Coordinate nextCoord) {
        nextCoord.row = initCoord.row;
        int reversed = areaSize - 1 - (initCoord.col - shift);
        int relativeCol = reversed + time;
        int colShift = relativeCol % areaSize;
        nextCoord.col = areaSize - 1 - colShift + shift;
        var bliz = new Blizzard(nextCoord, initBliz.dir);
        List<Blizzard> blizz = new ArrayList<>();
        blizz.add(bliz);
        dangerousPoints.merge(nextCoord, blizz, (b1, b2) -> {
            b1.addAll(b2);
            return b1;
        });
    }

    private static void processVerticalDown(int time, int areaSize, int shift, Map<Coordinate, List<Blizzard>> dangerousPoints, Coordinate initCoord, Blizzard initBliz, Coordinate nextCoord) {
        nextCoord.col = initCoord.col;
        int relativeRow = initCoord.row - shift + time;
        int rowShift = relativeRow % areaSize;
        nextCoord.row = rowShift + shift;
        var bliz = new Blizzard(nextCoord, initBliz.dir);
        List<Blizzard> blizz = new ArrayList<>();
        blizz.add(bliz);
        dangerousPoints.merge(nextCoord, blizz, (b1, b2) -> {
            b1.addAll(b2);
            return b1;
        });
    }

    private static void processVerticalUp(int time, int areaSize, int shift, Map<Coordinate, List<Blizzard>> dangerousPoints,
                                          Coordinate initCoord, Blizzard initBliz, Coordinate nextCoord) {
        nextCoord.col = initCoord.col;
        int reversed = areaSize - 1 - (initCoord.row - shift);
        int relativeCol = reversed + time;
        int colShift = relativeCol % areaSize;
        nextCoord.row = areaSize - 1 - colShift + shift;
        var bliz = new Blizzard(nextCoord, initBliz.dir);
        List<Blizzard> blizz = new ArrayList<>();
        blizz.add(bliz);
        dangerousPoints.merge(nextCoord, blizz, (b1, b2) -> {
            b1.addAll(b2);
            return b1;
        });
    }


    public static Integer lcm(Integer number1, Integer number2) {
        // convert string 'a' and 'b' into BigInteger

        // calculate multiplication of two bigintegers
        BigInteger num1 = BigInteger.valueOf(number1);
        BigInteger num2 = BigInteger.valueOf(number2);
        BigInteger mul = num1.multiply(num2);
        // calculate gcd of two bigintegers
        BigInteger gcd = num1.gcd(num2);

        // calculate lcm using formula: lcm * gcd = x * y
        BigInteger lcm = mul.divide(gcd);
        return lcm.intValue();
    }


    @AllArgsConstructor
    @ToString
    private static class Blizzard {
        private Coordinate initialPos;
        private Direction dir;

    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    @NoArgsConstructor
    private static class Coordinate {
        private int row;
        private int col;
    }

    @ToString
    private enum Direction {
        UP("^"), DOWN("v"), LEFT("<"), RIGHT(">"), STOP("*");

        private String value;

        Direction(String value) {
            this.value = value;
        }

        public static Optional<Direction> fromValue(String value) {
            for (Direction direction : values()) {
                if (value.equals(direction.value)) {
                    return Optional.of(direction);
                }
            }
            return Optional.empty();
        }

        public Direction reverse() {
            if (this == Direction.RIGHT) {
                return Direction.LEFT;
            }
            if (this == Direction.LEFT) {
                return Direction.RIGHT;
            }
            if (this == Direction.DOWN) {
                return Direction.UP;
            }
            if (this == Direction.UP) {
                return Direction.DOWN;
            }
            if (this == Direction.STOP) {
                return Direction.STOP;
            }

            throw new RuntimeException("not found");
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class DiscoveryInfo {
        private Coordinate coord;
        private int time;
    }

    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day24.txt");
        int maxRow = -1;
        Map<Coordinate, List<Blizzard>> coordToBlizzard = new HashMap<>();
        Set<Coordinate> possibleCoords = new HashSet<>();
        for (int row = 1; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 1; col < charArray.length; col++) {
                String val = String.valueOf(charArray[col]);
                var dir = Direction.fromValue(val);
                if (dir.isPresent()) {
                    var coord = new Coordinate(row, col);
                    var bliz = new Blizzard(coord, dir.get());
                    coordToBlizzard.put(coord, List.of(bliz));
                } else if (val.equals(".")) {
                    possibleCoords.add(new Coordinate(row, col));
                }
            }

            if (line.startsWith("##")) {
                maxRow = row;
                break;
            }
        }
        int maxCol = lines.get(0).length() - 1;
        possibleCoords.addAll(coordToBlizzard.keySet());
        var start = new Coordinate(0, 1);
        possibleCoords.add(start); //start;
        var end = new Coordinate(maxRow, maxCol - 1);
        possibleCoords.add(end);

        List<Coordinate> path1 = findBestPath(possibleCoords, coordToBlizzard, start, end, maxCol - 1, maxRow - 1, 0);

        List<Coordinate> path2 = findBestPath(possibleCoords, coordToBlizzard, end, start, maxCol - 1, maxRow - 1, path1.size() - 1);
        List<Coordinate> path3 = findBestPath(possibleCoords, coordToBlizzard, start, end, maxCol - 1, maxRow - 1, path1.size() + path2.size() - 2);

        System.out.println(path1.size()  + path2.size() + path3.size() - 3);
    }

}
package aoc;


import aoc.Day18Part1.Coordinate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day18Part2 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task2() throws IOException {
        List<String> lines =FilesUtilS.readFile("day18.txt");
        Set<Coordinate> coords = new HashSet<>();
        for (String line : lines) {
            String[] values = line.split(",");
            coords.add(new Coordinate(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()),
                    Integer.parseInt(values[2].trim())));
        }
        int maxX = -1;
        int maxY = -1;
        int maxZ = -1;

        for (Coordinate coord : coords) {
            if (coord.z > maxZ) {
                maxZ = coord.z;
            }
            if (coord.y > maxY) {
                maxY = coord.y;
            }
            if (coord.x > maxX) {
                maxX = coord.x;
            }
        }


        Set<Coordinate> reachable = findReachable(coords, new Coordinate(maxX, maxY, maxZ));

        Set<Coordinate> locked = new HashSet<>();
        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                for (int z = 0; z <= maxZ; z++) {
                    var coord = new Coordinate(x, y, z);
                    if (!reachable.contains(coord) && !coords.contains(coord)) {
                        locked.add(coord);
                    }
                }
            }
        }

        System.out.println(locked);

        int res = findSurface(coords, locked);
        System.out.println(res);
    }

    private static Set<Coordinate> findReachable(Set<Coordinate> coords, Coordinate maxCoord) {
        Coordinate start = new Coordinate(0, 0, 0);
        Set<Coordinate> reachable = new HashSet<>();
        findIt(coords, start, reachable, maxCoord);
        return reachable;
    }

    private static void findIt(Set<Coordinate> coords, Coordinate cursor, Set<Coordinate> visited, Coordinate maxCoord) {
        if (cursor.x < 0 || cursor.y < 0 || cursor.z < 0
                || cursor.x > maxCoord.x || cursor.y > maxCoord.y || cursor.z > maxCoord.z) {
            return;
        }
        if (coords.contains(cursor)) {
            return;
        }
        if (visited.contains(cursor)) {
            return;
        }
        visited.add(cursor);

        var nextCoord = new Coordinate(cursor.x, cursor.y, cursor.z + 1);
        findIt(coords, nextCoord, visited, maxCoord);

        nextCoord = new Coordinate(cursor.x, cursor.y, cursor.z - 1);
        findIt(coords, nextCoord, visited, maxCoord);

        nextCoord = new Coordinate(cursor.x, cursor.y + 1, cursor.z);
        findIt(coords, nextCoord, visited, maxCoord);

        nextCoord = new Coordinate(cursor.x, cursor.y - 1, cursor.z);
        findIt(coords, nextCoord, visited, maxCoord);

        nextCoord = new Coordinate(cursor.x + 1, cursor.y, cursor.z);
        findIt(coords, nextCoord, visited, maxCoord);

        nextCoord = new Coordinate(cursor.x - 1, cursor.y, cursor.z);
        findIt(coords, nextCoord, visited, maxCoord);
    }

    public static int findSurface(Collection<Coordinate> coords, Set<Coordinate> locked) {
        Map<Day18Part1.Line, Set<Integer>> lineValues = new HashMap<>();
        int maxX = -1;
        int maxY = -1;
        int maxZ = -1;
        for (Coordinate coord : coords) {

            Set<Integer> valuesZ = new HashSet<>();
            valuesZ.add(coord.z);
            lineValues.merge(new Day18Part1.Line(coord.x, coord.y, Integer.MIN_VALUE), valuesZ, (vals1, vals2) -> {
                vals1.addAll(vals2);
                return vals1;
            });

            Set<Integer> valuesY = new HashSet<>();
            valuesY.add(coord.y);
            lineValues.merge(new Day18Part1.Line(coord.x, Integer.MIN_VALUE, coord.z), valuesY, (vals1, vals2) -> {
                vals1.addAll(vals2);
                return vals1;
            });

            Set<Integer> valuesX = new HashSet<>();
            valuesX.add(coord.x);
            lineValues.merge(new Day18Part1.Line(Integer.MIN_VALUE, coord.y, coord.z), valuesX, (vals1, vals2) -> {
                vals1.addAll(vals2);
                return vals1;
            });

            if (coord.z > maxZ) {
                maxZ = coord.z;
            }
            if (coord.y > maxY) {
                maxY = coord.y;
            }
            if (coord.x > maxX) {
                maxX = coord.x;
            }
        }

        var lx = processX(lineValues, maxX, maxY, maxZ, locked);
        var ly = processY(lineValues, maxX, maxY, maxZ, locked);
        var lz = processZ(lineValues, maxX, maxY, maxZ, locked);

        return lx.size() + ly.size() + lz.size();
    }

    private static List<Coordinate> processX(Map<Day18Part1.Line, Set<Integer>> lineValues, int maxX, int maxY, int maxZ, Set<Coordinate> locked) {
        List<Coordinate> xSurface = new ArrayList<>();
        for (int z = 0; z <= maxZ; z++) {
            for (int y = 0; y <= maxY; y++) {
                var line = lineValues.get(new Day18Part1.Line(Integer.MIN_VALUE, y, z));
                if (line != null) {
                    Coordinate previous = Coordinate.stub;
                    for (int x = 0; x <= maxX; x++) {
                        if (line.contains(x)) {
                            var coord = new Coordinate(x, y, z);
                            if (x != previous.x + 1) {
                                if (previous != Coordinate.stub && !doesTouchLocked(locked, previous, new Coordinate(1, 0, 0))) {
                                    xSurface.add(previous);
                                }
                                if (!doesTouchLocked(locked, coord, new Coordinate(-1, 0, 0))) {
                                    xSurface.add(coord);
                                }
                            }
                            previous = coord;
                        }
                    }
                    if (!doesTouchLocked(locked, previous, new Coordinate(1, 0, 0))) {
                        xSurface.add(previous);
                    }
                }
            }
        }
        return xSurface;
    }

    private static boolean doesTouchLocked(Set<Coordinate> locked, Coordinate coord, Coordinate shift) {
        boolean res = locked.contains(new Coordinate(coord.x + shift.x, coord.y + shift.y, coord.z + shift.z));
        return res;
    }

    private static List<Coordinate> processZ(Map<Day18Part1.Line, Set<Integer>> lineValues, int maxX, int maxY,
                                             int maxZ, Set<Coordinate> locked) {
        List<Coordinate> zSurface = new ArrayList<>();
        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                var line = lineValues.get(new Day18Part1.Line(x, y, Integer.MIN_VALUE));
                if (line != null) {
                    Coordinate previous = Coordinate.stub;
                    for (int z = 0; z <= maxZ; z++) {
                        if (line.contains(z)) {
                            var coord = new Coordinate(x, y, z);
                            if (z != previous.z + 1) {
                                if ((previous != Coordinate.stub) && !doesTouchLocked(locked, previous, new Coordinate(0, 0, 1))) {
                                    zSurface.add(previous);
                                }
                                if (!doesTouchLocked(locked, coord, new Coordinate(0, 0, -1))) {
                                    zSurface.add(coord);
                                }
                            }
                            previous = coord;
                        }
                    }
                    if (!doesTouchLocked(locked, previous, new Coordinate(0, 0, 1))) {
                        zSurface.add(previous);
                    }
                }
            }
        }
        return zSurface;
    }

    private static List<Coordinate> processY(Map<Day18Part1.Line, Set<Integer>> lineValues, int maxX, int maxY,
                                             int maxZ, Set<Coordinate> locked) {
        List<Coordinate> ySurface = new ArrayList<>();
        for (int x = 0; x <= maxX; x++) {
            for (int z = 0; z <= maxZ; z++) {
                var line = lineValues.get(new Day18Part1.Line(x, Integer.MIN_VALUE, z));
                if (line != null) {
                    Coordinate previous = Coordinate.stub;
                    for (int y = 0; y <= maxY; y++) {
                        var coord = new Coordinate(x, y, z);
                        if (line.contains(y)) {
                            if (y != previous.y + 1) {
                                if (previous != Coordinate.stub && (!doesTouchLocked(locked, previous, new Coordinate(0, 1, 0)))) {
                                    ySurface.add(previous);
                                }
                                if (!doesTouchLocked(locked, coord, new Coordinate(0, -1, 0))) {
                                    ySurface.add(coord);
                                }
                            }
                            previous = coord;
                        }
                    }
                    if (!doesTouchLocked(locked, previous, new Coordinate(0, 1, 0))) {
                        ySurface.add(previous);
                    }
                }
            }
        }
        return ySurface;
    }

}

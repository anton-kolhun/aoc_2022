package aoc;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18Part1 {

    public static void main(String[] args) throws IOException {
        task1();
    }

    private static void task1() throws IOException {
        List<String> lines =FilesUtilS.readFile("day18.txt");
        List<Coordinate> coords = new ArrayList<>();
        for (String line : lines) {
            String[] values = line.split(",");
            coords.add(new Coordinate(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()),
                    Integer.parseInt(values[2].trim())));
        }

        findSurface(coords);

    }

    public static int findSurface(Collection<Coordinate> coords) {
        Map<Line, Set<Integer>> lineValues = new HashMap<>();
        int maxX = -1;
        int maxY = -1;
        int maxZ = -1;
        for (Coordinate coord : coords) {

            Set<Integer> valuesZ = new HashSet<>();
            valuesZ.add(coord.z);
            lineValues.merge(new Line(coord.x, coord.y, Integer.MIN_VALUE), valuesZ, (vals1, vals2) -> {
                vals1.addAll(vals2);
                return vals1;
            });

            Set<Integer> valuesY = new HashSet<>();
            valuesY.add(coord.y);
            lineValues.merge(new Line(coord.x, Integer.MIN_VALUE, coord.z), valuesY, (vals1, vals2) -> {
                vals1.addAll(vals2);
                return vals1;
            });

            Set<Integer> valuesX = new HashSet<>();
            valuesX.add(coord.x);
            lineValues.merge(new Line(Integer.MIN_VALUE, coord.y, coord.z), valuesX, (vals1, vals2) -> {
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

        var lx = processX(lineValues, maxX, maxY, maxZ);
        var ly = processY(lineValues, maxX, maxY, maxZ);
        var lz = processZ(lineValues, maxX, maxY, maxZ);
//        System.out.println(l1);
//        System.out.println(l2);
//        System.out.println(lx);
        System.out.println(lx.size() + ly.size() + lz.size());

        //printX(lineValues, maxX, maxY, maxZ);
//        printY(lineValues, maxX, maxY, maxZ);
        return lx.size() + ly.size() + lz.size();
    }

    private static void printY(Map<Line, Set<Integer>> lineValues, int maxX, int maxY, int maxZ) {
        for (int z = 0; z <= maxZ; z++) {
            for (int x = 0; x <= maxX; x++) {
                var line = lineValues.get(new Line(x, Integer.MIN_VALUE, z));
                if (line != null) {
                    var lineSorted = line.stream()
                            .sorted(Comparator.comparingInt(o -> o))
                            .collect(Collectors.toList());
                    System.out.println();
                    System.out.println();
                    System.out.println(lineSorted);
                    Coordinate previous = Coordinate.stub;
                    for (int y = 1; y <= maxY; y++) {
                        if (line.contains(y)) {
                            var coord = new Coordinate(x, y, z);
                            if (y != previous.y + 1) {
                                if (previous != Coordinate.stub) {
                                    System.out.print(previous.y + " ");
                                }
                                System.out.print(y + " ");
                            }
                            previous = coord;
                        }
                    }
                    System.out.print(previous.y + " ");
                }
            }
        }
    }

    private static void printX(Map<Line, Set<Integer>> lineValues,
                               int maxX, int maxY, int maxZ) {
        for (int z = 1; z <= maxZ; z++) {
            for (int y = 1; y <= maxY; y++) {
                var line = lineValues.get(new Line(Integer.MIN_VALUE, y, z));
                if (line != null) {
                    var lineSorted = line.stream()
                            .sorted(Comparator.comparingInt(o -> o))
                            .collect(Collectors.toList());
                    System.out.println();
                    System.out.println();
                    System.out.println(lineSorted);
                    Coordinate previous = Coordinate.stub;
                    for (int x = 1; x <= maxX; x++) {
                        if (line.contains(x)) {
                            var coord = new Coordinate(x, y, z);
                            if (x != previous.x + 1) {
                                if (previous != Coordinate.stub) {
                                    System.out.print(previous.x + " ");
                                }
                                System.out.print(x + " ");
                            }
                            previous = coord;
                        }
                    }
                    System.out.print(previous.x + " ");
                }
            }
        }
    }

    private static List<Coordinate> processX(Map<Line, Set<Integer>> lineValues, int maxX, int maxY, int maxZ) {
        List<Coordinate> xSurface = new ArrayList<>();
        for (int z = 0; z <= maxZ; z++) {
            for (int y = 0; y <= maxY; y++) {
                var line = lineValues.get(new Line(Integer.MIN_VALUE, y, z));
                if (line != null) {
                    Coordinate previous = Coordinate.stub;
                    for (int x = 0; x <= maxX; x++) {
                        if (line.contains(x)) {
                            var coord = new Coordinate(x, y, z);
                            if (x != previous.x + 1) {
                                if (previous != Coordinate.stub) {
                                    xSurface.add(previous);
                                }
                                xSurface.add(coord);
                            }
                            previous = coord;
                        }
                    }
                    xSurface.add(previous);
                }
            }
        }
        return xSurface;
    }

    private static List<Coordinate> processZ(Map<Line, Set<Integer>> lineValues, int maxX, int maxY, int maxZ) {
        List<Coordinate> zSurface = new ArrayList<>();
        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                var line = lineValues.get(new Line(x, y, Integer.MIN_VALUE));
                if (line != null) {
                    Coordinate previous = Coordinate.stub;
                    for (int z = 0; z <= maxZ; z++) {
                        if (line.contains(z)) {
                            var coord = new Coordinate(x, y, z);
                            if (z != previous.z + 1) {
                                if (previous != Coordinate.stub) {
                                    zSurface.add(previous);
                                }
                                zSurface.add(coord);
                            }
                            previous = coord;
                        }
                    }
                    zSurface.add(previous);
                }
            }
        }
        return zSurface;
    }

    private static List<Coordinate> processY(Map<Line, Set<Integer>> lineValues, int maxX, int maxY, int maxZ) {
        List<Coordinate> ySurface = new ArrayList<>();
        for (int x = 0; x <= maxX; x++) {
            for (int z = 0; z <= maxZ; z++) {
                var line = lineValues.get(new Line(x, Integer.MIN_VALUE, z));
                if (line != null) {
                    Coordinate previous = Coordinate.stub;
                    for (int y = 0; y <= maxY; y++) {
                        var coord = new Coordinate(x, y, z);
                        if (line.contains(y)) {
                            if (y != previous.y + 1) {
                                if (previous != Coordinate.stub) {
                                    ySurface.add(previous);
                                }
                                ySurface.add(coord);
                            }
                            previous = coord;
                        }
                    }
                    ySurface.add(previous);
                }
            }
        }
        return ySurface;
    }

    public static class Coordinate {
        int x;
        int y;
        int z;

        public Coordinate(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static Coordinate stub = new Coordinate(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Coordinate that = (Coordinate) o;

            if (x != that.x) return false;
            if (y != that.y) return false;
            return z == that.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }

        @Override
        public String toString() {
            return String.format("{x=%s; y=%s; z=%s}", x, y, z);

        }
    }

    @ToString
    public static class Line {
        private int xAxis;
        private int yAxis;
        private int zAxis;

        public Line(int xAxis, int yAxis, int zAxis) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            this.zAxis = zAxis;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Line line = (Line) o;

            if (xAxis != line.xAxis) return false;
            if (yAxis != line.yAxis) return false;
            return zAxis == line.zAxis;
        }

        @Override
        public int hashCode() {
            int result = xAxis;
            result = 31 * result + yAxis;
            result = 31 * result + zAxis;
            return result;
        }
    }


}

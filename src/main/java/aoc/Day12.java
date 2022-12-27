package aoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class Day12 {

    public static void main(String[] args) throws IOException {
//        task1();
        task2();

    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day12.txt");
        Map<Coordinate, Integer> coordToVales = new HashMap<>();
        Coordinate start = null;
        Coordinate end = null;
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                if (c == 'S') {
                    start = new Coordinate(row, col);
                    c = 'a';
                    int intRepr = Character.getNumericValue(c) - 9;
                    coordToVales.put(new Coordinate(row, col), intRepr);
                } else if (c == 'E') {
                    c = 'z';
                    end = new Coordinate(row, col);
                    int intRepr = Character.getNumericValue(c) - 9;
                    coordToVales.put(new Coordinate(row, col), intRepr);
                } else {
                    int intRepr = Character.getNumericValue(c) - 9;
                    coordToVales.put(new Coordinate(row, col), intRepr);
                }
            }
        }


        List<Edge> edges = buildEdges(coordToVales, lines.size(), lines.get(0).length());

        int path = findPath(coordToVales, edges, start, end);
        System.out.println(path);
    }

    private static List<Edge> buildEdges(Map<Coordinate, Integer> coordToVales, int rows, int cols) {
        List<Edge> edges = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                var coord1 = new Coordinate(row, col);
                var coord2 = new Coordinate(row, col + 1);
                Integer coord1Val = coordToVales.get(coord1);
                Integer coord2Val = coordToVales.getOrDefault(coord2, Integer.MAX_VALUE);

                if (coord2Val - coord1Val <= 1 || coord1Val == 0 || coord2Val == -1) {
                    edges.add(new Edge(coord1, coord2));
                }
                if (coord1Val - coord2Val <= 1 || coord2Val == 0 || coord1Val == -1) {
                    edges.add(new Edge(coord2, coord1));
                }
                var coord3 = new Coordinate(row + 1, col);
                Integer coord3Val = coordToVales.getOrDefault(coord3, Integer.MAX_VALUE);
                if (coord3Val - coord1Val <= 1 || coord1Val == 0 || coord3Val == -1) {
                    edges.add(new Edge(coord1, coord3));
                }
                if (coord1Val - coord3Val <= 1 || coord3Val == 0 || coord1Val == -1) {
                    edges.add(new Edge(coord3, coord1));
                }

            }
        }
        return edges;
    }

    private static int findPath(Map<Coordinate, Integer> coordToVales, List<Edge> edges, Coordinate start, Coordinate end) {
        Map<Coordinate, List<Edge>> fromEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.from));
        Map<Coordinate, List<Edge>> toEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.from));


        Set<Coordinate> visited = new HashSet<>();
        Map<Coordinate, Integer> coordToDistance = new HashMap<>();
        List<Coordinate> initial = new ArrayList<>();
        initial.add(start);
        coordToDistance.put(start, 0);
        PriorityQueue<Distance> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.distance));
        priorityQueue.add(new Distance(start, 0));
        boolean notFound = true;
        while (notFound && !priorityQueue.isEmpty()) {
            Coordinate coordMin = findCoord(priorityQueue, visited);
            int minPathSize = coordToDistance.getOrDefault(coordMin, Integer.MAX_VALUE);
            visited.add(coordMin);
            List<Edge> fromEdgs = fromEdges.getOrDefault(coordMin, new ArrayList<>());
            for (Edge fromEdge : fromEdgs) {
                var minSize = coordToDistance.getOrDefault(fromEdge.to, Integer.MAX_VALUE);
                if (minSize > minPathSize + 1) {
                    coordToDistance.put(fromEdge.to, minPathSize + 1);
                    priorityQueue.add(new Distance(fromEdge.to, minPathSize + 1));
                }
                if (fromEdge.to.equals(end)) {
                    notFound = false;
                }
            }
        }
        Integer min = coordToDistance.getOrDefault(end, Integer.MAX_VALUE);
        return min;
    }


    private static void task2() throws IOException {
        List<Coordinate> starts = new ArrayList<>();
        Coordinate end = null;
        List<String> lines = FilesUtilS.readFile("day12.txt");
        Map<Coordinate, Integer> coordToVales = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                if (c == 'S') {
                    c = 'a';
                    int intRepr = Character.getNumericValue(c) - 9;
                    coordToVales.put(new Coordinate(row, col), intRepr);
                    starts.add(new Coordinate(row, col));
                } else if (c == 'E') {
                    c = 'z';
                    end = new Coordinate(row, col);
                    int intRepr = Character.getNumericValue(c) - 9;
                    coordToVales.put(new Coordinate(row, col), intRepr);
                } else {
                    if (c == 'a') {
                        starts.add(new Coordinate(row, col));
                    }
                    int intRepr = Character.getNumericValue(c) - 9;
                    coordToVales.put(new Coordinate(row, col), intRepr);
                }
            }
        }
        //print(lines, coordToVales);

        List<Integer> results = new ArrayList<>();
        List<Edge> edges = buildEdges(coordToVales, lines.size(), lines.get(0).length());
        for (Coordinate coordinate : starts) {
            var path = findPath(coordToVales, edges, coordinate, end);
            results.add(path);
        }
        results.sort(Comparator.comparingInt(o -> o));
        System.out.println(results.get(0));

    }

    private static void print(List<String> lines, Map<Coordinate, Integer> coordToVales) {
        for (int row = 0; row < lines.size(); row++) {
            for (int col = 0; col < lines.get(0).length(); col++) {
                int value = coordToVales.get(new Coordinate(row, col));
                String str;
                if (value == -1) {
                    str = "E";
                } else if (value == 0) {
                    str = "S";
                } else {
                    str = String.valueOf(value);
                }
                String suffix;
                if (str.length() == 2) {
                    suffix = " ";
                } else {
                    suffix = "  ";
                }
                System.out.print(str + suffix);
            }
            System.out.println();
        }
    }

    private static Coordinate findCoord(PriorityQueue<Distance> distances, Set<Coordinate> visited) {
        if (distances.isEmpty()) {
            return null;
        }
        Coordinate coord = distances.poll().coord;
        while (visited.contains(coord)) {
            coord = distances.poll().coord;
        }
        return coord;
    }

    private static class Coordinate {
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

    private static class Edge {
        Coordinate from;
        Coordinate to;

        public Edge(Coordinate from, Coordinate to) {
            this.from = from;
            this.to = to;
        }
    }

    private static class Distance {
        private Coordinate coord;
        private int distance;

        public Distance(Coordinate coord, int distance) {
            this.coord = coord;
            this.distance = distance;
        }
    }
}

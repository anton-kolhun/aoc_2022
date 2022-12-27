package aoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class Day16Part1 {

    public static void main(String[] args) throws IOException {
        System.out.println("starting execution new...");
        var start = System.currentTimeMillis();
        task1();
        var end = System.currentTimeMillis();
        var duration = (end - start) / 1000;
        System.out.println("Executed in " + duration);

    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day16.txt");
        List<Edge> edges = new ArrayList<>();
        Map<String, Integer> edgeToRate = new HashMap<>();
        for (String line : lines) {
            String[] vals = line.split(" ");
            var from = vals[1];
            String rate = vals[4].split("=")[1];
            rate = rate.substring(0, rate.length() - 1);
            edgeToRate.put(from, Integer.parseInt(rate));
            int cursor;
            for (cursor = 9; cursor < vals.length - 1; cursor++) {
                var value = vals[cursor].substring(0, vals[cursor].length() - 1);
                Edge edge = new Edge(from, value);
                edges.add(edge);
            }
            if (cursor < vals.length) {
                Edge edge = new Edge(from, vals[cursor]);
                edges.add(edge);
            }
        }

        Set<String> nonNullPoints = edgeToRate.entrySet().stream()
                .filter(entry -> entry.getValue() != 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<Path, List<String>> fromTo = new HashMap<>();
        for (Map.Entry<String, Integer> from : edgeToRate.entrySet()) {
            String pointFrom = from.getKey();
            for (Map.Entry<String, Integer> to : edgeToRate.entrySet()) {
                String pointTo = to.getKey();
                if (!pointTo.equals(pointFrom)) {
                    List<String> path = findPath(edges, pointFrom, pointTo);
                    fromTo.put(new Path(pointFrom, pointTo), path);
                }
            }
        }

        Map<String, List<Edge>> fromEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.from));

        processGraph(edges, edgeToRate, fromTo, nonNullPoints, fromEdges);

 /*       var l1 = Arrays.asList(
                "AA", "JH", "CA", "CA", "QO", "GN", "FP", "AE", "YH", "YH", "WM",
                "UX", "UX", "ZI", "AR", "AR", "IS", "LH", "FP", "UE", "LG", "LE", "LE");

        var l2 = Arrays.asList(
                "AA", "XU", "TU", "TU", "LV", "UK", "UK", "KJ", "YF",
                "EK", "EK", "LK", "IL", "GW", "GW", "BL", "JT", "JT");*/

   /*     printPath(l2, edgeToRate);
        int res = calculateScore(l1, edgeToRate) + calculateScore(l2, edgeToRate);
        System.out.println(res);*/

    }


    private static List<String> findPath(List<Edge> edges, String start, String end) {
        Map<String, List<Edge>> fromEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.from));

        Set<String> visited = new HashSet<>();
        Map<String, List<String>> coordToDistance = new HashMap<>();
        List<String> initial = new ArrayList<>();
        initial.add(start);
        coordToDistance.put(start, initial);
        PriorityQueue<Distance> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.distance));
        priorityQueue.add(new Distance(start, 0));
        boolean notFound = true;
        while (notFound && !priorityQueue.isEmpty()) {
            String coordMin = findCoord(priorityQueue, visited);
            List<String> minPath = coordToDistance.getOrDefault(coordMin, new ArrayList<>());
            visited.add(coordMin);
            List<Edge> fromEdgs = fromEdges.getOrDefault(coordMin, new ArrayList<>());
            for (Edge fromEdge : fromEdgs) {
                var pathTo = coordToDistance.get(fromEdge.to);
                var pathTosize = Integer.MAX_VALUE;
                if (pathTo != null) {
                    pathTosize = pathTo.size();
                }
                if (pathTosize > minPath.size() + 1) {
                    var newPath = new ArrayList<>(minPath);
                    newPath.add(fromEdge.to);
                    coordToDistance.put(fromEdge.to, newPath);
                    priorityQueue.add(new Distance(fromEdge.to, newPath.size()));
                }
                if (fromEdge.to.equals(end)) {
                    notFound = false;
                }

            }
        }
        return coordToDistance.get(end);
    }

    private static String findCoord(PriorityQueue<Distance> distances, Set<String> visited) {
        if (distances.isEmpty()) {
            return null;
        }
        String coord = distances.poll().point;
        while (visited.contains(coord)) {
            coord = distances.poll().point;
        }
        return coord;
    }

    private static void processGraph(List<Edge> edges, Map<String, Integer> edgeToRate, Map<Path, List<String>> fromTo,
                                     Set<String> nonNullPoints, Map<String, List<Edge>> fromEdges) {
        List<String> path1 = new ArrayList<>();
        path1.add("AA");
        List<String> path2 = new ArrayList<>();
        path2.add("AA");
        TeamPath result = processIt1(path1, path2, edgeToRate, fromTo, nonNullPoints, fromEdges);
        int score = calculateScore(result.path1, edgeToRate) + calculateScore(result.path2, edgeToRate);
        System.out.println(score);
        System.out.println(result.path1);
        System.out.println(result.path2);
    }

    private static TeamPath processIt1(List<String> path1, List<String> path2, Map<String, Integer> edgeToRate, Map<Path,
            List<String>> fromTo, Set<String> remainingToVisit, Map<String, List<Edge>> fromEdges) {
        if (path1.size() > 27 || path2.size() > 27) {
            return new TeamPath();
        }

        String currentPoint = path1.get(path1.size() - 1);

        List<String> toReachable = new ArrayList<>();
        for (Map.Entry<Path, List<String>> path : fromTo.entrySet()) {
            if (path.getKey().from.equals(currentPoint)) {
                toReachable.add(path.getKey().to);
            }
        }

        List<TeamPath> allResults = new ArrayList<>();
        for (String toPoint : toReachable) {
            if (remainingToVisit.contains(toPoint)) {
                List<String> way = fromTo.get(new Path(currentPoint, toPoint));
                List<String> nextPath = new ArrayList<>(path1);
                nextPath.addAll(way.subList(1, way.size()));
                nextPath.add(toPoint);
                var nextRemainingToVisit = new HashSet<>(remainingToVisit);
                nextRemainingToVisit.remove(toPoint);
                TeamPath res = processIt2(nextPath, path2, edgeToRate, fromTo, nextRemainingToVisit, fromEdges);
                allResults.add(res);
            }
        }
        allResults = allResults.stream()
                .filter(teamPath -> teamPath.path1 != null && teamPath.path2 != null)
                .collect(Collectors.toList());
        if (allResults.isEmpty()) {
            return new TeamPath(path1, path2);
        }
        TeamPath bestRes = new TeamPath();
        int bestScore = Integer.MIN_VALUE;
        for (TeamPath result : allResults) {
            int score = calculateScore(result.path1, edgeToRate) + calculateScore(result.path2, edgeToRate);
            if (score > bestScore) {
                bestScore = score;
                bestRes = result;
            }
        }
        return bestRes;
    }

    private static TeamPath processIt2(List<String> path1, List<String> path2, Map<String, Integer> edgeToRate, Map<Path,
            List<String>> fromTo, Set<String> remainingToVisit, Map<String, List<Edge>> fromEdges) {
        if (path1.size() > 27 || path2.size() > 27) {
            return new TeamPath();
        }
        String currentPoint = path2.get(path2.size() - 1);

        List<String> toReachable = new ArrayList<>();
        for (Map.Entry<Path, List<String>> path : fromTo.entrySet()) {
            if (path.getKey().from.equals(currentPoint)) {
                toReachable.add(path.getKey().to);
            }
        }

        List<TeamPath> allResults = new ArrayList<>();
        for (String toPoint : toReachable) {
            if (remainingToVisit.contains(toPoint)) {
                List<String> way = fromTo.get(new Path(currentPoint, toPoint));
                List<String> nextPath = new ArrayList<>(path2);
                nextPath.addAll(way.subList(1, way.size()));
                nextPath.add(toPoint);
                var nextRemainingToVisit = new HashSet<>(remainingToVisit);
                nextRemainingToVisit.remove(toPoint);
                TeamPath res = processIt1(path1, nextPath, edgeToRate, fromTo, nextRemainingToVisit, fromEdges);
                allResults.add(res);
            }
        }

        allResults = allResults.stream()
                .filter(teamPath -> teamPath.path1 != null && teamPath.path2 != null)
                .collect(Collectors.toList());
        if (allResults.isEmpty()) {
            return new TeamPath(path1, path2);
        }


        TeamPath bestRes = new TeamPath();
        int bestScore = Integer.MIN_VALUE;
        for (TeamPath result : allResults) {
            int score = calculateScore(result.path1, edgeToRate) + calculateScore(result.path2, edgeToRate);
            if (score > bestScore) {
                bestScore = score;
                bestRes = result;
            }
        }
        return bestRes;
    }


    private static void printPath(List<String> bestPath, Map<String, Integer> edgeToRate) {
        int totalScore = 0;
        int currentScore = 0;
        String current = null;
        String previous = bestPath.get(0);
        List<String> opened = new ArrayList<>();
        int cursor;
        for (cursor = 1; cursor < bestPath.size() - 1; cursor++) {
            System.out.printf("== Minute %s ==%n", cursor);
            current = bestPath.get(cursor);
            if (opened.isEmpty()) {
                System.out.println("No valves are open.");
            } else {
                var message = String.format("Valve %s is open, releasing %s pressure", opened, currentScore);
                System.out.println(message);
            }
            String message;
            if (current.equals(previous)) {
                message = String.format("You open valve %s", current);
                int rate = edgeToRate.get(current);
                currentScore = currentScore + rate;
                opened.add(current);
            } else {
                message = String.format("You move to valve %s", current);
            }
            totalScore = totalScore + currentScore;
            System.out.println(message);
            System.out.println();
            previous = current;
        }

        current = bestPath.get(cursor);

        System.out.printf("== Minute %s ==%n", cursor);
        var message = String.format("Valve %s is open, releasing %s pressure", opened, currentScore);
        System.out.println(message);
        if (current.equals(previous)) {
            message = String.format("You open valve %s", current);
            int rate = edgeToRate.get(current);
            currentScore = currentScore + rate;
            opened.add(current);
            System.out.println(message);
        }
        totalScore = totalScore + currentScore;
        System.out.println();
        cursor++;


        for (; cursor < 27; cursor++) {
            System.out.printf("== Minute %s ==%n", cursor);
            message = String.format("Valve %s is open, releasing %s pressure", opened, currentScore);
            System.out.println(message);
            totalScore = totalScore + currentScore;
            System.out.println();
        }

    }


    private static int calculateScore(List<String> path1, Map<String, Integer> edgeToRate) {
        if (path1 == null || path1.isEmpty()) {
            return 0;
        }
        int totalScore = 0;
        int currentScore = 0;
        String current = path1.get(0);
        String previous = "N/A";
        int cursor;
        for (cursor = 1; cursor < 27; cursor++) {
            if (cursor == path1.size()) {
                if (previous.equals(current)) {
                    int rate = edgeToRate.get(current);
                    currentScore = currentScore + rate;
                }
                totalScore = totalScore + currentScore;
                continue;
            }
            if (cursor > path1.size()) {
                totalScore = totalScore + currentScore;
                continue;
            }
            if (previous.equals(current)) {
                int rate = edgeToRate.get(current);
                currentScore = currentScore + rate;
            }
            totalScore = totalScore + currentScore;
            previous = current;
            current = path1.get(cursor);
        }

        return totalScore;

    }


    private static class Edge {
        private String from;
        private String to;

        public Edge() {
        }

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

    private static class Distance {
        private String point;
        private int distance;

        public Distance(String point, int distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    private static class Path {
        private String from;
        private String to;

        public Path() {
        }

        public Path(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Path path = (Path) o;

            if (!Objects.equals(from, path.from)) return false;
            return Objects.equals(to, path.to);
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

    private static class TeamPath {
        private List<String> path1;
        private List<String> path2;

        public TeamPath() {
        }

        public TeamPath(List<String> path1, List<String> path2) {
            this.path1 = path1;
            this.path2 = path2;
        }
    }


    private static class Pair {
        private final List<List<String>> paths;
        private int hashCode;

        public Pair(List<String> path1, List<String> path2) {
            paths = new ArrayList<>(2);
            paths.add(path1);
            paths.add(path2);
            paths.sort((l1, l2) -> {
                for (int i = 0; i < l1.size(); i++) {
                    String val1 = l1.get(i);
                    String val2 = l2.get(i);
                    var val = val1.compareTo(val2);
                    if (val != 0) {
                        return val;
                    }
                }
                return 0;
            });
            hashCode = paths.hashCode();

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            return Objects.equals(paths, pair.paths);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}

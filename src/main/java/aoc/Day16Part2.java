package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class Day16Part2 {

    public static void main(String[] args) throws IOException {
        System.out.println("starting execution...");
        var start = System.currentTimeMillis();
        task1();
        var end = System.currentTimeMillis();
        var duration = (end - start) / 1000;
        System.out.println("Executed in " + duration);
    }

    private static void task1() throws IOException {
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
        Map<Path, Integer> fromTo = new HashMap<>();
        for (Map.Entry<String, Integer> from : edgeToRate.entrySet()) {
            String pointFrom = from.getKey();
            for (Map.Entry<String, Integer> to : edgeToRate.entrySet()) {
                String pointTo = to.getKey();
                if (!pointTo.equals(pointFrom)) {
                    int value = findPath(edges, pointFrom, pointTo);
                    fromTo.put(new Path(pointFrom, pointTo), value);
                }
            }
        }
        processGraph(edges, edgeToRate, fromTo);
    }


    private static int findPath(List<Edge> edges, String start, String end) {
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
        Integer min = coordToDistance.get(end).size();
        return min;
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

    private static void processGraph(List<Edge> edges, Map<String, Integer> edgeToRate, Map<Path, Integer> fromTo) {
        Map<String, List<Edge>> fromEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.from));

        Map<String, List<Edge>> sorted = new HashMap<>();
        for (Map.Entry<String, List<Edge>> entry : fromEdges.entrySet()) {
            List<Edge> lines = entry.getValue();
            var sortedEdges1 = lines.stream().filter(edge -> edgeToRate.get(edge.to) != 0)
                    .collect(Collectors.toList());
            var sortedEdges2 = lines.stream().filter(edge -> edgeToRate.get(edge.to) == 0)
                    .collect(Collectors.toList());
            sortedEdges1.addAll(sortedEdges2);
            sorted.put(entry.getKey(), sortedEdges1);
        }


        var start = "AA";
        var reachableWithNonNullRate = edgeToRate.entrySet().stream()
                .filter(entry -> entry.getValue() != 0)
                .filter(entry -> fromTo.containsKey(new Path("AA", entry.getKey())))
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> path1 = new ArrayList<>();
        path1.add(start);
        List<String> path2 = new ArrayList<>();
        path2.add(start);

        var teamPath = processIt1(edgeToRate, sorted, path1, path2, new LinkedHashSet<>(), 0
                , reachableWithNonNullRate, reachableWithNonNullRate.size(), fromTo);


        System.out.println(teamPath.path1);
        System.out.println(teamPath.path2);
        var max = calculateScore(teamPath.path1, edgeToRate) + calculateScore(teamPath.path2, edgeToRate);
        //printPath(teamPath.path1, edgeToRate);
        //printPath(teamPath.path2, edgeToRate);
        System.out.println(max);
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


    private static boolean isAnyGoodPathExist(Set<String> remainingNonNull, Map<Path, Integer> fromTo,
                                              int remainingTime, String point1, String point2) {
        if (remainingNonNull.contains(point1)) {
            return true;
        }
        for (String nonNull : remainingNonNull) {
            var nonNullPath = new Path(point1, nonNull);
            if (fromTo.containsKey(nonNullPath)) {
                if (remainingTime > fromTo.get(nonNullPath)) {
                    return true;
                }
            }
        }

        if (remainingNonNull.contains(point2)) {
            return true;
        }
        for (String nonNull : remainingNonNull) {
            var nonNullPath = new Path(point2, nonNull);
            if (fromTo.containsKey(nonNullPath)) {
                if (remainingTime > fromTo.get(nonNullPath)) {
                    return true;
                }
            }
        }

        return false;
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


    private static TeamPath processIt1(Map<String, Integer> edgeToRate, Map<String, List<Edge>> fromEdges,
                                       List<String> path1, List<String> path2, Set<String> turned,
                                       int time, Set<String> remainingNonNull, int totalNonNull, Map<Path, Integer> fromTo) {

        if (time >= 26) {
            return new TeamPath(path1, path2);
        }
        int remainingTime = 26 - time;

        if (turned.size() == totalNonNull) {
            return new TeamPath(path1, path2);
        }

        var actualRemaining = remainingNonNull.size();
        if (actualRemaining % 2 != 0) {
            actualRemaining++;
        }

        int timeTOComplete = actualRemaining * 3 / 2;
        if (
                (path1.size() >= 2 && path1.get(path1.size() - 1).equals(path1.get(path1.size() - 2))) ||
                        (path2.size() >= 2 && path2.get(path2.size() - 1).equals(path2.get(path2.size() - 2)))
        ) {
            timeTOComplete = timeTOComplete - 2;
        }
        if (remainingTime < timeTOComplete) {
            return new TeamPath();
        }


        String point1 = path1.get(path1.size() - 1);
        String point2 = path1.get(path2.size() - 1);

        boolean nonNullPaths = false;
        for (String to : remainingNonNull) {
            if (fromTo.containsKey(new Path(point1, to)) ||
                    fromTo.containsKey(new Path(point2, to))) {
                nonNullPaths = true;
                break;
            }
        }
        if (!nonNullPaths) {
            if (remainingNonNull.size() > 1) {
                System.out.println();
            }
            return new TeamPath(path1, path2);
        }


        List<TeamPath> total = new ArrayList<>();


        if (!isAnyGoodPathExist(remainingNonNull, fromTo, remainingTime, point1, point2)) {
            return new TeamPath(path1, path2);
        }


        if (!turned.contains(point1)) {
            if (edgeToRate.get(point1) != 0) {
                var newTurned = new LinkedHashSet<>(turned);
                newTurned.add(point1);
                var nextPath = new ArrayList<>(path1);
                nextPath.add(point1);
                var nextNonNull = new HashSet<>(remainingNonNull);
                nextNonNull.remove(point1);
                var res = processIt2(edgeToRate, fromEdges, nextPath, path2, newTurned, time, nextNonNull,
                        totalNonNull, fromTo);
                total.add(res);
            }
        }


        for (Edge edge : fromEdges.get(point1)) {
            String previous;
            if (path1.size() > 1) {
                previous = path1.get(path1.size() - 2);
            } else {
                previous = null;
            }
            if (!edge.to.equals(previous)) {
                var nextPath = new ArrayList<>(path1);
                nextPath.add(edge.to);
                var res = processIt2(edgeToRate, fromEdges, nextPath, path2, turned, time, remainingNonNull,
                        totalNonNull, fromTo);
                total.add(res);
            }

        }
        if (total.isEmpty()) {
            return new TeamPath(path1, path2);
        }


        int max = Integer.MIN_VALUE;
        TeamPath bestRes = new TeamPath();
        for (TeamPath teamPath : total) {


            int res = calculateScore(teamPath.path1, edgeToRate) + calculateScore(teamPath.path2, edgeToRate);
            if (res > max) {
                max = res;
                bestRes = teamPath;
            }
        }
        return bestRes;
    }


    private static TeamPath processIt2(Map<String, Integer> edgeToRate, Map<String, List<Edge>> fromEdges,
                                       List<String> path1, List<String> path2, Set<String> turned,
                                       int time, Set<String> remainingNonNull, int totalNonNull,
                                       Map<Path, Integer> fromTo) {

        String point2 = path2.get(path2.size() - 1);
        String point1 = path1.get(path1.size() - 1);
        List<TeamPath> total = new ArrayList<>();

        int remainingTime = 26 - time;

     /*   if (remainingTime < remainingNonNull.size()) {
            return new TeamPath();
        }*/

        boolean nonNullPaths = false;
        for (String to : remainingNonNull) {
            if (fromTo.containsKey(new Path(point1, to)) ||
                    fromTo.containsKey(new Path(point2, to))) {
                nonNullPaths = true;
                break;
            }
        }
        if (!nonNullPaths) {
            if (remainingNonNull.size() > 1) {
                System.out.println();
            }
            return new TeamPath(path1, path2);
        }


        if (!isAnyGoodPathExist(remainingNonNull, fromTo, remainingTime, point1, point2)) {
            return new TeamPath(path1, path2);
        }


        if (!turned.contains(point2)) {
            if (edgeToRate.get(point2) != 0) {
                var newTurned = new LinkedHashSet<>(turned);
                newTurned.add(point2);
                var nextPath = new ArrayList<>(path2);
                nextPath.add(point2);
                var nextNonNull = new HashSet<>(remainingNonNull);
                nextNonNull.remove(point2);
                var res = processIt1(edgeToRate, fromEdges, path1, nextPath, newTurned, time + 1, nextNonNull, totalNonNull, fromTo);
                total.add(res);
            }
        }

        for (Edge edge : fromEdges.get(point2)) {
            String previous;
            if (path2.size() > 1) {
                previous = path2.get(path2.size() - 2);
            } else {
                previous = null;
            }
            if (!edge.to.equals(previous)) {
                var nextPath = new ArrayList<>(path2);
                nextPath.add(edge.to);
                var res = processIt1(edgeToRate, fromEdges, path1, nextPath, turned, time + 1, remainingNonNull,
                        totalNonNull, fromTo);
                total.add(res);
            }
        }

        int max = Integer.MIN_VALUE;
        TeamPath bestRes = new TeamPath(path1, path2);
        for (TeamPath teamPath : total) {

            int res = calculateScore(teamPath.path1, edgeToRate) + calculateScore(teamPath.path2, edgeToRate);
            if (res > max) {
                max = res;
                bestRes = teamPath;
            }
        }
        return bestRes;
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

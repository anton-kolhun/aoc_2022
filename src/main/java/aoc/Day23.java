package aoc;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 {


    public static void main(String[] args) throws IOException, URISyntaxException {
        //task1();
        task2();
    }

    private static void task1() throws IOException {
        List<String> lines = FilesUtilS.readFile("day23.txt");
        Set<Coordinate> heroes = new HashSet<>();
        Map<Coordinate, Character> coordsToValue = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                var coord = new Coordinate(row, col);
                coordsToValue.put(coord, c);
                if (c == '#') {
                    heroes.add(coord);
                }
            }
        }
        process(heroes);
    }

    private static void process(Set<Coordinate> heroes) {
        Map<Coordinate, List<State>> curToPrev = new HashMap<>();
        for (Coordinate hero : heroes) {
            List<State> states = new ArrayList<>();
            states.add(new State(hero, hero));
            curToPrev.put(hero, states);
        }
        printMap(curToPrev, 0);
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.add(Direction.WEST);
        directions.add(Direction.EAST);
        Map<Direction, DirectionResolver> dirResolver = Map.of(
                Direction.NORTH, Day23::northFunc,
                Direction.SOUTH, Day23::southFunc,
                Direction.EAST, Day23::eastFunc,
                Direction.WEST, Day23::westFunc
        );
        for (int round = 0; round < 10; round++) {
            Map<Coordinate, List<State>> nextMap = new HashMap<>();
            for (List<State> states : curToPrev.values()) {
                for (State state : states) {
                    if (isNeighbourAround(state.current, curToPrev)) {
                        Coordinate move = getMove(state.current, curToPrev, directions, dirResolver);
                        var nextCoord = new Coordinate(state.current.row + move.row, state.current.col + move.col);
                        updateMap(new State(state.current, nextCoord), nextMap, nextCoord);
                    } else {
                        updateMap(state, nextMap, state.current);
                    }
                }
            }
            processConflicts(nextMap, curToPrev);
            curToPrev = nextMap;
            printMap(nextMap, round + 1);
            rotateDirections(directions);
        }

        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int maxCol = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        for (Coordinate coordinate : curToPrev.keySet()) {
            if (coordinate.row > maxRow) {
                maxRow = coordinate.row;
            }
            if (coordinate.row < minRow) {
                minRow = coordinate.row;
            }
            if (coordinate.col > maxCol) {
                maxCol = coordinate.col;
            }
            if (coordinate.col < minCol) {
                minCol = coordinate.col;
            }
        }
        int square = (maxRow - minRow + 1) * (maxCol - minCol + 1) - curToPrev.size();
        System.out.println(square);

    }

    private static void updateMap(State state, Map<Coordinate, List<State>> nextMap, Coordinate state1) {
        List<State> list = new ArrayList<>();
        list.add(state);
        nextMap.merge(state1, list, (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        });
    }

    private static void rotateDirections(List<Direction> directions) {
        var first = directions.remove(0);
        directions.add(first);
    }

    private static void printMap(Map<Coordinate, List<State>> map, int round) {
        System.out.println("Round = " + round);

        Set<Coordinate> next = map.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 0)
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(state -> state.current)
                .collect(Collectors.toSet());

        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int maxCol = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE;
        for (Coordinate coordinate : next) {
            if (coordinate.row > maxRow) {
                maxRow = coordinate.row;
            }
            if (coordinate.row < minRow) {
                minRow = coordinate.row;
            }
            if (coordinate.col > maxCol) {
                maxCol = coordinate.col;
            }
            if (coordinate.col < minCol) {
                minCol = coordinate.col;
            }
        }

        for (int row = minRow; row <= maxRow; row++) {
            // System.out.print(row + " ");
            for (int col = minCol; col <= maxCol; col++) {
                if (next.contains(new Coordinate(row, col))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    private static void processConflicts(Map<Coordinate, List<State>> nextMap, Map<Coordinate, List<State>> currentMap) {
        List<Coordinate> conflicting = nextMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Coordinate coord : conflicting) {
            handleConflict(nextMap, currentMap, coord);
        }
    }

    private static void handleConflict(Map<Coordinate, List<State>> nextMap, Map<Coordinate, List<State>> currentMap,
                                       Coordinate conflict) {
        List<State> conflStates = nextMap.get(conflict);
        for (Iterator<State> iterator = conflStates.iterator(); iterator.hasNext(); ) {
            State conflState = iterator.next();
            if (!conflState.current.equals(conflState.previous)) {
                iterator.remove();
                updateMap(new State(conflState.previous, conflState.previous), nextMap, conflState.previous);
            }
        }
        if (nextMap.get(conflict).isEmpty()) {
            nextMap.remove(conflict);
        }
        processConflicts(currentMap, nextMap);
    }


    private static Coordinate getMoveByDirection(Direction dir) {
        if (dir == Direction.NORTH) {
            return new Coordinate(-1, 0);
        }
        if (dir == Direction.SOUTH) {
            return new Coordinate(1, 0);
        }
        if (dir == Direction.EAST) {
            return new Coordinate(0, 1);
        }
        if (dir == Direction.WEST) {
            return new Coordinate(0, -1);
        }
        throw new RuntimeException("smth went wrong");
    }

    private static boolean isNeighbourAround(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious) {
        if (currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row, hero.col + 1))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row, hero.col - 1))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col + 1))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col - 1))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col + 1))) {
            return true;
        }
        if (currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col - 1))) {
            return true;
        }
        return false;
    }

    private static Coordinate getMove(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious,
                                      List<Direction> directions, Map<Direction, DirectionResolver> dirResolver) {
        for (Direction direction : directions) {
            Optional<Direction> res = dirResolver.get(direction).apply(hero, currentToPrevious);
            if (res.isPresent()) {
                return getMoveByDirection(res.get());
            }
        }
        return new Coordinate(0, 0);
    }

    private static Optional<Direction> eastFunc(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious) {
        if (!(currentToPrevious.containsKey(new Coordinate(hero.row, hero.col + 1)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col + 1)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col + 1)))) {
            return Optional.of(Direction.EAST);
        }
        return Optional.empty();
    }

    private static Optional<Direction> westFunc(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious) {
        if (!(currentToPrevious.containsKey(new Coordinate(hero.row, hero.col - 1)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col - 1)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col - 1)))) {
            return Optional.of(Direction.WEST);

        }
        return Optional.empty();
    }

    private static Optional<Direction> southFunc(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious) {
        if (!(currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col + 1)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row + 1, hero.col - 1)))) {
            return Optional.of(Direction.SOUTH);
        }
        return Optional.empty();
    }

    private static Optional<Direction> northFunc(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious) {
        if (!(currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col + 1)) ||
                currentToPrevious.containsKey(new Coordinate(hero.row - 1, hero.col - 1)))) {
            return Optional.of(Direction.NORTH);
        }
        return Optional.empty();
    }


    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static class Coordinate {
        int row;
        int col;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class State {
        Coordinate previous;
        Coordinate current;
    }


    private enum Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST;
    }


    @FunctionalInterface
    private interface DirectionResolver {
        Optional<Direction> apply(Coordinate hero, Map<Coordinate, List<State>> currentToPrevious);
    }


    private static void task2() throws IOException, URISyntaxException {
        List<String> lines = FilesUtilS.readFile("day23.txt");
        Set<Coordinate> heroes = new HashSet<>();
        Map<Coordinate, Character> coordsToValue = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            char[] charArray = line.toCharArray();
            for (int col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                var coord = new Coordinate(row, col);
                coordsToValue.put(coord, c);
                if (c == '#') {
                    heroes.add(coord);
                }
            }
        }
        process2(heroes);
    }

    private static void process2(Set<Coordinate> heroes) {
        Map<Coordinate, List<State>> curToPrev = new HashMap<>();
        for (Coordinate hero : heroes) {
            List<State> states = new ArrayList<>();
            states.add(new State(hero, hero));
            curToPrev.put(hero, states);
        }
        printMap(curToPrev, 0);
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.add(Direction.WEST);
        directions.add(Direction.EAST);
        Map<Direction, DirectionResolver> dirResolver = Map.of(
                Direction.NORTH, Day23::northFunc,
                Direction.SOUTH, Day23::southFunc,
                Direction.EAST, Day23::eastFunc,
                Direction.WEST, Day23::westFunc
        );
        int round = 0;
        while (true) {
            round++;
            Map<Coordinate, List<State>> nextMap = new HashMap<>();
            for (List<State> states : curToPrev.values()) {
                for (State state : states) {
                    if (isNeighbourAround(state.current, curToPrev)) {
                        Coordinate move = getMove(state.current, curToPrev, directions, dirResolver);
                        var nextCoord = new Coordinate(state.current.row + move.row, state.current.col + move.col);
                        updateMap(new State(state.current, nextCoord), nextMap, nextCoord);
                    } else {
                        updateMap(state, nextMap, state.current);
                    }
                }
            }
            processConflicts(nextMap, curToPrev);
            if (areMapsEqual(curToPrev, nextMap)) {
                System.out.println(round);
                return;
            }
            curToPrev = nextMap;
            rotateDirections(directions);
        }
    }

    private static boolean areMapsEqual(Map<Coordinate, List<State>> curToPrev, Map<Coordinate, List<State>> nextMap) {
        for (Map.Entry<Coordinate, List<State>> prev : curToPrev.entrySet()) {
            var prevStates = new HashSet<>(prev.getValue());
            var nextStates = new HashSet<>(nextMap.getOrDefault(prev.getKey(), new ArrayList<>()));
            if (!prevStates.equals(nextStates)) {
                return false;
            }
        }
        return true;
    }
}
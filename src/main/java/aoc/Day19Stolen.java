package aoc;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day19Stolen {

    private static final int ORE_ROBOT = 0;
    private static final int CLAY_ROBOT = 1;
    private static final int OB_ROBOT = 2;
    private static final int GEO_ROBOT = 3;

    private static Map<State, Integer> cache = new HashMap<>();

    private static List<Blueprint> getBlueprints(final List<String> lines) {
        final List<Blueprint> blueprints = new ArrayList<>();
    /*    var cursor = new Blueprint();
        cursor = parseTestBluePrint(lines, blueprints, cursor);
        blueprints.add(cursor);*/

        final Pattern p = Pattern.compile(
                "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
        for (final String line : lines) {
            final Matcher m = p.matcher(line);
            if (m.find()) {
                blueprints.add(new Blueprint(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)),
                        Integer.parseInt(m.group(6)), Integer.parseInt(m.group(7))));
            }
        }
        return blueprints;
    }

    private static Blueprint parseTestBluePrint(List<String> lines, List<Blueprint> blueprints, Blueprint cursor) {
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("Blueprint")) {
                blueprints.add(cursor);
                cursor = new Blueprint();
                continue;
            }
            if (line.contains("Each ore robot")) {
                String[] parts = line.split(" ");
                String ore = parts[6];
                cursor.oreCost = Integer.parseInt(ore);
            }
            if (line.contains("Each clay robot")) {
                String[] parts = line.split(" ");
                String clay = parts[6];
                cursor.clayOreCost = Integer.parseInt(clay);
            }
            if (line.contains("Each obsidian robot")) {
                String[] parts = line.split(" ");
                cursor.obOreCost = Integer.parseInt(parts[6]);
                cursor.obClayCost = Integer.parseInt(parts[9]);
            }
            if (line.contains("Each geode robot")) {
                String[] parts = line.split(" ");
                cursor.geoOreCost = Integer.parseInt(parts[6]);
                cursor.geoObCost = Integer.parseInt(parts[9]);
            }
        }
        return cursor;
    }

    private static int getMaxGeodesForType(final Blueprint b, int minutesLeft, final int goal, int nrOre, int nrClay, int nrOb,
                                           int nrGeo,
                                           final int nrOreRobots, final int nrClayRobots, final int nrObRobots, final int nrGeoRobots) {
        if (minutesLeft == 0) {
            return nrGeo;
        }
        // Stop building a robot if we have more of the resource it builds than we need.
        final int maxOre = Math.max(b.oreCost, Math.max(b.clayOreCost, Math.max(b.obOreCost, b.geoObCost)));
        if (goal == ORE_ROBOT && nrOre >= maxOre || goal == CLAY_ROBOT && nrClay >= b.obClayCost
                || goal == OB_ROBOT && (nrOb >= b.geoObCost || nrClay == 0) || goal == GEO_ROBOT && nrOb == 0) {
            return 0;
        }

        final State state = new State(nrOre, nrClay, nrOb, nrGeo, nrOreRobots, nrClayRobots, nrObRobots, nrGeoRobots,
                minutesLeft, goal);

        if (cache.containsKey(state)) {
            return cache.get(state);
        }
        int max = 0;

        while (minutesLeft > 0) {
            if (goal == ORE_ROBOT && nrOre >= b.oreCost) { // Build ore robot
                int tmpMax = 0;
                for (int newGoal = 0; newGoal < 4; newGoal++) {
                    tmpMax = Math.max(tmpMax,
                            getMaxGeodesForType(b, minutesLeft - 1, newGoal, nrOre - b.oreCost + nrOreRobots,
                                    nrClay + nrClayRobots, nrOb + nrObRobots, nrGeo + nrGeoRobots, nrOreRobots + 1,
                                    nrClayRobots, nrObRobots, nrGeoRobots));
                }
                max = Math.max(max, tmpMax);
                cache.put(state, max);
                return max;
            } else if (goal == CLAY_ROBOT && nrOre >= b.clayOreCost) { // Build clay robot
                int tmpMax = 0;
                for (int newGoal = 0; newGoal < 4; newGoal++) {
                    tmpMax = Math.max(tmpMax,
                            getMaxGeodesForType(b, minutesLeft - 1, newGoal, nrOre - b.clayOreCost + nrOreRobots,
                                    nrClay + nrClayRobots, nrOb + nrObRobots, nrGeo + nrGeoRobots, nrOreRobots,
                                    nrClayRobots + 1, nrObRobots, nrGeoRobots));
                }
                max = Math.max(max, tmpMax);
                cache.put(state, max);
                return max;
            } else if (goal == OB_ROBOT && nrOre >= b.obOreCost && nrClay >= b.obClayCost) { // Build ob robot
                int tmpMax = 0;
                for (int newGoal = 0; newGoal < 4; newGoal++) {
                    tmpMax = Math.max(tmpMax,
                            getMaxGeodesForType(b, minutesLeft - 1, newGoal, nrOre - b.obOreCost + nrOreRobots,
                                    nrClay - b.obClayCost + nrClayRobots, nrOb + nrObRobots, nrGeo + nrGeoRobots,
                                    nrOreRobots, nrClayRobots, nrObRobots + 1, nrGeoRobots));
                }
                max = Math.max(max, tmpMax);
                cache.put(state, max);
                return max;
            } else if (goal == GEO_ROBOT && nrOre >= b.geoOreCost && nrOb >= b.geoObCost) { // Build geo robot
                int tmpMax = 0;
                for (int newGoal = 0; newGoal < 4; newGoal++) {
                    tmpMax = Math.max(tmpMax,
                            getMaxGeodesForType(b, minutesLeft - 1, newGoal, nrOre - b.geoOreCost + nrOreRobots,
                                    nrClay + nrClayRobots, nrOb - b.geoObCost + nrObRobots, nrGeo + nrGeoRobots,
                                    nrOreRobots, nrClayRobots, nrObRobots, nrGeoRobots + 1));
                }
                max = Math.max(max, tmpMax);
                cache.put(state, max);
                return max;
            }
            // Can not build a robot, so continue gathering resources.
            minutesLeft--;
            nrOre += nrOreRobots;
            nrClay += nrClayRobots;
            nrOb += nrObRobots;
            nrGeo += nrGeoRobots;
            max = Math.max(max, nrGeo);
        }
        cache.put(state, max);
        return max;
    }

    private static int getMaxGeodes(final Blueprint b, final int nrMinutes) {
        cache = new HashMap<>();
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = Math.max(result, getMaxGeodesForType(b, nrMinutes, i, 0, 0, 0, 0, 1, 0, 0, 0));
        }
        return result;
    }

    private static int getQualityLevel(final Blueprint b, int blueprintNumber) {
        var res = getMaxGeodes(b, 24) * blueprintNumber;
        return res;
    }

/*    protected String runPart2(final List<String> input) {
        List<Blueprint> blueprints = getBlueprints(input);
        blueprints = blueprints.subList(0, Math.min(3, blueprints.size()));
        return String.valueOf(blueprints.stream().mapToInt(b -> getMaxGeodes(b, 32)).reduce(1, (a, b) -> a * b));
    }*/

    private static long runPart1(final List<String> lines) throws IOException {
        final List<Blueprint> blueprints = getBlueprints(lines);
        int sum = 0;
        for (int i = 0; i < blueprints.size(); i++) {
            Blueprint blueprint = blueprints.get(i);
            sum += getQualityLevel(blueprint, i + 1);
        }
        return sum;
    }

    protected static String runPart2(final List<String> input) {
        List<Blueprint> blueprints = getBlueprints(input);
        blueprints = blueprints.subList(0, Math.min(3, blueprints.size()));
        return String.valueOf(blueprints.stream().mapToInt(b -> getMaxGeodes(b, 32)).reduce(1, (a, b) -> a * b));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Blueprint {
        int nr;
        int oreCost;
        int clayOreCost;
        int obOreCost;
        int obClayCost;
        int geoOreCost;
        int geoObCost;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class State {
        int nrOre;
        int nrClay;
        int nrOb;
        int nrGeo;
        int nrOreRobot;
        int nrClayRobot;
        int nrObRobot;
        int nrGeoRobot;
        int minutesLeft;
        int goal;
    }

    public static void main(final String... args) throws IOException {
        var result = runPart1(FilesUtilS.readFile("day19.txt"));
        System.out.println(result);
    }
}
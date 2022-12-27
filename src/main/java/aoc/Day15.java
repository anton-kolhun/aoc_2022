package aoc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day15 {


    public static void main(String[] args) throws IOException {
        task1();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day15.txt");
        var sensorToB = readSensors(lines);
        System.out.println(sensorToB);

        for (int row = 0; row <= 4000000; row++) {
            List<Range> ranges = new ArrayList<>();

            for (Map.Entry<Day14.Coordinate, Day14.Coordinate> entry : sensorToB.entrySet()) {
                var sensor = entry.getKey();
                var b = entry.getValue();
                int strength = Math.abs(sensor.row - b.row) + Math.abs(sensor.col - b.col) - Math.abs(row - sensor.row);
                if (strength >= 0) {
                    int start = sensor.col - strength;
                    int end = sensor.col + strength;
                    var range = new Range(start, end);
                    ranges.add(range);
                }
            }

            for (Iterator<Range> iterator = ranges.iterator(); iterator.hasNext(); ) {
                Range range = iterator.next();
                if (range.end < 0) {
                    iterator.remove();
                } else if (range.start > 4000000) {
                    iterator.remove();
                } else if (range.start < 0) {
                    range.start = 0;
                } else if (range.end > 4000000) {
                    range.end = 4000000;
                }
            }


            var sorted = ranges.stream()
                    .sorted(Comparator.comparingInt(o -> o.start))
                    .collect(Collectors.toList());

            var merged = sorted.get(0);
            for (int i = 1; i < sorted.size(); i++) {
                Range next = sorted.get(i);
                if (merged.end >= next.start) {
                    if (next.end >= merged.end) {
                        merged.end = next.end;
                    } else {
                        //next is a part of merged
                    }
                } else {
                    int col = merged.end + 1;
                    System.out.println(row + " " + col);
                    BigDecimal val = BigDecimal.valueOf(col);
                    val = val.multiply(BigDecimal.valueOf(4000000));
                    val = val.add(BigDecimal.valueOf(row));
                    System.out.println(val);
                    return;
                }
            }

        }


        Set<Day14.Coordinate> noBCoords = new HashSet<>();
    }

    private static Map<Day14.Coordinate, Day14.Coordinate> readSensors(List<String> lines) {
        Map<Day14.Coordinate, Day14.Coordinate> coordToB = new LinkedHashMap<>();
        for (String line : lines) {
            String[] values = line.split(" ");
            String sensorCol = values[2];
            sensorCol = sensorCol.substring(0, sensorCol.length() - 1);
            var sensorColVal = sensorCol.split("=")[1];

            String sensorRow = values[3];
            sensorRow = sensorRow.substring(0, sensorRow.length() - 1);
            var sensorRowVal = sensorRow.split("=")[1];
            var sensorCoord = new Day14.Coordinate(Integer.parseInt(sensorRowVal), Integer.parseInt(sensorColVal));

            String bCol = values[8];
            bCol = bCol.substring(0, bCol.length() - 1);
            var bColVal = bCol.split("=")[1];
            String bRow = values[9];
            var bRowVal = bRow.split("=")[1];
            var bCoord = new Day14.Coordinate(Integer.parseInt(bRowVal), Integer.parseInt(bColVal));
            coordToB.put(sensorCoord, bCoord);
        }
        return coordToB;
    }

    private static class Range {
        int start;
        int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}

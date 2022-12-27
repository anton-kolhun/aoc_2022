package aoc;

import aoc.Day22Part1.Coordinate;
import aoc.Day22Part1.Position;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class Day22Part2 {


    public static Map<Integer, Map<Position, BlockInfo>> blockMoves = new HashMap<>();

    public static List<Block> blocks = new ArrayList<>();

    static {
        initMyBlocks();
//        initTestBlocks();
    }


    private static void initMyBlocks() {
        var block1 = new Block(1, 0, 49, 50, 99);
        var block2 = new Block(2, 0, 49, 100, 149);
        var block3 = new Block(3, 50, 99, 50, 99);
        var block4 = new Block(4, 100, 149, 0, 49);
        var block5 = new Block(5, 100, 149, 50, 99);
        var block6 = new Block(6, 150, 199, 0, 49);
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        blocks.add(block6);

        Map<Position, BlockInfo> block1Teleports = Map.of(
                Position.UP, new BlockInfo(block6, Position.RIGHT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colStart)),
                Position.LEFT, new BlockInfo(block4, Position.RIGHT, (old, block) -> new Coordinate(block.rowEnd - old.row, block.colStart)));
        blockMoves.put(1, block1Teleports);

        Map<Position, BlockInfo> block2Teleports = Map.of(
                Position.UP, new BlockInfo(block6, Position.UP, (old, block) -> new Coordinate(block.rowEnd, block.colStart + old.column)),
                Position.DOWN, new BlockInfo(block3, Position.LEFT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colEnd)),
                Position.RIGHT, new BlockInfo(block5, Position.LEFT, (old, block) -> new Coordinate(block.rowEnd - old.row, block.colEnd))
        );
        blockMoves.put(2, block2Teleports);

        Map<Position, BlockInfo> block3Teleports = Map.of(
                Position.LEFT, new BlockInfo(block4, Position.DOWN, (old, block) -> new Coordinate(block.rowStart, block.colStart + old.row)),
                Position.RIGHT, new BlockInfo(block2, Position.UP, (old, block) -> new Coordinate(block.rowEnd, block.colStart + old.row)));
        blockMoves.put(3, block3Teleports);

        Map<Position, BlockInfo> block4Teleports = Map.of(
                Position.LEFT, new BlockInfo(block1, Position.RIGHT, (old, block) -> new Coordinate(block.rowEnd - old.row, block.colStart)),
                Position.UP, new BlockInfo(block3, Position.RIGHT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colStart))
        );
        blockMoves.put(4, block4Teleports);


        Map<Position, BlockInfo> block5Teleports = Map.of(
                Position.DOWN, new BlockInfo(block6, Position.LEFT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colEnd)),
                Position.RIGHT, new BlockInfo(block2, Position.LEFT, (old, block) -> new Coordinate(block.rowEnd - old.row, block.colEnd))
        );
        blockMoves.put(5, block5Teleports);

        Map<Position, BlockInfo> block6Teleports = Map.of(

                Position.LEFT, new BlockInfo(block1, Position.DOWN, (old, block) -> new Coordinate(block.rowStart, block.colStart + old.row)),
                Position.RIGHT, new BlockInfo(block5, Position.UP, (old, block) -> new Coordinate(block.rowEnd, block.colStart + old.row)),
                Position.DOWN, new BlockInfo(block2, Position.DOWN, (old, block) -> new Coordinate(block.rowStart, block.colStart + old.column))
        );
        blockMoves.put(6, block6Teleports);


    }


    public static void main(String[] args) throws IOException {
        task();
    }

    private static void task() throws IOException {
        List<String> lines = FilesUtilS.readFile("day22.txt");
        Map<Coordinate, String> coordToValue = new HashMap<>();
        Map<Integer, Integer> rowToColStart = new HashMap<>();
        Map<Integer, Integer> rowToColEnd = new HashMap<>();
        int row;
        int maxCol = 0;
        for (row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            if (line.isEmpty()) {
                break;
            }
            char[] charArray = line.toCharArray();
            boolean started = false;
            maxCol = Math.max(maxCol, charArray.length);
            int col;
            for (col = 0; col < charArray.length; col++) {
                char c = charArray[col];
                String value = String.valueOf(c).trim();
                if (!value.isEmpty()) {
                    coordToValue.put(new Coordinate(row, col), value);
                    if (!started) {
                        rowToColStart.put(row, col);
                    }
                    started = true;
                }
            }
            rowToColEnd.put(row, col - 1);
        }

        Map<Integer, Integer> colToRowStart = new HashMap<>();
        Map<Integer, Integer> colToRowEnd = new HashMap<>();
        int maxRow = row;
        populateColToRowStart(coordToValue, colToRowStart, colToRowEnd, maxRow, maxCol);

        String pass = lines.get(row + 1);
        List<String> moves = parsePass(pass);
        //System.out.println(coordToValue);
        int colStart = rowToColStart.get(0);


        navigate(new Coordinate(0, colStart), coordToValue, rowToColStart, rowToColEnd, moves, Position.RIGHT,
                colToRowStart, colToRowEnd, maxRow, maxCol);

    }


    private static void navigate(Coordinate current, Map<Coordinate, String> coordToValue, Map<Integer, Integer> rowToColStart,
                                 Map<Integer, Integer> rowToColEnd, List<String> moves, Position initialPos,
                                 Map<Integer, Integer> colToRowStart, Map<Integer, Integer> colToRowEnd, int maxRow, int maxCol) {
        var pos = initialPos;
        for (String move : moves) {
            try {
                int steps = Integer.parseInt(move);
                var currentInfo = doSteps(current, pos, steps, coordToValue, rowToColStart, rowToColEnd, colToRowStart, colToRowEnd);
                current = currentInfo.coord;
                pos = currentInfo.pos;
     /*           printMatrix(current, coordToValue, maxRow, maxCol, pos);
                System.out.printf("Steps = %s, Position = %s", steps, pos.toString());
                System.out.println();*/
            } catch (Exception e) {
                int newVal;
                if (move.equals("R")) {
                    newVal = pos.value + 1;
                } else {
                    newVal = pos.value - 1;
                }
                pos = Position.fromValue(newVal);
            }
        }
        int formual = 1000 * (current.row + 1) + 4 * (current.column + 1) + pos.value;
        System.out.println(formual);
    }


    private static Map<Integer, Coordinate> calcualteForlolock4(Map<Coordinate, String> coordToValue, int blockSize) {
        int block4ColStart = 8;
        int block4ColEnd = 11;
        int block4RowStart = 4;
        int block4RowEnd = 7;
        return new HashMap<>();
    }


    private static void populateColToRowStart(Map<Coordinate, String> coordToValue, Map<Integer, Integer> colToRowStart,
                                              Map<Integer, Integer> colToRowEnd, int maxRow, int maxCol) {
        for (int col = 0; col <= maxCol; col++) {
            boolean started = false;
            for (int row = 0; row <= maxRow; row++) {
                if (!started && coordToValue.get(new Coordinate(row, col)) != null) {
                    colToRowStart.put(col, row);
                    started = true;
                    continue;
                }
                if (started && !coordToValue.containsKey(new Coordinate(row, col))) {
                    colToRowEnd.put(col, row - 1);
                    break;
                }
            }
        }
    }

    private static List<String> parsePass(String pass) {
        int cursorDigit = 0;
        int lastLetterIndex = 0;
        List<String> moves = new ArrayList<>();
        char[] charArray = pass.toCharArray();
        int i;
        for (i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isLetter(c)) {
                String previous = pass.substring(cursorDigit, i);
                moves.add(previous);
                moves.add(String.valueOf(c));
                lastLetterIndex = i;
                cursorDigit = i + 1;
            }
        }
        String lastNumber = pass.substring(lastLetterIndex + 1, pass.length());
        moves.add(lastNumber);
        return moves;
    }

    private static CoordInfo doSteps(Coordinate current, Position pos, int steps, Map<Coordinate, String> coordToValue,
                                     Map<Integer, Integer> rowToColStart, Map<Integer, Integer> rowToColEnd,
                                     Map<Integer, Integer> colToRowStart, Map<Integer, Integer> colToRowEnd) {
        Coordinate move = getMoveByPosition(pos);
        for (int i = 0; i < steps; i++) {
            var nextCoord = new Coordinate(current.row + move.row, current.column + move.column);
            var value = coordToValue.getOrDefault(nextCoord, "teleport");
            if (value.equals("#")) {
                return new CoordInfo(current, pos);
            }
            if (value.equals("teleport")) {
                CoordInfo teleported = calculateBlock(current, pos, coordToValue);
                if (coordToValue.get(teleported.coord).equals("#")) {
                    return new CoordInfo(current, pos);
                }
                current = teleported.coord;
                move = getMoveByPosition(teleported.pos);
                pos = teleported.pos;
            } else {
                current = nextCoord;
            }
        }

        return new CoordInfo(current, pos);
    }

    private static Coordinate getMoveByPosition(Position pos) {
        if (pos == Position.RIGHT) {
            return new Coordinate(0, 1);
        }
        if (pos == Position.LEFT) {
            return new Coordinate(0, -1);
        }
        if (pos == Position.DOWN) {
            return new Coordinate(1, 0);
        }
        if (pos == Position.UP) {
            return new Coordinate(-1, 0);
        }
        throw new RuntimeException("not-found");
    }

    private static CoordInfo calculateBlock(Coordinate current, Position pos, Map<Coordinate, String> coordToValue) {
        Block oldBlock = findBlock(current);
        var blockInfo = blockMoves.get(oldBlock.index).get(pos);
        var newCoord = blockInfo.rotator.apply(new Coordinate(current.row - oldBlock.rowStart, current.column - oldBlock.colStart), blockInfo.block);
        return new CoordInfo(newCoord, blockInfo.pos);

    }

    private static Block findBlock(Coordinate current) {
        for (Block block : blocks) {
            if (block.rowStart <= current.row && block.rowEnd >= current.row
                    && block.colStart <= current.column && block.colEnd >= current.column) {
                return block;
            }
        }
        throw new RuntimeException("not found");
    }

    @AllArgsConstructor
    private static class CoordInfo {
        private Coordinate coord;
        private Position pos;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Block {
        private int index;
        private int rowStart;
        private int rowEnd;
        private int colStart;
        private int colEnd;


    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class BlockInfo {
        private Block block;
        private Position pos;
        BiFunction<Coordinate, Block, Coordinate> rotator;
    }

   /* private static void initTestBlocks() {
        var block1 = new Block(1, 0, 3, 8, 11);
        var block2 = new Block(2, 4, 7, 0, 3);
        var block3 = new Block(3, 4, 7, 4, 7);
        var block4 = new Block(4, 4, 7, 8, 11);
        var block5 = new Block(5, 8, 11, 8, 11);
        var block6 = new Block(6, 8, 11, 12, 15);
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        blocks.add(block6);


        Map<Position, BlockInfo> block4Teleports = Map.of(
                Position.RIGHT, new BlockInfo(block6, Position.DOWN, (old, block) -> new Coordinate(block.rowStart, block.colEnd - old.row)));
        blockMoves.put(4, block4Teleports);

        Map<Position, BlockInfo> block3Teleports = Map.of(
                Position.UP, new BlockInfo(block1, Position.RIGHT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colStart)),
                Position.DOWN, new BlockInfo(block5, Position.RIGHT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colStart)));

        blockMoves.put(3, block3Teleports);

        Map<Position, BlockInfo> block2Teleports = Map.of(
                Position.UP, new BlockInfo(block1, Position.DOWN, (old, block) -> new Coordinate(block.rowEnd, block.colEnd - old.column)),
                Position.DOWN, new BlockInfo(block5, Position.UP, (old, block) -> new Coordinate(block.rowEnd, block.colEnd - old.column)));
        blockMoves.put(2, block2Teleports);

        Map<Position, BlockInfo> block5Teleports = Map.of(
                Position.LEFT, new BlockInfo(block3, Position.UP, (old, block) -> new Coordinate(block.rowEnd, block.rowStart + old.column)),
                Position.DOWN, new BlockInfo(block2, Position.UP, (old, block) -> new Coordinate(block.rowEnd, block.colEnd - old.column)));
        blockMoves.put(5, block5Teleports);

        Map<Position, BlockInfo> block1Teleports = Map.of(
                Position.LEFT, new BlockInfo(block3, Position.DOWN, (old, block) -> new Coordinate(block.rowStart, block.rowStart + old.column)),
                Position.RIGHT, new BlockInfo(block6, Position.LEFT, (old, block) -> new Coordinate(block.rowStart + old.column, block.rowEnd)),
                Position.UP, new BlockInfo(block2, Position.DOWN, (old, block) -> new Coordinate(block.rowStart, block.colEnd - old.column))
        );
        blockMoves.put(1, block1Teleports);

        Map<Position, BlockInfo> block6Teleports = Map.of(
                Position.UP, new BlockInfo(block4, Position.LEFT, (old, block) -> new Coordinate(block.rowEnd - old.column, block.colEnd)),
                Position.DOWN, new BlockInfo(block2, Position.RIGHT, (old, block) -> new Coordinate(block.rowStart + old.column, block.colStart)));
        blockMoves.put(6, block6Teleports);
    }*/

}

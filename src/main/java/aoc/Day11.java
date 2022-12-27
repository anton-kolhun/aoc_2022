package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day11 {

    public static void main(String[] args) throws IOException {
        task2();
    }

  /*  private static void task1() throws IOException {
        List<MonkeyOp> monkeys = initMonkeysOps();
        Map<Integer, Integer> monkeyItemsCount = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < monkeys.size(); j++) {
                MonkeyOp monkey = monkeys.get(j);
                monkeyItemsCount.merge(j, monkey.items.size(), Integer::sum);
                for (Iterator<Long> iterator = monkey.items.iterator(); iterator.hasNext(); ) {
                    Long item = iterator.next();
                    long newVal = monkey.operate(item);
                    long dividedBy3 = newVal / 3;
                    int passTo = (int) monkey.testDivisible(dividedBy3);
                    monkeys.get(passTo).items.add(dividedBy3);
                    iterator.remove();
                }
            }
            System.out.println("end of round");
        }

        var sorted = monkeyItemsCount.values().stream()
                .sorted((o1, o2) -> o2 - o1)
                .collect(Collectors.toList());
        int val = sorted.get(0) * sorted.get(1);
        System.out.println(val);
    }*/

    private static List<MonkeyOp> initMonkeysOps() throws IOException {
        List<String> lines = FilesUtilS.readFile("day11.txt");
        List<MonkeyOp> monkeys = new ArrayList<>();
        MonkeyOp currentMonkey = new MonkeyOp();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                monkeys.add(currentMonkey);
                currentMonkey = new MonkeyOp();
            }
            if (line.contains("Starting items")) {
                currentMonkey.initItems(line.trim());
            } else if (line.contains("Operation:")) {
                currentMonkey.initOperation(line.trim());
            } else if (line.contains("Test:")) {
                line = line.trim();
                String divideBy = line.split(" ")[3];
                String trueMonkey = lines.get(++i).trim().split(" ")[5];
                String falseMonkey = lines.get(++i).trim().split(" ")[5];
                String expr = divideBy + " " + trueMonkey + " " + falseMonkey;
                currentMonkey.initTestDivisible(expr);
            }
        }
        monkeys.add(currentMonkey);
        return monkeys;
    }

    private static class MonkeyOp {
        List<Numb> items = new ArrayList<>();
        private int divideBy;
        private Function<Numb, Numb> op;
        private Function<Numb, Long> testDivisible;

        public MonkeyOp() {
        }

        public MonkeyOp(String itemsExpr, String operationExpr, String divisibleExpr) {
            initItems(itemsExpr);
            initOperation(operationExpr);
            initTestDivisible(divisibleExpr);
        }


        public void initItems(String itemsExpr) {
            int startFrom = itemsExpr.indexOf(":");
            String values = itemsExpr.substring(startFrom + 2);
            String[] strItems = values.split(",");
            for (String strItem : strItems) {
                var trimmed = strItem.trim();
                Long val = Long.parseLong(trimmed);
                var numb = new Numb(val);
                items.add(numb);
            }
        }

        public void initTestDivisible(String divisibleExpr) {
            String[] parts = divisibleExpr.split(" ");
            int divideBy = Integer.parseInt(parts[0]);
            this.divideBy = divideBy;
            testDivisible = value -> {
               long simplified = value.modOf(divideBy);
                if ((simplified % divideBy) == 0) {
                    return Long.parseLong(parts[1]);
                }
                return Long.parseLong(parts[2]);
            };
        }

        public void initOperation(String operationExpr) {
            String[] operation = operationExpr.split(" ");
            if (operation[4].equals("*")) {
                op = (old) -> {
                    if (operation[5].equals("old")) {
                        return old.square();
                    }
                    return old.multiplyNum(Long.parseLong(operation[5]));
                };
            } else if (operation[4].equals("+")) {
                op = (old) -> old.addNumb(Long.parseLong(operation[5]));
            } else {
                throw new RuntimeException("no found operation");
            }
        }

        public Numb operate(Numb value) {
            return op.apply(value);
        }

        public long testDivisible(Numb value) {
            return testDivisible.apply(value);
        }
    }

    private static void task2() throws IOException {
        List<MonkeyOp> monkeys = initMonkeysOps();
        Map<Integer, Long> monkeyItemsCount = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            System.out.println("round = " + (i + 1));
            for (int j = 0; j < monkeys.size(); j++) {
                MonkeyOp monkey = monkeys.get(j);
                monkeyItemsCount.merge(j, (long) monkey.items.size(), Long::sum);
                for (Iterator<Numb> iterator = monkey.items.iterator(); iterator.hasNext(); ) {
                    Numb item = iterator.next();
                    Numb newVal = monkey.operate(item);
                    int passTo = (int) monkey.testDivisible(newVal);
//                    System.out.println("pasTo = " + passTo);
                    var nextMonkey = monkeys.get(passTo);
                    nextMonkey.items.add(newVal);
                    iterator.remove();
                }
            }
//            System.out.println("round = " + (i + 1) + "; " +  monkeyItemsCount.get(0) + ", " + monkeyItemsCount.get(1) + ", " + monkeyItemsCount.get(2) + ", " + monkeyItemsCount.get(3));
//            System.out.println("end of round");
        }
        var sorted = monkeyItemsCount.values().stream()
                .sorted((o1, o2) -> (int) (o2 - o1))
                .collect(Collectors.toList());
        long valuee = sorted.get(0) * sorted.get(1);
        System.out.println(valuee);
    }

    public static class Numb {
        Numb nested;
        Operator operator;
        long value;


        public Numb(long value) {
            this.value = value;
        }

        private Numb(Numb nested, Operator operator, long value) {
            this.nested = nested;
            this.operator = operator;
            this.value = value;
        }

        private Numb addNumb(long value) {
            return new Numb(this, Operator.SUM, value);
        }

        private Numb multiplyNum(long value) {
            return new Numb(this, Operator.MULTPLY, value);
        }

        private Numb square() {
            return new Numb(this, Operator.SQUARE, -1);
        }

        public long modOf(long divideBy) {
            var val = calculateModOf(divideBy, this);
            return val;
        }

        private long calculateModOf(long divideBy, Numb numb) {
            long value;
            if (numb.nested != null) {
                value = calculateModOf(divideBy, numb.nested);
                long val;
                if (numb.operator == Operator.MULTPLY) {
                    val = numb.value * value;
                } else if (numb.operator == Operator.SUM) {
                    val = numb.value + value;
                } else {
                    val = (long) Math.pow(value, 2);
                }
                return val % divideBy;
            }
            return numb.value % divideBy;
        }
    }

    public enum Operator {
        MULTPLY, SUM, SQUARE;
    }

}


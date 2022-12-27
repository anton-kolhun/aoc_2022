package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day21Part2Stolen {
    public static void main(String[] args) throws IOException {
        final Map<String, Monkey> monkeys = FilesUtilS.readFile("day21.txt")
                .stream()
                .map(String::trim)
                .map(Monkey::new)
                .collect(Collectors.toMap(Monkey::getName,
                        Function.identity()));
        monkeys.forEach((name, monkey) -> monkey.linkMonkeys(monkeys));

/*        // Part 1
        System.out.println(monkeys.get("root").getValue());*/

        // Part 2
        System.out.println(monkeys.get("humn").calculateNeededValue());
    }


    public static class Monkey {
        private final Operation operation;
        private final String leftMonkeyName;
        private final String rightMonkeyName;
        private final String name;

        private Monkey parent;
        private Monkey leftMonkey;
        private Monkey rightMonkey;
        private Long value;

        public Monkey(final String string) {
            name = string.substring(0, 4);

            if (string.length() == 17) {
                operation = Operation.of(string.charAt(11));
                leftMonkeyName = string.substring(6, 10);
                rightMonkeyName = string.substring(13);
            } else {
                operation = null;
                leftMonkeyName = null;
                rightMonkeyName = null;

                value = Long.parseLong(string.substring(6));
            }
        }

        public long getValue() {
            if (value == null) {
                return operation.on(leftMonkey.getValue(), rightMonkey.getValue());
            }
            return value;
        }

        public void linkMonkeys(final Map<String, Monkey> allMonkeys) {
            leftMonkey = allMonkeys.get(leftMonkeyName);
            if (leftMonkey != null) {
                leftMonkey.parent = this;
            }
            rightMonkey = allMonkeys.get(rightMonkeyName);
            if (rightMonkey != null) {
                rightMonkey.parent = this;
            }
        }

        public String getName() {
            return name;
        }

        public long calculateNeededValue() {
            if ("root".equals(parent.name)) {
                return parent.getOtherChild(this).getValue();
            }

            return parent.operation.invert(parent.getOtherChild(this).getValue(),
                    parent.calculateNeededValue(),
                    isLeftChild());
        }

        private boolean isLeftChild() {
            return parent.leftMonkey == this;
        }

        private Monkey getOtherChild(final Monkey monkey) {
            return monkey == leftMonkey ? rightMonkey : leftMonkey;
        }

        public enum Operation {
            ADD,
            SUBTRACT,
            MULTIPLY,
            DIVIDE;

            public static Operation of(final char operation) {
                switch (operation) {
                    case '+':
                        return ADD;
                    case '-':
                        return SUBTRACT;
                    case '*':
                        return MULTIPLY;
                    case '/':
                        return DIVIDE;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            public long on(final long x, final long y) {
                switch (this) {
                    case ADD:
                        return x + y;
                    case SUBTRACT:
                        return x - y;
                    case MULTIPLY:
                        return x * y;
                    case DIVIDE:
                        return x / y;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            public long invert(final long input, final long output, final boolean answerWasLeft) {
                switch (this) {
                    case ADD:
                        return output - input;
                    case MULTIPLY:
                        return output / input;
                    case SUBTRACT:
                        return answerWasLeft ? input + output : input - output;
                    case DIVIDE:
                        return answerWasLeft ? input * output : input / output;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
    }
}
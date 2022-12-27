package aoc;

import lombok.ToString;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21Part2 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day21.txt");;
        Map<String, Formula> nodesToValues = new HashMap<>();
        List<Edge> edges = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                var name = parts[0];
                var formula = new Formula();
                if (parts[1].equals("X")) {
                    formula.left = BigDecimal.ONE;
                } else {
                    var val = Integer.parseInt(parts[1]);
                    formula.right = BigDecimal.valueOf(val);
                }
                nodesToValues.put(name.substring(0, name.length() - 1), formula);
            } else {
                if (parts[2].equals("/")) {
                    var to = parts[1];
                    var from1 = parts[3];
                    var from2 = parts[0];
                    from2 = from2.substring(0, from2.length() - 1);
                    edges.add(new Edge(from1, to, parts[2], 0));
                    edges.add(new Edge(from2, to, parts[2], 1));

                    nodesToValues.putIfAbsent(from1, new Formula());
                    nodesToValues.putIfAbsent(to, new Formula());
                    nodesToValues.putIfAbsent(from2, new Formula());

                } else {
                    var toNode = parts[0];
                    toNode = toNode.substring(0, toNode.length() - 1);
                    var from1 = parts[1];
                    var from2 = parts[3];
                    edges.add(new Edge(from1, toNode, parts[2], 0));
                    edges.add(new Edge(from2, toNode, parts[2], 1));

                    nodesToValues.putIfAbsent(from1, new Formula());
                    nodesToValues.putIfAbsent(toNode, new Formula());
                    nodesToValues.putIfAbsent(from2, new Formula());
                }


            }
        }
        findPath(edges, nodesToValues);
    }

    private static void findPath(List<Edge> edges, Map<String, Formula> nodeToValues) {

        var testEl1 = "pppw";
        var testEl2 = "sjmn";
        var el1 = "ddzt";
        var el2 = "rmtp";
        var f1 = findFormula(nodeToValues, edges, testEl1);
        var f2 = findFormula(nodeToValues, edges, testEl2);

        BigDecimal xPart = f1.left.subtract(f2.left);
        BigDecimal nonXPart = f2.right.subtract(f1.right);
        BigDecimal x = nonXPart.divide(xPart, 100000, RoundingMode.HALF_EVEN);
        System.out.println(x);
    }

    private static Formula findFormula(Map<String, Formula> nodeToValues, List<Edge> edges, String toFind) {

        var fromEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.from));

        var toEdgesInitial = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.to));

        Set<String> starts = new HashSet<>();
        for (String node : nodeToValues.keySet()) {
            if (!toEdgesInitial.containsKey(node)) {
                starts.add(node);
            }
        }
        var toEdges = edges.stream()
                .collect(Collectors.groupingBy(edge -> edge.to));

        var reachable = new HashSet<>(starts);

        while (!reachable.isEmpty()) {
            var it = reachable.iterator();
            String from = it.next();
            it.remove();
            var fromEds = fromEdges.getOrDefault(from, new ArrayList<>());
            for (Edge fromdEd : fromEds) {
                var edgs = toEdges.get(fromdEd.to);
                edgs.remove(new Edge(fromdEd.from, fromdEd.to));
                if (edgs.size() == 0) {
                    reachable.add(fromdEd.to);
                    var edge1 = toEdgesInitial.get(fromdEd.to).get(0);
                    var edge2 = toEdgesInitial.get(fromdEd.to).get(1);
                    Edge op1;
                    Edge op2;
                    if (edge1.index == 0) {
                        op1 = edge1;
                        op2 = edge2;
                    } else {
                        op1 = edge2;
                        op2 = edge1;
                    }
                    var nextFormula = nodeToValues.get(op1.from).calculate(nodeToValues.get(op2.from), edge1.operation);
                    nodeToValues.put(fromdEd.to, nextFormula);
                    if (fromdEd.to.equals(toFind)) {
                        System.out.println("found");
                        return nextFormula;
                    }
                }
            }
        }
        return null;
    }


    @ToString
    private static class Edge {
        private String from;
        private String to;
        private String operation;
        private Integer index;

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public Edge(String from, String to, String operation, Integer index) {
            this.from = from;
            this.to = to;
            this.operation = operation;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            if (!Objects.equals(from, edge.from)) return false;
            return Objects.equals(to, edge.to);
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

    private static class Formula {
        BigDecimal left = BigDecimal.ZERO;
        BigDecimal right = BigDecimal.ZERO;

        Formula calculate(Formula other, String operation) {
            var f = new Formula();
            if (this.left.equals(BigDecimal.ZERO) && other.left.equals(BigDecimal.ZERO)) {
                if (operation.equals("+")) {
                    f.right = right.add(other.right);
                    return f;
                }
                if (operation.equals("-")) {
                    f.right = right.subtract(other.right);
                    return f;
                }
                if (operation.equals("*")) {
                    f.right = right.multiply(other.right);
                    return f;
                }

                if (operation.equals("/")) {
                    f.right = right.divide(other.right, 10000, RoundingMode.HALF_UP);
                    return f;
                }
            }
            var nonNullPart = !this.left.equals(BigDecimal.ZERO) ? this : other;
            var nullPart = this.left.equals(BigDecimal.ZERO) ? this : other;

            if (operation.equals("+")) {
                f.left = nonNullPart.left;
                f.right = nonNullPart.right.add(nullPart.right);
                return f;
            }
            if (operation.equals("-")) {
                f.left = nonNullPart.left;
                f.right = nonNullPart.right.subtract(nullPart.right);
                return f;
            }
            if (operation.equals("*")) {
                f.left = nonNullPart.left.multiply(nullPart.right);
                f.right = nonNullPart.right.multiply(nullPart.right);
                return f;
            }

            if (operation.equals("/")) {
                f.left = nonNullPart.left.divide(nullPart.right, 100000, RoundingMode.HALF_EVEN);
                f.right = nonNullPart.right.divide(nullPart.right, 100000, RoundingMode.HALF_EVEN);
                return f;
            }
            return f;
        }

    }
}


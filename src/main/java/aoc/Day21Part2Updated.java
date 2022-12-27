package aoc;

import lombok.ToString;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21Part2Updated {

    public static void main(String[] args) throws IOException {
        task2();
    }

    public static BigInteger lcm(BigInteger number1, BigInteger number2) {
        // convert string 'a' and 'b' into BigInteger

        // calculate multiplication of two bigintegers
        BigInteger mul = number1.multiply(number2);

        // calculate gcd of two bigintegers
        BigInteger gcd = number1.gcd(number2);

        // calculate lcm using formula: lcm * gcd = x * y
        BigInteger lcm = mul.divide(gcd);
        return lcm;
    }

    private static void task2() throws IOException {
        List<String> lines = FilesUtilS.readFile("day21.txt");
        Map<String, Formula> nodesToValues = new HashMap<>();
        List<Edge> edges = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                var name = parts[0];
                var formula = new Formula();
                if (parts[1].equals("X")) {
                    formula.left = BigInteger.ONE;
                } else {
                    var val = Integer.parseInt(parts[1]);
                    formula.right = BigInteger.valueOf(val);
                }
                nodesToValues.put(name.substring(0, name.length() - 1), formula);
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
        findPath(edges, nodesToValues);
    }

    private static void findPath(List<Edge> edges, Map<String, Formula> nodeToValues) {

        var testEl1 = "pppw";
        var testEl2 = "sjmn";
        var el1 = "ddzt";
        var el2 = "rmtp";
        var f1 = findFormula(nodeToValues, edges, el1);
        var f2 = findFormula(nodeToValues, edges, el2);


        BigInteger rightSide = f1.divideByPart.multiply(f2.right).divide(f2.divideByPart);
        BigInteger nonXPart = rightSide.subtract(f1.right);
        BigInteger x = nonXPart.divide(f1.left);


      /*  BigInteger xPart = f1.left.subtract(f2.left);
        BigInteger nonXPart = f2.right.subtract(f1.right);
        BigInteger x = nonXPart.divide(xPart);*/
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
        BigInteger left = BigInteger.ZERO;
        BigInteger right = BigInteger.ZERO;

        BigInteger divideByPart = BigInteger.ONE;

        Formula calculate(Formula other, String operation) {
            var f = new Formula();
            if (this.left.equals(BigInteger.ZERO) && other.left.equals(BigInteger.ZERO)) {
                if (operation.equals("+")) {
                    var lcm = lcm(this.divideByPart, other.divideByPart);
                    var timesThis = lcm.divide(this.divideByPart);
                    var timesOther = lcm.divide(other.divideByPart);
                    f.right = this.right.multiply(timesThis)
                            .add(other.right.multiply(timesOther));
                    f.divideByPart = lcm;
                    return f;
                }
                if (operation.equals("-")) {
                    var lcm = lcm(this.divideByPart, other.divideByPart);
                    var timesThis = lcm.divide(this.divideByPart);
                    var timesOther = lcm.divide(other.divideByPart);
                    f.right = this.right.multiply(timesThis)
                            .subtract(other.right.multiply(timesOther));
                    f.divideByPart = lcm;
                    return f;
                }
                if (operation.equals("*")) {
                    f.right = right.multiply(other.right);
                    f.divideByPart = this.divideByPart.multiply(other.divideByPart);
                    return f;
                }
                if (operation.equals("/")) {
                    f.right = this.right.multiply(other.divideByPart);
                    f.divideByPart = this.divideByPart.multiply(other.right);
                    return f;
                }
            }
            var nonNullPart = !this.left.equals(BigInteger.ZERO) ? this : other;
            var nullPart = this.left.equals(BigInteger.ZERO) ? this : other;

            if (operation.equals("+")) {
                var lcm = lcm(nonNullPart.divideByPart, nullPart.divideByPart);
                var timesNull = lcm.divide(nullPart.divideByPart);
                var timesNonNull = lcm.divide(nonNullPart.divideByPart);
                f.left = nonNullPart.left.multiply(timesNonNull);
                f.right = nonNullPart.right.multiply(timesNonNull)
                        .add(nullPart.right.multiply(timesNull));
                f.divideByPart = lcm;
                return f;
            }
            if (operation.equals("-")) {
                var lcm = lcm(nonNullPart.divideByPart, nullPart.divideByPart);
                var timesNull = lcm.divide(nullPart.divideByPart);
                var timesNonNull = lcm.divide(nonNullPart.divideByPart);
                f.left = nonNullPart.left.multiply(timesNonNull);
                f.right = nonNullPart.right.multiply(timesNonNull)
                        .subtract(nullPart.right.multiply(timesNull));
                if (!nonNullPart.equals(this)) {
                    f.right = f.right.multiply(BigInteger.valueOf(-1));
                    f.left = f.left.multiply(BigInteger.valueOf(-1));
                }
                f.divideByPart = lcm;
                return f;
            }
            if (operation.equals("*")) {
                f.left = nonNullPart.left.multiply(nullPart.right);
                f.right = nonNullPart.right.multiply(nullPart.right);
                f.divideByPart = nonNullPart.divideByPart.multiply(nullPart.divideByPart);
                return f;
            }

            if (operation.equals("/")) {
                if (nonNullPart.equals(this)) {
                    f.left = nonNullPart.left.multiply(nullPart.divideByPart);
                    f.right = nonNullPart.right.multiply(nullPart.divideByPart);
                    f.divideByPart = nonNullPart.divideByPart.multiply(nullPart.right);
                    return f;
                }
                f.left = nullPart.divideByPart.multiply(nullPart.divideByPart);
                f.right = nonNullPart.right.multiply(nullPart.divideByPart);
                f.divideByPart = nonNullPart.divideByPart.multiply(nullPart.right);
                if (!nonNullPart.equals(this)) {

                }
                return f;
            }
            return f;
        }

    }
}

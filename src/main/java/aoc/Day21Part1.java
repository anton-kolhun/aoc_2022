package aoc;

import lombok.ToString;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21Part1 {

    public static void main(String[] args) throws IOException {
        task1();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day21.txt");
        Map<String, BigDecimal> nodesToValues = new HashMap<>();
        List<Edge> edges = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                var name = parts[0];
                var val = new BigDecimal(parts[1]);
                nodesToValues.put(name.substring(0, name.length() - 1), val);
            } else {
                var toNode = parts[0];
                toNode = toNode.substring(0, toNode.length() - 1);
                var from1 = parts[1];
                var from2 = parts[3];
                edges.add(new Edge(from1, toNode, parts[2], 0));
                edges.add(new Edge(from2, toNode, parts[2], 1));

                nodesToValues.putIfAbsent(from1, BigDecimal.valueOf(Integer.MIN_VALUE));
                nodesToValues.putIfAbsent(toNode, BigDecimal.valueOf(Integer.MIN_VALUE));
                nodesToValues.putIfAbsent(from2, BigDecimal.valueOf(Integer.MIN_VALUE));
            }
        }
        findPath(edges, nodesToValues);
    }

    private static void findPath(List<Edge> edges, Map<String, BigDecimal> nodeToValues) {
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
        var toFind = "root";
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
                    BigDecimal total = BigDecimal.ZERO;
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
                    if (edge1.operation.equals("+")) {
                        total = nodeToValues.get(op1.from).add(nodeToValues.get(op2.from));
                    } else if (edge1.operation.equals("-")) {
                        total = nodeToValues.get(op1.from).subtract(nodeToValues.get(op2.from));
                    } else if (edge1.operation.equals("*")) {
                        var v1 = nodeToValues.get(op1.from);
                        var v2 =  nodeToValues.get(op2.from);
                        total = v1.multiply(v2);
                    } else if (edge1.operation.equals("/")) {
                        total = nodeToValues.get(op1.from).divide(nodeToValues.get(op2.from));
                    } else {
                        System.out.println("wtf");
                    }

                    nodeToValues.put(fromdEd.to, total);
                    if (fromdEd.to.equals(toFind)) {
                        System.out.println(nodeToValues.get(op1.from));
                        System.out.println(nodeToValues.get(op2.from));
                        System.out.println("found");
                        System.out.println(total);
                    }
                }
            }
        }

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
}

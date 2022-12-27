package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day20Stolen {


    public static void main(String[] args) throws IOException {
        long res = solveTask2();
        System.out.println(res);
    }


    private static final long DECRYPTION_KEY = 811_589_153L;

    static long solveTask1() throws IOException {
        List<String> lines = FilesUtilS.readFile("day20.txt");
        var numbers = lines.stream().map(Long::parseLong)
                .collect(Collectors.toList());
        File file = new File(numbers);
        file.mix();
        return file.getGroveCoordinates();
    }

    static long solveTask2() throws IOException {
        List<String> lines = FilesUtilS.readFile("day20.txt");
        var numbers = lines.stream().map(Long::parseLong)
                .collect(Collectors.toList());

        numbers.replaceAll(value -> value * DECRYPTION_KEY);

        File file = new File(numbers);
        for (int i = 0; i < 10; i++) {
            file.mix();
        }
        return file.getGroveCoordinates();
    }

    private static class File {

        private final List<Node> nodesInOriginalOrder = new ArrayList<>();
        private final int size;
        private final Node zeroNode;

        File(List<Long> numbers) {
            size = numbers.size();

            // Store all numbers in a doubly-linked list.
            // And remember the original order of the nodes and the zero node.
            Node tempZeroNode = null;
            for (int i = 0; i < size; i++) {
                Long number = numbers.get(i);
                Node node = new Node(number);

                nodesInOriginalOrder.add(node);

                if (number == 0) {
                    tempZeroNode = node;
                }

                if (i > 0) {
                    Node previousNode = nodesInOriginalOrder.get(i - 1);
                    previousNode.setNext(node);
                    node.setPrevious(previousNode);
                }
            }

            this.zeroNode = tempZeroNode;

            // Make the list circular
            Node firstNode = nodesInOriginalOrder.get(0);
            Node lastNode = nodesInOriginalOrder.get(size - 1);
            firstNode.setPrevious(lastNode);
            lastNode.setNext(firstNode);
        }

        void mix() {
            for (int i = 0; i < size; i++) {
                Node node = nodesInOriginalOrder.get(i);
                move(node);
            }
        }

        private void move(Node node) {
            long distance = node.value() % (size - 1);
            if (distance == 0) {
                return;
            }

            Node previous = node.previous();
            Node next = node.next();

            // Remove node from current position
            previous.setNext(next);
            next.setPrevious(previous);

            if (distance < 0) {
                for (long i = 0; i > distance; i--) {
                    next = previous;
                    previous = previous.previous();
                }
            } else {
                for (long i = 0; i < distance; i++) {
                    previous = next;
                    next = next.next();
                }
            }

            // Insert node at new position
            previous.setNext(node);
            node.setPrevious(previous);
            node.setNext(next);
            next.setPrevious(node);
        }

        long getGroveCoordinates() {
            Node node = zeroNode;

            long sum = 0;
            for (int i = 0; i < 3; i++) {
                node = skipNodes(node, 1000);
                sum += node.value();
            }

            return sum;
        }

        private Node skipNodes(Node node, int numberOfNodes) {
            int remainder = numberOfNodes % size;
            for (int i = 0; i < remainder; i++) {
                node = node.next();
            }
            return node;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Node node = nodesInOriginalOrder.get(0);
            for (int i = 0; i < size; i++) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(node.value());
                node = node.next();
            }
            return result.toString();
        }
    }

    static class Node {

        private final long value;

        private Node previous;
        private Node next;

        Node(long value) {
            this.value = value;
        }

        long value() {
            return value;
        }

        Node previous() {
            return previous;
        }

        void setPrevious(Node previous) {
            this.previous = previous;
        }

        Node next() {
            return next;
        }

        void setNext(Node next) {
            this.next = next;
        }
    }

    private static class ListParser {

        static List<Long> parse(String input) {
            return input.lines().map(Long::parseLong).collect(Collectors.toList());
        }
    }
}

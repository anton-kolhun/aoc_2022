package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day5 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day5.txt");
//        List<List<String>> stacks =  loadStacks(lines);
        List<List<String>> temp = List.of(
                List.of("P", "L", "M", "N", "W", "V", "B", "H"),
                List.of("H", "Q", "M"),
                List.of("L", "M", "Q", "F", "G", "B", "D", "N"),
                List.of("G", "W", "M", "Q", "F", "T", "Z"),
                List.of("P", "H", "T", "M"),
                List.of("T", "G", "H", "D", "J", "M", "B", "C"),
                List.of("R", "V", "F", "B", "N", "M"),
                List.of("S", "G", "R", "M", "H", "L", "P"),
                List.of("N", "C", "B", "D", "P")
        );
        List<List<String>> stacks = new ArrayList<>();
        for (List<String> stack : temp) {
            stacks.add(new ArrayList<>(stack));
        }

        for (String line : lines) {
            String[] actions = line.split(" ");
            int quantity = Integer.parseInt(actions[1]);
            int from = Integer.parseInt(actions[3]);
            int to = Integer.parseInt(actions[5]);
            for (int i = 0; i < quantity; i++) {
                String el = stacks.get(from - 1).remove(0);
                stacks.get(to - 1).add(0, el);
            }
            //printStack(stacks);
        }
        System.out.println();

        for (List<String> stack : stacks) {
            if (!stack.isEmpty()) {
                System.out.print(stack.get(0));
            }
        }
    }

    private static void printStack(List<List<String>> stacks) {
        int max = 0;
        for (List<String> stack : stacks) {
            if (stack.size() > max) {
                max = stack.size();
            }
        }
        int cursor = 0;
        while (cursor < max) {
            for (List<String> stack : stacks) {
                if (cursor < stack.size()) {
                    String symb = stack.get(cursor);
                    System.out.print("[" + symb + "] ");
                } else {
                    System.out.print("    ");
                }
            }
            System.out.println();
            cursor++;
        }
    }


    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day5.txt");
//        List<List<String>> stacks =  loadStacks(lines);
        List<List<String>> temp = List.of(
                List.of("P", "L", "M", "N", "W", "V", "B", "H"),
                List.of("H", "Q", "M"),
                List.of("L", "M", "Q", "F", "G", "B", "D", "N"),
                List.of("G", "W", "M", "Q", "F", "T", "Z"),
                List.of("P", "H", "T", "M"),
                List.of("T", "G", "H", "D", "J", "M", "B", "C"),
                List.of("R", "V", "F", "B", "N", "M"),
                List.of("S", "G", "R", "M", "H", "L", "P"),
                List.of("N", "C", "B", "D", "P")
        );
        List<List<String>> stacks = new ArrayList<>();
        for (List<String> stack : temp) {
            stacks.add(new ArrayList<>(stack));
        }

        for (String line : lines) {
            String[] actions = line.split(" ");
            int quantity = Integer.parseInt(actions[1]);
            int from = Integer.parseInt(actions[3]);
            int to = Integer.parseInt(actions[5]);
            List<String> toMove = stacks.get(from - 1).subList(0, quantity);
            stacks.get(to - 1).addAll(0, toMove);
            for (int i = 0; i < quantity; i++) {
                stacks.get(from - 1).remove(0);
            }
            //printStack(stacks);
        }
        System.out.println();

        for (List<String> stack : stacks) {
            if (!stack.isEmpty()) {
                System.out.print(stack.get(0));
            }
        }
    }

 /*   private static List<List<String>> loadStacks(List<String> lines) {
        String line = lines.get(0);
        int counter = 1;
        List<List<String>> rows = new ArrayList<>();
        while (!line.equals("")) {
            String[] symbols = line.split(" ");
            List<String> row  = new ArrayList<>();
            for (String symbol : symbols) {
                row.add(symbol.substring(1,1));
            }
            rows.add(row);
            counter++;
            line = lines.get(counter);
        }
        return rows;
    }*/
}

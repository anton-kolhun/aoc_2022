package aoc;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day20Part2 {

    public static void main(String[] args) throws IOException {
        List<Integer> res = task2();
        testIt(res);
    }

    public static void testIt(List<Integer> res) {
        int initialIndex = -1;
        for (int index = 0; index < res.size(); index++) {
            Integer el = res.get(index);
            if (el == 0) {
                initialIndex = index;
                break;
            }
        }

        int sum = 0;
        for (int i = 0; i < 3001; i++) {
            initialIndex++;
            if (((i + 1) % 1000) == 0) {
                var ind = initialIndex % res.size();
                var el = res.get(ind);
                sum += el;
                System.out.println(el);
            }
        }
        System.out.println("_______");
        System.out.println(sum);
    }

    private static List<Integer> task2() throws IOException {
        List<String> lines = FilesUtilS.readFile("day20.txt");
        Map<Element, Integer> elementToCurrentPosition = new HashMap<>();
        LinkedList<Element> initial = new LinkedList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int val = Integer.parseInt(line);
            initial.add(new Element(i, Integer.parseInt(line)));
            elementToCurrentPosition.put(new Element(i, val), i);
        }

        int size = initial.size();
        LinkedList<Element> current = initial;
        int multiplier = 811589153;
        for (int mixCount = 0; mixCount < 10; mixCount++) {
            //current = initial;
            for (int cycle = 0; cycle < size; cycle++) {
                int initialIndex = cycle % elementToCurrentPosition.size();
                Element el = initial.get(initialIndex);
                if (el.value == 0) {
                    continue;
                }
                int currentPosition = elementToCurrentPosition.get(el);
                LinkedList<Element> next = new LinkedList<>(current);

//            var shift = (el.value % (size - 1));

                var shift = (el.value % (size - 1)) * (multiplier % (size - 1));
                shift = (shift % (size - 1));


                var nextPosition = currentPosition + shift;
                if (nextPosition >= size) {
                    nextPosition = (nextPosition % size) + 1;
                }

                if (nextPosition <= 0) {
                    nextPosition = size - 1 + nextPosition;
                }
                next.remove(currentPosition);
                next.add(nextPosition, el);
                current = next;
                Map<Element, Integer> nextMap = new HashMap<>();
                for (int i = 0; i < current.size(); i++) {
                    Element element = current.get(i);
                    nextMap.put(element, i);
                }
                elementToCurrentPosition = nextMap;
              /*  System.out.println("Cycle = " + mixCount);
                System.out.println("--- Moved " + el.value);
                for (Element element : current) {
                    System.out.print(element.value + ", ");
                }
                System.out.println();*/
            }
            //System.out.println("---end of cycle " +(mixCount + 1) + "-------");
        }

        List<Integer> res = current.stream()
                .map(element -> element.value)
                .collect(Collectors.toList());

        System.out.println(res);
        return res;
    }


    @ToString
    private static class Element {
        private int initialIndex;
        private int value;

        public Element(int index, int value) {
            this.initialIndex = index;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Element element = (Element) o;

            if (initialIndex != element.initialIndex) return false;
            return value == element.value;
        }

        @Override
        public int hashCode() {
            int result = initialIndex;
            result = 31 * result + value;
            return result;
        }
    }


}

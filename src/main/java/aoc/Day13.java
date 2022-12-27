package aoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day13 {

    public static void main(String[] args) throws IOException {
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day13.txt");
        int sum = 0;
        int pairNumber = 1;
        for (int i = 0; i < lines.size(); ) {
            if (lines.get(i).isEmpty()) {
                i++;
                continue;
            }
            Item item1 = readItem(lines.get(i));
            Item item2 = readItem(lines.get(i + 1));
            boolean isValid = isValidPair(item1, item2);
            if (isValid) {
                sum += pairNumber;
                System.out.println(pairNumber);
            }
            i = i + 2;
            pairNumber++;
        }
        System.out.println(sum);
    }

    private static boolean isValidPair(Item item1, Item item2) {
        int res = checkPair(item1, item2);
        return res <= 0;
    }

    private static int compareItems(Item item1, Item item2) {
        int res = checkPair(item1, item2);
        return res;
    }

    private static int checkPair(Item item1, Item item2) {
        if (item1.value == null && item1.nested.isEmpty() && item2.value == null && item2.nested.isEmpty()) {
            return 0;
        }
        if (item1.value == null && item1.nested.isEmpty()) {
            return -1;
        }
        if (item2.value == null && item2.nested.isEmpty()) {
            return 1;
        }
        if (item1.nested.isEmpty() && item2.nested.isEmpty()) {
            if (Objects.equals(item1.value, item2.value)) {
                return 0;
            }
            return item1.value - item2.value;
        } else if (!item1.nested.isEmpty() && !item2.nested.isEmpty()) {
            int cursor = 0;
            while (cursor < item1.nested.size() || cursor < item2.nested.size()) {
                if (cursor >= item2.nested.size()) {
                    return 1;
                }
                if (cursor >= item1.nested.size()) {
                    return -1;
                }
                int res = checkPair(item1.nested.get(cursor), item2.nested.get(cursor));
                if (res != 0) {
                    return res;
                }
                cursor++;
            }
        } else if (!item1.nested.isEmpty()) {
            int res = checkPair(item1.nested.get(0), item2);
            if (res == 0 && item1.nested.size() > 1) {
                return 1;
            }
            return res;
        } else {
            int res = checkPair(item1, item2.nested.get(0));
            if (res == 0 && item2.nested.size() > 1) {
                return -1;
            }
            return res;
        }
        return 0;
    }

    private static Item readItem(String line) {

        char[] charArray = line.toCharArray();
        int cursor = 0;
        Item root = new Item();
        readIt(cursor, charArray, root);
        return root.nested.get(0);
    }

    private static void readIt(int cursor, char[] charArray, Item item) {
        if (cursor == charArray.length) {
            return;
        }
        char c = charArray[cursor];
        cursor++;
        if (c == '[') {
            var kid = new Item();
            item.nested.add(kid);
            kid.parent = item;
            readIt(cursor, charArray, kid);
        } else if (c == ']') {
            readIt(cursor, charArray, item.parent);
        } else {
            if (Character.isDigit(c)) {
                var kid = new Item();
                item.nested.add(kid);
                kid.parent = item;
                if (Character.isDigit(charArray[cursor])) {
                    String val = new String(new char[]{c, charArray[cursor]});
                    kid.value = Integer.parseInt(val);
                } else {
                    kid.value = Character.getNumericValue(c);
                }
            }
            readIt(cursor, charArray, item);
        }
    }


    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day13.txt");
        List<Item> items = new ArrayList<>();
        Map<Integer, Integer> itemIndexToLine = new HashMap<>();
        int i;
        for (i = 0; i < lines.size() - 2; i++) {
            if (lines.get(i).isEmpty()) {
                continue;
            }
            Item item = readItem(lines.get(i));
            items.add(item);
        }
        Item item2 = readItem(lines.get(i));
        items.add(item2);
        Item item6 = readItem(lines.get(i + 1));
        items.add(item6);

        List<ItemInfo> infos = new ArrayList<>();
        for (int j = 0; j < items.size(); j++) {
            Item item = items.get(j);
            infos.add(new ItemInfo(item, j));
        }


        var sorted = infos.stream()
                .sorted(new ItemComparator())
                .collect(Collectors.toList());

        for (int j = 0; j < sorted.size(); j++) {
            System.out.println(lines.get(sorted.get(j).index));
        }


        int index2 = 0;
        int index6 = 0;

        for (int j = 0; j < sorted.size(); j++) {
            Item item = sorted.get(j).item;
            if (item == item2) {
                index2 = j + 1;
            }
            if (item == item6) {
                index6 = j + 1;
            }
        }

        System.out.println(index6 * index2);
    }


    private static class Item {
        private List<Item> nested = new ArrayList<>();
        private Integer value;
        private Item parent;
    }

    private static class ItemInfo {
        private Item item;
        private int index;

        public ItemInfo(Item item, int index) {
            this.item = item;
            this.index = index;
        }
    }


    private static class ItemComparator implements Comparator<ItemInfo> {

        @Override
        public int compare(ItemInfo o1, ItemInfo o2) {
            return compareItems(o1.item, o2.item);
        }
    }
}

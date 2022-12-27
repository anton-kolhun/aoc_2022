package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day7 {


    public static void main(String[] args) throws IOException {
        task1();
        task2();
    }

    private static void task1() {
        List<String> lines = FilesUtilS.readFile("day7.txt");
        Node tree = buildTree(lines);


        List<FolderInfo> foldersInfo = buildFolderInfo(tree);
        long res = foldersInfo.stream()
                .map(folderInfo -> folderInfo.size)
                .filter(val -> val <= 100000)
                .mapToLong(value -> value)
                .sum();

        System.out.println(res);
    }


    private static void task2() {
        List<String> lines = FilesUtilS.readFile("day7.txt");
        Node tree = buildTree(lines);
        List<FolderInfo> foldersInfo = buildFolderInfo(tree);

        FolderInfo root = null;
        for (FolderInfo folderInfo : foldersInfo) {
            if (folderInfo.name.equals("/")) {
                root = folderInfo;
                break;
            }
        }
        long unUsed = 70000000 - root.size;
        long toDelete = 30000000 - unUsed;
        System.out.println("toDelete " + toDelete);

        foldersInfo.sort((o1, o2) -> (int) (o1.size - o2.size));
        for (FolderInfo folderInfo : foldersInfo) {
            if (folderInfo.size > toDelete) {
                System.out.println("this folder sohuld be deleted: " + folderInfo.name + " " + folderInfo.size);
                return;
            }
        }
    }

    private static int fillDirectorySizes(Node cursor) {
        int total = 0;
        for (Node kid : cursor.kids.values()) {
            int size = fillDirectorySizes(kid);
            total = total + size;
        }
        cursor.size = cursor.size + total;
        return cursor.size;
    }

    private static List<FolderInfo> buildFolderInfo(Node cursor) {
        List<FolderInfo> allSubFolders = new ArrayList<>();
        for (Node node : cursor.kids.values()) {
            if (!node.isFile) {
                List<FolderInfo> folderSizes = buildFolderInfo(node);
                allSubFolders.addAll(folderSizes);
            }
        }
        allSubFolders.add(new FolderInfo(cursor.name, cursor.size));
        return allSubFolders;
    }

    private static Node buildTree(List<String> lines) {
        Node head = new Node();
        head.name = "/";
        var cursor = head;
        boolean isListing = false;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("$ cd")) {
                isListing = false;
                String[] navigation = line.split(" ");
                String path = navigation[2];
                if (path.equals("..")) {
                    cursor = cursor.parent;
                } else {
                    cursor = cursor.kids.get(path);
                }
                continue;
            }
            if (line.equals("$ ls")) {
                isListing = true;
                continue;
            }
            if (isListing) {
                String[] elements = line.split(" ");
                var kid = new Node();
                kid.name = elements[1];
                kid.parent = cursor;
                if (elements[0].equals("dir")) {
                    kid.isFile = false;
                } else {
                    kid.isFile = true;
                    kid.size = Integer.parseInt(elements[0]);
                }
                cursor.kids.put(kid.name, kid);
            }
        }
        fillDirectorySizes(head);
        return head;
    }


    private static class Node {
        Map<String, Node> kids = new HashMap<>();
        Node parent = null;
        String name = "";
        int size = 0;
        boolean isFile = true;
    }

    private static class FolderInfo {
        String name;
        long size = 0;

        public FolderInfo(String name, long size) {
            this.name = name;
            this.size = size;
        }
    }
}

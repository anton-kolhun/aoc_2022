package aoc;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FilesUtilS {

    @SneakyThrows
    public static List<String> readFile(String filename) {
        return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(filename).toURI()));
    }
}

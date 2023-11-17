package fr.aoc.session2022;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day7 {

    private final List<File> allFiles = new ArrayList<>(Collections.singletonList(File.builder().isFolder(true).id(0).name("/").files(new ArrayList<>()).idParentFile(-1).build()));
    private int idSeq = 1;

    public static void main(String[] args) throws IOException {
        Day7 day7 = new Day7();
        List<String> commands = day7.readInput("src/main/resources/2022/day7/input.txt");

        day7.createAllFiles(commands);

        int answer1 = day7.allFiles.stream()
                .filter(File::isFolder)
                .mapToInt(File::getTotalSize)
                .filter(size -> size <= 100000)
                .sum();
        int answer2 = day7.allFiles.stream()
                .filter(File::isFolder)
                .mapToInt(File::getTotalSize)
                .filter(size -> size >= 30000000 - (70000000 - day7.allFiles.get(0).getTotalSize()))
                .min().getAsInt();

        log.info("Answer 1 : {}", answer1);
        log.info("Answer 1 : {}", answer2);
    }

    private List<String> readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE)).toList();
        }
    }

    public void createAllFiles(List<String> commands) throws FileNotFoundException {
        AtomicReference<File> currentFolder = new AtomicReference<>(allFiles.get(0));
        commands.forEach(command -> {
            if (command.startsWith("$")) {
                if (command.startsWith("$ cd"))
                    currentFolder.set(moveFolder(command.split(" ")[2], currentFolder.get()));
            } else {
                File file;
                boolean isDir = command.startsWith("dir");
                file = File.builder()
                        .isFolder(isDir)
                        .size(isDir ? 0 : Integer.parseInt(command.split(" ")[0]))
                        .name(command.split(" ")[1])
                        .id(idSeq)
                        .files(isDir ? new ArrayList<>() : null)
                        .idParentFile(currentFolder.get().getId())
                        .build();
                idSeq++;
                currentFolder.get().getFiles().add(file);
                allFiles.add(file);
            }
        });
    }

    public File moveFolder(String destination, File currentFolder) {
        if ("..".equals(destination)) {
            return allFiles.stream()
                    .filter(file -> file.getId() == currentFolder.getIdParentFile())
                    .findFirst().orElse(allFiles.get(0));
        } else if ("/".equals(destination)) {
            return allFiles.get(0);
        } else {
            return currentFolder.getFiles().stream()
                    .filter(file -> file.getName().equals(destination)).findFirst().get();
        }
    }

    @Data
    @Builder
    static class File {
        private boolean isFolder;
        private int id;
        private int idParentFile;
        @Builder.Default
        private int size = 0;
        private List<File> files;
        private String name;

        public int getTotalSize() {
            if (!isFolder) {
                return size;
            } else {
                return files.stream()
                        .mapToInt(File::getTotalSize)
                        .sum();
            }
        }
    }

}

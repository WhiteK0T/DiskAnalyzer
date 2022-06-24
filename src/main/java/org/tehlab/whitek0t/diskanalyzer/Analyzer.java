package org.tehlab.whitek0t.diskanalyzer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {

    private final Map<String, Long> sizes = new HashMap<>();

    public Map<String, Long> calculateDirectorySize(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    long size = attrs.size();
                    updateDirSize(file, size);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
            return sizes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateDirSize(Path path, Long size) {
        String key = path.toString();
        sizes.merge(key, size, Long::sum);
        Path parent = path.getParent();
        if (parent != null) {
            updateDirSize(parent, size);
        }
    }
}

package com.github.jesusmrs05.client.markers;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class MarkersDbCache {

    private static final Path CACHE_DIRECTORY =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("mco-goto");

    private static final Path CACHE_PATH =
            CACHE_DIRECTORY.resolve("markersDB.js");

    private MarkersDbCache() {
    }

    public static Path getPath() {
        return CACHE_PATH;
    }

    public static String getRelativePath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .relativize(CACHE_PATH)
                .toString()
                .replace('\\', '/');
    }

    public static boolean exists() {
        return Files.isRegularFile(CACHE_PATH);
    }

    public static String read() throws IOException {
        return Files.readString(CACHE_PATH);
    }

    public static void save(String source) throws IOException {
        Files.createDirectories(CACHE_DIRECTORY);

        Path temporaryPath =
                CACHE_DIRECTORY.resolve(
                        "markersDB.js.tmp"
                );

        Files.writeString(
                temporaryPath,
                source
        );

        try {
            Files.move(
                    temporaryPath,
                    CACHE_PATH,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException atomicMoveException) {
            Files.move(
                    temporaryPath,
                    CACHE_PATH,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}
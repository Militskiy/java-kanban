package managers.util;

import managers.exceptions.LoadStateException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static managers.util.Constants.*;

 // Утилитарный класс для загрузки данных из CSV
public final class StateLoader {

    private StateLoader() {
    }

    private static int cutoffIndex;
    private static String[] loadedData;
    public static boolean isDataLoaded = false;

    private static void loadState(Path file) {
        try {
            loadedData = Files.readString(file, StandardCharsets.UTF_8).split(NEXT_LINE);
            for (int i = 0; i < loadedData.length; i++) {
                if (loadedData[i].isBlank()) {
                    cutoffIndex = i;
                }
            }
        } catch (IOException e) {
            throw new LoadStateException(e.getMessage());
        }
    }
    public static List<String[]> loadTaskState(Path file) {
        if (!isDataLoaded) {
            loadState(file);
            isDataLoaded = true;
        }
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < cutoffIndex; i++) {
            result.add(loadedData[i].split(DELIMITER));
        }
        result.remove(0);
        return result;
    }
    public static List<String[]> loadHistoryState(Path file) {
        if (!isDataLoaded) {
            loadState(file);
            isDataLoaded = true;
        }
        List<String[]> result = new ArrayList<>();
        for (int i = cutoffIndex + 1; i < loadedData.length; i++) {
            result.add(loadedData[i].split(DELIMITER));
        }
        return result;
    }
}

package managers.util;

import managers.exceptions.LoadStateException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static managers.util.Constants.*;

// Утилитарный класс для загрузки данных из CSV
public final class StateLoader {

    private StateLoader() {
    }

    private static int cutoffIndex;
    private static List<String> loadedData;

    private static void loadState(Path file) {
        try {
            loadedData = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < loadedData.size(); i++) {
                if (loadedData.get(i).isBlank() || loadedData.get(i).equals(",,,,,,,")) {
                    cutoffIndex = i;
                }
                if (cutoffIndex == 0) {
                    cutoffIndex = loadedData.size();
                }
            }
        } catch (IOException e) {
            throw new LoadStateException(e.getMessage() + " file not found or not accessible");
        }
    }

    public static List<String[]> loadTaskState(Path file) {
        loadState(file);
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < cutoffIndex; i++) {
            result.add(loadedData.get(i).split(DELIMITER));
        }
        result.remove(0);
        return result;
    }

    public static List<String[]> loadHistoryState(Path file) {
        List<String[]> result = new ArrayList<>();
        for (int i = cutoffIndex + 1; i < loadedData.size(); i++) {
            result.add(loadedData.get(i).split(DELIMITER));
        }
        return result;
    }
}

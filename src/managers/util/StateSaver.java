package managers.util;

import managers.exceptions.ManagerSaveException;
import tasks.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static managers.util.Constants.*;

// Утилитарный класс сохранения данных в CSV
public final class StateSaver {

    private StateSaver() {
    }

    public static void saveState(List<Task> list, List<Task> history) throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter( new FileWriter(DEFAULT_FILE_PATH, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic" + NEXT_LINE);
            for (Task task : list) {
                writer.write(TaskToCSVConverter.convertTaskToCSV(task) + NEXT_LINE);
            }
            writer.append(NEXT_LINE);
            for (int i = 0; i < history.size(); i++) {
                writer.append(TaskToCSVConverter.convertTaskToCSV(history.get(i)));
                if (i < history.size() - 1) {
                    writer.append(NEXT_LINE);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }
}
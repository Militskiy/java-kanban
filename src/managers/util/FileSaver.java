package managers.util;

import managers.exceptions.ManagerSaveException;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static managers.util.Constants.*;

// Утилитарный класс сохранения данных в CSV
public final class FileSaver {

    private FileSaver() {
    }

    public static void saveState(List<Task> list, List<Task> history) throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(FILEPATH, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic" + NEXT_LINE);
            for (Task task : list) {
                fileWriter.write(TaskToCSVConverter.convertTaskToCSV(task) + NEXT_LINE);
            }
            fileWriter.append(NEXT_LINE);
            for (int i = 0; i < history.size(); i++) {
                fileWriter.append(TaskToCSVConverter.convertTaskToCSV(history.get(i)));
                if (i < history.size() - 1) {
                    fileWriter.append(NEXT_LINE);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }
}

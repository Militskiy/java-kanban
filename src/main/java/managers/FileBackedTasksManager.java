package managers;

import managers.util.StateSaver;
import managers.util.StateLoader;
import tasks.*;
import tasks.util.Status;
import tasks.util.TaskType;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static managers.util.Constants.DEFAULT_FILE_PATH;
import static tasks.util.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    // main убил тк тестирование теперь идет в Тестах.

    // Метод загрузки из файла
    public static FileBackedTasksManager loadFromFile(Path file) {
        final FileBackedTasksManager fileBackedTasksManager = (FileBackedTasksManager) Managers.getDefault();
        for (int i = 0; i < StateLoader.loadTaskState(file).size(); i++) {
            restoreTaskFromString(StateLoader.loadTaskState(file).get(i), fileBackedTasksManager);
        }
        for (int i = StateLoader.loadHistoryState(file).size() - 1; i > -1; i--) {
            restoreHistoryFromString(StateLoader.loadHistoryState(file).get(i), fileBackedTasksManager);
        }
        return fileBackedTasksManager;
    }

    @Override
    public String addSubTask(Subtask subtask) {
        String id = super.addSubTask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubTask(String subtaskID) {
        super.deleteSubTask(subtaskID);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public String addTask(Task task) {
        String id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(String taskID) {
        super.deleteTask(taskID);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public String addEpic(Epic epic) {
        String id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(String id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(String id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(String id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(String id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    // Метод загрузки задач из строки
    private static void restoreTaskFromString(String[] stringArray, FileBackedTasksManager fileBackedTasksManager) {
        switch (TaskType.valueOf(stringArray[1])) {
            case TASK:
                Task task = new Task(stringArray[0],
                        TASK,
                        stringArray[2],
                        stringArray[4],
                        Status.valueOf(stringArray[3]),
                        returnDateOrNull(stringArray[6]),
                        Long.parseLong(stringArray[7]));

                fileBackedTasksManager.taskList.put(stringArray[0], task);
                fileBackedTasksManager.updateSortedByStartDateList(task);
                break;
            case EPIC:
                Epic epic = new Epic(stringArray[0],
                        EPIC,
                        stringArray[2],
                        stringArray[4],
                        Status.valueOf(stringArray[3]),
                        returnDateOrNull(stringArray[6]),
                        Long.parseLong(stringArray[7]),
                        calculateEndDate(returnDateOrNull(stringArray[6]), Long.parseLong(stringArray[7])));

                fileBackedTasksManager.epicList.put(stringArray[0], epic);
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(stringArray[0],
                        SUBTASK,
                        stringArray[2],
                        stringArray[4],
                        Status.valueOf(stringArray[3]),
                        fileBackedTasksManager.epicList.get(stringArray[5]),
                        returnDateOrNull(stringArray[6]),
                        Long.parseLong(stringArray[7]));

                fileBackedTasksManager.subtaskList.put(subtask.getId(), subtask);
                fileBackedTasksManager.epicList.get(subtask.getEpic().getId()).addSubtask(subtask);
                fileBackedTasksManager.updateEpicDates(subtask);
                fileBackedTasksManager.updateSortedByStartDateList(subtask);
                break;
        }
    }

    private static LocalDateTime calculateEndDate(LocalDateTime date, long duration) {
        if (date == null) {
            return null;
        }
        return date.plusMinutes(duration);
    }

    // Метод загрузки истории из строки
    private static void restoreHistoryFromString(String[] stringArray, FileBackedTasksManager fileBackedTasksManager) {
        switch (TaskType.valueOf(stringArray[1])) {
            case TASK:
                fileBackedTasksManager.getTaskById(stringArray[0]);
                break;
            case EPIC:
                fileBackedTasksManager.getEpicById(stringArray[0]);
                break;
            case SUBTASK:
                fileBackedTasksManager.getSubtaskById(stringArray[0]);
                break;
        }
    }

    private static LocalDateTime returnDateOrNull(String string) {
        if (string.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(string);
    }

    // Метод сохранения состояния в CSV
    private void save() {
        StateSaver.saveState(listEveryTaskAndEpicAndSubtask(), this.historyManager.getHistory(), DEFAULT_FILE_PATH);
    }
}

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

    public FileBackedTasksManager(String path) {
        load(path);
        isDataLoaded = true;
    }

    public FileBackedTasksManager() {
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

    // Метод загрузки из файла
    protected void load(String path) {
        Path file = Path.of(path);
        final FileBackedTasksManager fileBackedTasksManager = this;
        for (int i = 0; i < StateLoader.loadTaskState(file).size(); i++) {
            restoreTaskFromString(StateLoader.loadTaskState(file).get(i), fileBackedTasksManager);
        }
        for (int i = StateLoader.loadHistoryState(file).size() - 1; i > -1; i--) {
            restoreHistoryFromString(StateLoader.loadHistoryState(file).get(i), fileBackedTasksManager);
        }
    }

    // Метод сохранения состояния в CSV
    protected void save() {
        StateSaver.saveState(listEveryTaskAndEpicAndSubtask(), this.historyManager.getHistory(), DEFAULT_FILE_PATH);
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
                        fileBackedTasksManager.returnDateOrNull(stringArray[6]),
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
                        fileBackedTasksManager.returnDateOrNull(stringArray[6]),
                        Long.parseLong(stringArray[7]),
                        fileBackedTasksManager.calculateEndDate(fileBackedTasksManager.returnDateOrNull(stringArray[6]),
                                Long.parseLong(stringArray[7])));

                fileBackedTasksManager.epicList.put(stringArray[0], epic);
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(stringArray[0],
                        SUBTASK,
                        stringArray[2],
                        stringArray[4],
                        Status.valueOf(stringArray[3]),
                        stringArray[5],
                        fileBackedTasksManager.returnDateOrNull(stringArray[6]),
                        Long.parseLong(stringArray[7]));

                fileBackedTasksManager.subtaskList.put(subtask.getId(), subtask);
                fileBackedTasksManager.epicList.get(subtask.getEpicId()).addSubtask(subtask.getId());
                fileBackedTasksManager.updateEpicDates(subtask.getEpicId());
                fileBackedTasksManager.updateSortedByStartDateList(subtask);
                break;
        }
    }

    private LocalDateTime calculateEndDate(LocalDateTime date, long duration) {
        if (date == null) {
            return null;
        }
        return date.plusMinutes(duration);
    }

    // Метод загрузки истории из строки
    private void restoreHistoryFromString(String[] stringArray, FileBackedTasksManager fileBackedTasksManager) {
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

    private LocalDateTime returnDateOrNull(String string) {
        if (string.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(string);
    }
}

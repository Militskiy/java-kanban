package managers;

import managers.util.StateSaver;
import managers.util.StateLoader;
import tasks.*;
import tasks.util.Status;
import tasks.util.TaskType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static tasks.util.TaskType.*;
import static managers.util.Constants.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final Path FILE = Paths.get(DEFAULT_FILE_PATH);

    public static void main(String[] args) {
        // Для генерации CSV запускать class Main
        System.out.println(NEXT_LINE + "Loading Data");
        FileBackedTasksManager taskManager = loadFromFile(FILE);

        // Проверка, что после загрузки данных из файла, корректно работает добавление новых задач
        System.out.println("\n" + "Adding 1 Task, 1 Epic, 1 Subtask");
        Task task = new Task(null, TaskType.TASK, "New Task", "added after loading",
                Status.NEW, LocalDateTime.of(2022, 7, 25, 23, 20), 600);
        taskManager.addTask(task);
        Epic epic = new Epic(null, TaskType.EPIC, "New Epic", "added after loading");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(null, TaskType.SUBTASK, "New Subtask", "added after loading",
                Status.NEW, epic, LocalDateTime.of(2022, 7, 26, 23, 21), 600);
        taskManager.addSubTask(subtask);

        System.out.println(NEXT_LINE + "List every Task, Epic and Subtask");
        taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

        System.out.println(NEXT_LINE + "Showing loaded history");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        System.out.println(NEXT_LINE + "Showing loaded history + newly added and requested tasks");
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        System.out.println(NEXT_LINE + "Testing Epics subtask list");
        taskManager.listEpics().forEach((k, v) -> {
            System.out.println(v.getType() + " ID: " + v.getId());
            v.getSubtaskList().forEach((value) -> System.out.println(value.getType() + " " + value));
            System.out.println();
        });
        System.out.println("\n" + "Sorted List");
        taskManager.listPrioritizedTasks().forEach(System.out::println);
    }

    // Метод загрузки из файла
    public static FileBackedTasksManager loadFromFile(Path file) {
        FileBackedTasksManager fileBackedTasksManager = (FileBackedTasksManager) Managers.getDefault();
        for (int i = 0; i < StateLoader.loadTaskState(file).size(); i++) {
            restoreTaskFromString(StateLoader.loadTaskState(file).get(i), fileBackedTasksManager);
        }
        for (int i = StateLoader.loadHistoryState(file).size() - 1; i > -1; i--) {
            restoreHistoryFromString(StateLoader.loadHistoryState(file).get(i), fileBackedTasksManager);
        }
        return fileBackedTasksManager;
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
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
    public void addTask(Task task) {
        super.addTask(task);
        save();
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
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
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
                Task task = new Task(stringArray[0], TASK, stringArray[2], stringArray[4], Status.valueOf(stringArray[3]), returnDateOrNull(stringArray[6]), Long.parseLong(stringArray[7]));
                fileBackedTasksManager.taskList.put(stringArray[0], task);
                fileBackedTasksManager.updateSortedByStartDateList(task);
                break;
            case EPIC:
                Epic epic = new Epic(stringArray[0], EPIC, stringArray[2], stringArray[4], Status.valueOf(stringArray[3]),
                        returnDateOrNull(stringArray[6]), Long.parseLong(stringArray[7]),
                        calculateEndDate(returnDateOrNull(stringArray[6]), Long.parseLong(stringArray[7])));
                fileBackedTasksManager.epicList.put(stringArray[0], epic);
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(stringArray[0], SUBTASK, stringArray[2], stringArray[4],
                        Status.valueOf(stringArray[3]), fileBackedTasksManager.epicList.get(stringArray[5]), returnDateOrNull(stringArray[6]), Long.parseLong(stringArray[7]));
                fileBackedTasksManager.subtaskList.put(subtask.getId(), subtask);
                fileBackedTasksManager.epicList.get(subtask.getEpic().getId()).addSubtask(subtask);
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
        StateSaver.saveState(listEveryTaskAndEpicAndSubtask(), Managers.getDefaultHistory().getHistory());
    }
}

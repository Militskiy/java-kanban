package managers;

import managers.exceptions.ManagerSaveException;
import managers.util.FileSaver;
import managers.util.StateLoader;
import tasks.*;
import tasks.util.Status;
import tasks.util.TaskType;

import static tasks.util.TaskType.*;
import static managers.util.Constants.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        // Для генерации CSV запускать class Main
        System.out.println(NEXT_LINE + "Loading Data");
        FileBackedTasksManager taskManager = loadFromFile();

        // Проверка, что после загрузки данных из файла, корректно работает добавление новых задач
        System.out.println("\n" + "Adding 1 Task, 1 Epic, 1 Subtask");
        Task task = new Task(null, TaskType.TASK, "New Task", "added after loading", Status.NEW);
        taskManager.addTask(task);
        Epic epic = new Epic(null, TaskType.EPIC, "New Epic", "added after loading");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(null, TaskType.SUBTASK, "New Subtask", "added after loading",
                Status.NEW, epic);
        taskManager.addSubTask(subtask);

        System.out.println(NEXT_LINE + "List every Task, Epic and Subtask");
        taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

        System.out.println(NEXT_LINE + "Showing loaded history");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        System.out.println(NEXT_LINE + "Requesting new tasks and showing loaded + newly added history");
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);
    }

    // Метод загрузки из файла
    public static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager fileBackedTasksManager = (FileBackedTasksManager) Managers.getDefault();
        for (int i = 0; i < StateLoader.loadTaskState().size(); i++) {
            taskFromString(StateLoader.loadTaskState().get(i), fileBackedTasksManager);
        }
        for (int i = StateLoader.loadHistoryState().size() - 1; i > -1; i--) {
            historyFromString(StateLoader.loadHistoryState().get(i), fileBackedTasksManager);
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
    private static void taskFromString(String[] stringArray, FileBackedTasksManager fileBackedTasksManager) {
        switch (TaskType.valueOf(stringArray[1])) {
            case TASK:
                fileBackedTasksManager.taskList.put(stringArray[0], new Task(stringArray[0], TASK,
                        stringArray[2], stringArray[4], Status.valueOf(stringArray[3])));
                break;
            case EPIC:
                fileBackedTasksManager.epicList.put(stringArray[0], new Epic(stringArray[0], EPIC,
                        stringArray[2], stringArray[4], Status.valueOf(stringArray[3])));
                break;
            case SUBTASK:
                fileBackedTasksManager.subtaskList.put(stringArray[0], new Subtask(stringArray[0], SUBTASK,
                        stringArray[2], stringArray[4], Status.valueOf(stringArray[3]),
                        fileBackedTasksManager.epicList.get(stringArray[5])));
                break;
        }
    }

    // Метод загрузки истории из строки
    private static void historyFromString(String[] stringArray, FileBackedTasksManager fileBackedTasksManager) {
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
    // Метод сохранения состояния в CSV
    private void save() {
        try {
            FileSaver.saveState(listEveryTaskAndEpicAndSubtask(), Managers.getDefaultHistory().getHistory());
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }
}

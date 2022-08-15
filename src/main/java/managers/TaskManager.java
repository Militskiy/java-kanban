package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    // Метод добавления tasks.Subtask
    String addSubTask(Subtask subtask);

    // Метод обновления tasks.Subtask
    void updateSubtask(Subtask subtask);

    // Метод удаления tasks.Subtask
    void deleteSubTask(String subtaskID);

    // Метод удаления всех tasks.Subtask
    void deleteAllSubTasks();

    // Метод добавления tasks.Task
    String addTask(Task task);

    // Метод обновления tasks.Task
    void updateTask(Task task);

    // Метод удаления tasks.Task
    void deleteTask(String taskID);

    void deleteAllTasks();

    // Метод добавления tasks.Epic
    String addEpic(Epic epic);

    // Метод обновления tasks.Epic
    void updateEpic(Epic epic);

    // Метод удаления tasks.Epic
    void deleteEpic(String id);

    // Метод удаления всех tasks.Epic
    void deleteAllEpics();

    // Метод запроса списка tasks.Task
    LinkedHashMap<String, Task> listTasks();

    // Метод запроса конкретной tasks.Task
    Task getTaskById(String id);

    // Метод запроса списка tasks.Epic
    LinkedHashMap<String, Epic> listEpics();

    // Метод запроса конкретного tasks.Epic
    Epic getEpicById(String id);

    // Метод запроса списка tasks.Subtask
    LinkedHashMap<String, Subtask> listAllSubtasks();

    // Метод запроса конкретного tasks.Subtask
    Subtask getSubtaskById(String id);

    // Метод запроса списка tasks.Subtask конкретного tasks.Epic
    LinkedHashMap<String, Subtask> listEpicSubtasks(String epicID);

    // Метод вывода сразу всех задач, эпиков и подзадач
    List<Task> listEveryTaskAndEpicAndSubtask();
    TreeSet<Task> listPrioritizedTasks();
    <T extends Task> void updateSortedByStartDateList (T task);
    List<Task> getHistory();
    boolean isDataLoaded();
}
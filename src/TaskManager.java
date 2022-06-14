import java.util.LinkedHashMap;
import java.util.List;

public interface TaskManager {
    HistoryManager historyManager = Managers.getDefaultHistory();

    // Метод добавления Subtask
    void addSubTask(Subtask subtask);

    // Метод обновления Subtask
    void updateSubtask(Subtask subtask);

    // Метод удаления Subtask
    void deleteSubTask(String subtaskID);

    // Метод удаления всех Subtask
    void deleteAllSubTasks();

    // Метод добавления Task
    void addTask(Task task);

    // Метод обновления Task
    void updateTask(Task task);

    // Метод удаления Task
    void deleteTask(String taskID);

    void deleteAllTasks();

    // Метод добавления Epic
    void addEpic(Epic epic);

    // Метод обновления Epic
    void updateEpic(Epic epic);

    // Метод удаления Epic
    void deleteEpic(String id);

    // Метод удаления всех Epic
    void deleteAllEpics();

    // Метод запроса списка Task
    LinkedHashMap<String, Task> listTasks();

    // Метод запроса конкретной Task
    Task getTaskById(String id);

    // Метод запроса списка Epic
    LinkedHashMap<String, Epic> listEpics();

    // Метод запроса конкретного Epic
    Epic getEpicById(String id);

    // Метод запроса списка Subtask
    LinkedHashMap<String, Subtask> listAllSubtasks();

    // Метод запроса конкретного Subtask
    Subtask getSubtaskById(String id);

    // Метод запроса списка Subtask конкретного Epic
    LinkedHashMap<String, Subtask> listEpicSubtasks(String epicID);

    // Метод вывода сразу всех задач, эпиков и подзадач
    List<Task> listEveryTaskAndEpicAndSubtask();

    // Метод расчета статуса Epic в зависимости от статусов его Subtask
    void updateEpicStatus(Subtask subtask);
}
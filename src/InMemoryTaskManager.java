import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Subtask> subtaskList = new LinkedHashMap<>();

    // Метод добавления Subtask
    @Override
    public void addSubTask(Subtask subtask) {
        subtask.setId(IdGenerator.generateID("Subtask"));
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод обновления Subtask
    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод удаления Subtask
    @Override
    public void deleteSubTask(String subtaskID) {
        Subtask subtask = subtaskList.get(subtaskID);
        subtaskList.remove(subtaskID);
        epicList.get(subtask.getEpicId()).getSubtaskList().remove(subtaskID);
        updateEpicStatus(subtask);
    }

    // Метод удаления всех Subtask
    @Override
    public void deleteAllSubTasks() {
        subtaskList.clear();
        for (String epic : epicList.keySet()) {
            Epic epic1 = epicList.get(epic);
            epic1.setStatus(Status.NEW);
            epicList.put(epic, epic1);
        }
    }

    // Метод добавления Task
    @Override
    public void addTask(Task task) {
        task.setId(IdGenerator.generateID("Task"));
        taskList.put(task.getId(), task);
    }

    // Метод обновления Task
    @Override
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    // Метод удаления Task
    @Override
    public void deleteTask(String taskID) {
        taskList.remove(taskID);
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    // Метод добавления Epic
    @Override
    public void addEpic(Epic epic) {
        epic.setId(IdGenerator.generateID("Epic"));
        epic.setStatus(Status.NEW);
        epicList.put(epic.getId(), epic);
    }

    // Метод обновления Epic
    @Override
    public void updateEpic(Epic epic) {
        epic.setStatus(epicList.get(epic.getId()).getStatus());
        epicList.put(epic.getId(), epic);
    }

    // Метод удаления Epic
    @Override
    public void deleteEpic(String id) {
        epicList.remove(id);
        subtaskList.values().removeIf(n -> id.equals(n.getEpicId()));
    }

    // Метод удаления всех Epic
    @Override
    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    // Метод запроса списка Task
    @Override
    public LinkedHashMap<String, Task> listTasks() {
        return taskList;
    }

    // Метод запроса конкретной Task
    @Override
    public Task getTaskById(String id) {
        Task task = taskList.get(id);
        Managers.getDefaultHistory().add(task);
        return task;
    }

    // Метод запроса списка Epic
    @Override
    public LinkedHashMap<String, Epic> listEpics() {
        return epicList;
    }

    // Метод запроса конкретного Epic
    @Override
    public Epic getEpicById(String id) {
        Epic epic = epicList.get(id);
        Managers.getDefaultHistory().add(epic);
        return epic;
    }

    // Метод запроса списка Subtask
    @Override
    public LinkedHashMap<String, Subtask> listAllSubtasks() {
        return subtaskList;
    }

    // Метод запроса конкретного Subtask
    @Override
    public Subtask getSubtaskById(String id) {
        Subtask subtask = subtaskList.get(id);
        Managers.getDefaultHistory().add(subtask);
        return subtask;
    }

    // Метод запроса списка Subtask конкретного Epic
    @Override
    public LinkedHashMap<String, Subtask> listEpicSubtasks(String epicID) {
        LinkedHashMap<String, Subtask> list = new LinkedHashMap<>();
        for (String id : subtaskList.keySet()) {
            if (subtaskList.get(id).getEpicId().equals(epicID)) {
                list.put(id, subtaskList.get(id));
            }
        }
        return list;
    }

    // Метод вывода сразу всех задач, эпиков и подзадач
    @Override
    public List<Task> listEveryTaskAndEpicAndSubtask() {
        List<Task> list = new ArrayList<>();
        for (String id : taskList.keySet()) {
            Task task = taskList.get(id);
            list.add(task);
        }
        for (String id : epicList.keySet()) {
            Epic epic = epicList.get(id);
            list.add(epic);
        }
        for (String id : subtaskList.keySet()) {
            Subtask subtask = subtaskList.get(id);
            list.add(subtask);
        }
        return list;
    }

    // Метод расчета статуса Epic в зависимости от статусов его Subtask
    @Override
    public void updateEpicStatus(Subtask subtask) {
        ArrayList<Status> epicStatusList = new ArrayList<>(epicList.get(subtask.getEpicId()).getSubtaskList().values());
        Epic epic = epicList.get(subtask.getEpicId());
        if (epicList.get(subtask.getEpicId()).getSubtaskList().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            if (epicStatusList.stream().allMatch(epicStatusList.get(0)::equals)) {
                epic.setStatus(epicStatusList.get(0));
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
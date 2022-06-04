import java.util.*;

public class TaskManager {
    private final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Subtask> subtaskList = new LinkedHashMap<>();

    IdGenerator idGenerator = new IdGenerator();

    // Метод добавления Subtask
    public void addSubTask(Subtask subtask) {
        subtask.setId(idGenerator.generateID("Subtask"));
        subtaskList.put(subtask.getId(), subtask);
        updateEpicStatus(subtask);
    }

    // Метод обновления Subtask
    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        updateEpicStatus(subtask);
    }

    // Метод удаления Subtask
    public void deleteSubTask(String subtaskID) {
        Subtask subtask = subtaskList.get(subtaskID);
        subtaskList.remove(subtaskID);
        updateEpicStatus(subtask);
    }

    // Метод удаления всех Subtask
    public void deleteAllSubTasks() {
        subtaskList.clear();
        for (String epic : epicList.keySet()) {
            Epic epic1 = epicList.get(epic);
            epic1.setStatus(Status.NEW);
            epicList.put(epic, epic1);
        }
    }

    // Метод добавления Task
    public void addTask(Task task) {
        task.setId(idGenerator.generateID("Task"));
        taskList.put(task.getId(), task);
    }

    // Метод обновления Task
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    // Метод удаления Task
    public void deleteTask(String taskID) {
        taskList.remove(taskID);
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    // Метод добавления Epic
    public void addEpic(Epic epic) {
        epic.setId(idGenerator.generateID("Epic"));
        epic.setStatus(Status.NEW);
        epicList.put(epic.getId(), epic);
    }

    // Метод обновления Epic
    public void updateEpic(Epic epic) {
        epic.setStatus(epicList.get(epic.getId()).getStatus());
        epicList.put(epic.getId(), epic);
    }

    // Метод удаления Epic
    public void deleteEpic(String id) {
        epicList.remove(id);
        subtaskList.values().removeIf(n -> id.equals(n.getEpicId()));
    }

    // Метод удаления всех Epic
    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    // Метод запроса списка Task
    public LinkedHashMap<String, Task> listTasks() {
        return taskList;
    }

    // Метод запроса конкретной Task
    public Task giveTask(String id) {
        return taskList.get(id);
    }

    // Метод запроса списка Epic
    public LinkedHashMap<String, Epic> listEpics() {
        return epicList;
    }

    // Метод запроса конкретного Epic
    public Epic giveEpic(String id) {
        return epicList.get(id);
    }

    // Метод запроса списка Subtask
    public LinkedHashMap<String, Subtask> listAllSubtasks() {
        return subtaskList;
    }

    // Метод запроса конкретного Subtask
    public Subtask giveSubtask(String id) {
        return subtaskList.get(id);
    }

    // Метод запроса списка Subtask конкретного Epic
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
    public List<Object> listEveryTaskAndEpicAndSubtask() {
        List<Object> list = new ArrayList<>();
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
    public void updateEpicStatus(Subtask subtask) {
        ArrayList<String> epicStatusList = new ArrayList<>();
        for (String id : subtaskList.keySet()) {
            if (subtaskList.get(id).getEpicId().equals(subtask.getEpicId())) {
                epicStatusList.add(subtaskList.get(id).getStatus());
            }
        }
        String status;
        if (epicStatusList.isEmpty()) {
            status = Status.NEW;
            Epic epic = epicList.get(subtask.getEpicId());
            epic.setStatus(status);
            epicList.put(subtask.getEpicId(), epic);
        } else {
            if (epicStatusList.stream().allMatch(epicStatusList.get(0)::equals)) {
                status = epicStatusList.get(0);
            } else {
                status = Status.IN_PROGRESS;
            }
            Epic epic = epicList.get(subtask.getEpicId());
            epic.setStatus(status);
            epicList.put(subtask.getEpicId(), epic);
        }
    }
}

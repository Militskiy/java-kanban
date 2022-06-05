import java.util.*;

public class TaskManager {
    private final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();

    IdGenerator idGenerator = new IdGenerator();

    // Метод добавления Subtask
    public void addSubTask(Subtask subtask) {
        subtask.setId(idGenerator.generateID("Subtask"));
        epicList.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод обновления Subtask
    public void updateSubtask(Subtask subtask) {
        epicList.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод удаления Subtask
    public void deleteSubTask(String subtaskID) {
        for (String id : epicList.keySet()) {
            Subtask subtask = epicList.get(id).getSubtaskList().get(subtaskID);
            if (epicList.get(id).getSubtaskList().containsKey(subtaskID)) {
                epicList.get(id).getSubtaskList().remove(subtaskID);
                updateEpicStatus(subtask);
            }
        }
    }

    // Метод удаления всех Subtask
    public void deleteAllSubTasks() {
        for (String epic : epicList.keySet()) {
            epicList.get(epic).getSubtaskList().clear();
            epicList.get(epic).setStatus(Status.NEW);
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
        epicList.put(epic.getId(), epic);
    }

    // Метод удаления Epic
    public void deleteEpic(String id) {
        epicList.remove(id);
    }

    // Метод удаления всех Epic
    public void deleteAllEpics() {
        epicList.clear();
    }

    // Метод запроса списка Task
    public LinkedHashMap<String, Task> listTasks() {
        return taskList;
    }

    // Метод запроса конкретной Task
    public Task getTaskById(String id) {
        return taskList.get(id);
    }

    // Метод запроса списка Epic
    public LinkedHashMap<String, Epic> listEpics() {
        return epicList;
    }

    // Метод запроса конкретного Epic
    public Epic getEpicById(String id) {
        return epicList.get(id);
    }

    // Метод запроса списка Subtask
    public List<Subtask> listAllSubtasks() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (String epic : epicList.keySet()) {
            for (String subtask : epicList.get(epic).getSubtaskList().keySet()) {
                subtaskList.add(epicList.get(epic).getSubtaskList().get(subtask));
            }
        }
        return subtaskList;
    }

    // Метод запроса конкретного Subtask
    public Subtask getSubtaskById(String subtaskId) {
        for (String epic : epicList.keySet()) {
            if (epicList.get(epic).getSubtaskList().containsKey(subtaskId)) {
                return epicList.get(epic).getSubtaskList().get(subtaskId);
            }
        }
        return null;
    }

    // Метод запроса списка Subtask конкретного Epic
    public LinkedHashMap<String, Subtask> listEpicSubtasks(String epicID) {
        return epicList.get(epicID).getSubtaskList();
    }

    // Метод вывода сразу всех задач, эпиков и подзадач
    public List<Object> listEveryTaskAndEpicAndSubtask() {
        List<Object> list = new ArrayList<>();
        for (String id : taskList.keySet()) {
            Task task = taskList.get(id);
            list.add(task);
        }
        for (String id : epicList.keySet()) {
            list.add(epicList.get(id));
            for (String subtaskId : epicList.get(id).getSubtaskList().keySet()) {
                list.add(epicList.get(id).getSubtaskList().get(subtaskId));
            }
        }
        return list;
    }

    // Метод расчета статуса Epic в зависимости от статусов его Subtask
    private void updateEpicStatus(Subtask subtask) {
        ArrayList<Status> epicStatusList = new ArrayList<>();
        for (String id : epicList.get(subtask.getEpicId()).getSubtaskList().keySet()) {
            epicStatusList.add(epicList.get(subtask.getEpicId()).getSubtaskList().get(id).getStatus());
        }
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
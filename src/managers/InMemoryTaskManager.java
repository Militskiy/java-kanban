package managers;

import managers.util.*;
import tasks.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Subtask> subtaskList = new LinkedHashMap<>();

    // Метод добавления Subtask
    @Override
    public void addSubTask(Subtask subtask) {
        subtask.setId(IdGenerator.generateID("Subtask"));
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(subtask.getEpic().getId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод обновления Subtask
    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        updateEpicStatus(subtask);
    }

    // Метод удаления Subtask
    @Override
    public void deleteSubTask(String subtaskID) {
        if (subtaskList.containsKey(subtaskID)) {
            epicList.get(subtaskList.get(subtaskID).getEpic().getId()).getSubtaskList().remove(subtaskList.get(subtaskID));
            updateEpicStatus(subtaskList.get(subtaskID));
            subtaskList.remove(subtaskID);
            Managers.getDefaultHistory().remove(List.of(subtaskID));
        }
    }

    // Метод удаления всех Subtask
    @Override
    public void deleteAllSubTasks() {
        Managers.getDefaultHistory().remove(new ArrayList<>(subtaskList.keySet()));
        subtaskList.clear();
        epicList.forEach((id, epic) -> {
            epic.setStatus(Status.NEW);
            epicList.put(id, epic);
        });
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
        if (taskList.containsKey(taskID)) {
            taskList.remove(taskID);
            Managers.getDefaultHistory().remove(List.of(taskID));
        }
    }

    @Override
    public void deleteAllTasks() {
        Managers.getDefaultHistory().remove(new ArrayList<>(taskList.keySet()));
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
        if (epicList.containsKey(id)) {
            List<String> ids = new ArrayList<>(List.of(id));
            epicList.get(id).getSubtaskList().forEach(subtask -> {
                ids.add(subtask.getId());
                subtaskList.remove(subtask.getId());
            });
            Managers.getDefaultHistory().remove(ids);
            epicList.remove(id);
        }
    }

    // Метод удаления всех Epic
    @Override
    public void deleteAllEpics() {
        Managers.getDefaultHistory().remove(new ArrayList<>(subtaskList.keySet()));
        subtaskList.clear();
        Managers.getDefaultHistory().remove(new ArrayList<>(epicList.keySet()));
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
        if (taskList.containsKey(id)) {
            Task task = taskList.get(id);
            Managers.getDefaultHistory().add(task);
            return task;
        }
        return null;
    }

    // Метод запроса списка Epic
    @Override
    public LinkedHashMap<String, Epic> listEpics() {
        return epicList;
    }

    // Метод запроса конкретного Epic
    @Override
    public Epic getEpicById(String id) {
        if (epicList.containsKey(id)) {
            Epic epic = epicList.get(id);
            Managers.getDefaultHistory().add(epic);
            return epic;
        }
        return null;
    }

    // Метод запроса списка Subtask
    @Override
    public LinkedHashMap<String, Subtask> listAllSubtasks() {
        return subtaskList;
    }

    // Метод запроса конкретного Subtask
    @Override
    public Subtask getSubtaskById(String id) {
        if (subtaskList.containsKey(id)) {
            Subtask subtask = subtaskList.get(id);
            Managers.getDefaultHistory().add(subtask);
            return subtask;
        }
        return null;
    }

    // Метод запроса списка Subtask конкретного Epic
    @Override
    public LinkedHashMap<String, Subtask> listEpicSubtasks(String epicID) {
        if (epicList.containsKey(epicID)) {
            LinkedHashMap<String, Subtask> list = new LinkedHashMap<>();
            epicList.get(epicID).getSubtaskList().forEach(subtask -> list.put(subtask.getId(), subtask));
            return list;
        }
        return null;
    }

    // Метод вывода сразу всех задач, эпиков и подзадач
    @Override
    public List<Task> listEveryTaskAndEpicAndSubtask() {
        List<Task> list = new ArrayList<>(taskList.values());
        list.addAll(epicList.values());
        list.addAll(subtaskList.values());
        return list;
    }

    // Метод расчета статуса Epic в зависимости от статусов его Subtask
    private void updateEpicStatus(Subtask subtask) {
        Epic epic = epicList.get(subtask.getEpic().getId());
        ArrayList<Status> epicStatusList = new ArrayList<>();
        epic.getSubtaskList().forEach(subtask1 -> epicStatusList.add(subtask1.getStatus()));
        if (epic.getSubtaskList().isEmpty()) {
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
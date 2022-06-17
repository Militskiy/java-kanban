package managers;

import managers.util.*;
import tasks.*;

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
        epicList.get(subtaskList.get(subtaskID).getEpic().getId()).getSubtaskList().remove(subtaskList.get(subtaskID));
        updateEpicStatus(subtaskList.get(subtaskID));
        subtaskList.remove(subtaskID);
    }

    // Метод удаления всех Subtask
    @Override
    public void deleteAllSubTasks() {
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
        subtaskList.values().removeIf(subtask -> id.equals(subtask.getEpic().getId()));
    }

    // Метод удаления всех Epic
    @Override
    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    // Метод запроса списка tasks.Task
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
        subtaskList.forEach((id, subtask) -> {
            if (subtask.getEpic().getId().equals(epicID)) {
                list.put(id, subtask);
            }
        });
        return list;
    }

    // Метод вывода сразу всех задач, эпиков и подзадач
    @Override
    public List<Task> listEveryTaskAndEpicAndSubtask() {
        List<Task> list = new ArrayList<>();
        taskList.forEach((id, task) -> list.add(task));
        epicList.forEach((id, epic) -> list.add(epic));
        subtaskList.forEach((id, subtask) -> list.add(subtask));
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
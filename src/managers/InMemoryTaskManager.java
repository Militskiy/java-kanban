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

    // Метод добавления tasks.Subtask
    @Override
    public void addSubTask(Subtask subtask) {
        subtask.setId(IdGenerator.generateID("Subtask"));
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод обновления tasks.Subtask
    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        epicList.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask);
    }

    // Метод удаления tasks.Subtask
    @Override
    public void deleteSubTask(String subtaskID) {
        Subtask subtask = subtaskList.get(subtaskID);
        subtaskList.remove(subtaskID);
        epicList.get(subtask.getEpicId()).getSubtaskList().remove(subtaskID);
        updateEpicStatus(subtask);
    }

    // Метод удаления всех tasks.Subtask
    @Override
    public void deleteAllSubTasks() {
        subtaskList.clear();
        epicList.forEach((k, v) -> {
            v.setStatus(Status.NEW);
            epicList.put(k, v);
        });
    }

    // Метод добавления tasks.Task
    @Override
    public void addTask(Task task) {
        task.setId(IdGenerator.generateID("Task"));
        taskList.put(task.getId(), task);
    }

    // Метод обновления tasks.Task
    @Override
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    // Метод удаления tasks.Task
    @Override
    public void deleteTask(String taskID) {
        taskList.remove(taskID);
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    // Метод добавления tasks.Epic
    @Override
    public void addEpic(Epic epic) {
        epic.setId(IdGenerator.generateID("Epic"));
        epic.setStatus(Status.NEW);
        epicList.put(epic.getId(), epic);
    }

    // Метод обновления tasks.Epic
    @Override
    public void updateEpic(Epic epic) {
        epic.setStatus(epicList.get(epic.getId()).getStatus());
        epicList.put(epic.getId(), epic);
    }

    // Метод удаления tasks.Epic
    @Override
    public void deleteEpic(String id) {
        epicList.remove(id);
        subtaskList.values().removeIf(n -> id.equals(n.getEpicId()));
    }

    // Метод удаления всех tasks.Epic
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

    // Метод запроса конкретной tasks.Task
    @Override
    public Task getTaskById(String id) {
        Task task = taskList.get(id);
        Managers.getDefaultHistory().add(task);
        return task;
    }

    // Метод запроса списка tasks.Epic
    @Override
    public LinkedHashMap<String, Epic> listEpics() {
        return epicList;
    }

    // Метод запроса конкретного tasks.Epic
    @Override
    public Epic getEpicById(String id) {
        Epic epic = epicList.get(id);
        Managers.getDefaultHistory().add(epic);
        return epic;
    }

    // Метод запроса списка tasks.Subtask
    @Override
    public LinkedHashMap<String, Subtask> listAllSubtasks() {
        return subtaskList;
    }

    // Метод запроса конкретного tasks.Subtask
    @Override
    public Subtask getSubtaskById(String id) {
        Subtask subtask = subtaskList.get(id);
        Managers.getDefaultHistory().add(subtask);
        return subtask;
    }

    // Метод запроса списка tasks.Subtask конкретного tasks.Epic
    @Override
    public LinkedHashMap<String, Subtask> listEpicSubtasks(String epicID) {
        LinkedHashMap<String, Subtask> list = new LinkedHashMap<>();
        subtaskList.forEach((id, subtask) -> {
            if (subtask.getEpicId().equals(epicID)) {
                list.put(id, subtask);
            }
        });
        return list;
    }

    // Метод вывода сразу всех задач, эпиков и подзадач
    @Override
    public List<Task> listEveryTaskAndEpicAndSubtask() {
        List<Task> list = new ArrayList<>();
        taskList.forEach((k, v) -> list.add(v));
        epicList.forEach((k, v) -> list.add(v));
        subtaskList.forEach((k, v) -> list.add(v));
        return list;
    }

    // Метод расчета статуса tasks.Epic в зависимости от статусов его tasks.Subtask
    private void updateEpicStatus(Subtask subtask) {
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
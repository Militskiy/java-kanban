package managers;

import managers.exceptions.ValidationException;
import managers.util.IdGenerator;
import tasks.util.TaskComparator;
import tasks.util.TaskValidator;
import tasks.*;
import tasks.util.Status;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final TaskComparator taskComparator = new TaskComparator();
    protected final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Subtask> subtaskList = new LinkedHashMap<>();
    protected final TreeSet<Task> dateSortedTaskList = new TreeSet<>(taskComparator);

    // Метод добавления Subtask
    @Override
    public void addSubTask(Subtask subtask) {
        try {
            TaskValidator.validateTask(subtask, "add");
            subtask.setId(IdGenerator.generateID());
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpic().getId()).addSubtask(subtask);
            updateEpicStatus(subtask);
            updateEpicDates(subtask);
            updateSortedByStartDateList(subtask);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }
    }

    // Метод обновления Subtask
    @Override
    public void updateSubtask(Subtask subtask) {
        try {
            TaskValidator.validateTask(subtask, "update");
            dateSortedTaskList.remove(subtaskList.get(subtask.getId()));
            subtaskList.put(subtask.getId(), subtask);
            updateEpicStatus(subtask);
            updateEpicDates(subtask);
            updateSortedByStartDateList(subtask);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }

    }

    // Метод удаления Subtask
    @Override
    public void deleteSubTask(String subtaskID) {
        if (subtaskList.containsKey(subtaskID)) {
            epicList.get(subtaskList.get(subtaskID).getEpic().getId()).getSubtaskList().remove(subtaskList.get(subtaskID));
            updateEpicStatus(subtaskList.get(subtaskID));
            updateEpicDates(subtaskList.get(subtaskID));
            subtaskList.remove(subtaskID);
            Managers.getDefaultHistory().remove(List.of(subtaskID));
            dateSortedTaskList.remove(subtaskList.get(subtaskID));
        }
    }

    // Метод удаления всех Subtask
    @Override
    public void deleteAllSubTasks() {
        Managers.getDefaultHistory().remove(new ArrayList<>(subtaskList.keySet()));
        subtaskList.forEach((id, subtask) -> dateSortedTaskList.remove(subtask));
        subtaskList.clear();
        epicList.forEach((id, epic) -> {
            epic.setStatus(Status.NEW);
            epic.clearSubTaskList();
            epicList.put(id, epic);
        });
    }

    // Метод добавления Task
    @Override
    public void addTask(Task task) {
        try {
            TaskValidator.validateTask(task, "add");
            task.setId(IdGenerator.generateID());
            taskList.put(task.getId(), task);
            updateSortedByStartDateList(task);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }
    }

    // Метод обновления Task
    @Override
    public void updateTask(Task task) {
        try {
            TaskValidator.validateTask(task, "update");
            dateSortedTaskList.remove(taskList.get(task.getId()));
            taskList.put(task.getId(), task);
            updateSortedByStartDateList(task);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }
    }

    // Метод удаления Task
    @Override
    public void deleteTask(String taskID) {
        if (taskList.containsKey(taskID)) {
            taskList.remove(taskID);
            Managers.getDefaultHistory().remove(List.of(taskID));
            dateSortedTaskList.remove(taskList.get(taskID));
        }
    }

    @Override
    public void deleteAllTasks() {
        Managers.getDefaultHistory().remove(new ArrayList<>(taskList.keySet()));
        taskList.forEach((id, task) -> dateSortedTaskList.remove(task));
        taskList.clear();
    }

    // Метод добавления Epic
    @Override
    public void addEpic(Epic epic) {
        epic.setId(IdGenerator.generateID());
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
        epicList.forEach((id, epic) -> epic.getSubtaskList().forEach(dateSortedTaskList::remove));
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
    @Override
    public TreeSet<Task> listPrioritizedTasks() {
        return dateSortedTaskList;
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

    // Метод обновления endDate Epic
    private void updateEpicDates(Subtask subtask) {
        Epic epic = epicList.get(subtask.getEpic().getId());
        LocalDateTime minDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime maxDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        long epicDuration = 0;
        for (Subtask subtask1 : epic.getSubtaskList()) {
            if (subtask1.getStartDate() != null) {
                if (minDate.isBefore(subtask1.getStartDate())) {
                    minDate = subtask1.getStartDate();
                }
                if (maxDate.isBefore(subtask1.getStartDate().plusMinutes(subtask1.getDuration()))) {
                    maxDate = subtask1.getStartDate().plusMinutes(subtask1.getDuration());
                }
                epicDuration = epicDuration + subtask1.getDuration();
            }
        }
        epic.setStartDate(minDate);
        epic.setDuration(epicDuration);
        epic.setEndDate(maxDate);
        epicList.put(epic.getId(), epic);
    }
    protected <T extends Task> void updateSortedByStartDateList (T task) {
        dateSortedTaskList.add(task);
    }
}
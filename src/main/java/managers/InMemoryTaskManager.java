package managers;

import managers.exceptions.NoSuchEpicException;
import managers.exceptions.ValidationException;
import managers.util.IdGenerator;
import tasks.util.TaskComparator;
import tasks.util.TaskValidator;
import tasks.*;
import tasks.util.Status;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TaskValidator taskValidator = new TaskValidator(this);
    protected final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Subtask> subtaskList = new LinkedHashMap<>();
    protected final TreeSet<Task> dateSortedTaskList = new TreeSet<>(new TaskComparator());

    // Метод добавления Subtask
    @Override
    public String addSubTask(Subtask subtask) {
        try {
            if (epicList.get(subtask.getEpic().getId()) == null) {
                throw new NoSuchEpicException("No such epic exists.");
            }
            if (subtask.getStatus() == null) {
                subtask.setStatus(Status.NEW);
            }
            taskValidator.validateNewTask(subtask, "add");
            subtask.setId(IdGenerator.generateID());
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpic().getId()).addSubtask(subtask);
            updateEpicStatus(subtask);
            updateEpicDates(subtask);
            updateSortedByStartDateList(subtask);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }
        return subtask.getId();
    }

    // Метод обновления Subtask
    @Override
    public void updateSubtask(Subtask subtask) {
        try {
            LocalDateTime oldStartDate = subtaskList.get(subtask.getId()).getStartDate();
            long oldDuration = subtaskList.get(subtask.getId()).getDuration();
            taskValidator.validateUpdatedTask(subtask, oldStartDate, oldDuration, "update");
            dateSortedTaskList.remove(subtaskList.get(subtask.getId()));
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpic().getId()).updateSubtask(subtask);
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
            dateSortedTaskList.remove(subtaskList.get(subtaskID));
            subtaskList.remove(subtaskID);
            historyManager.remove(List.of(subtaskID));
        }
    }

    // Метод удаления всех Subtask
    @Override
    public void deleteAllSubTasks() {
        historyManager.remove(new ArrayList<>(subtaskList.keySet()));
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
    public String addTask(Task task) {
        try {
            if (task.getStatus() == null) {
                task.setStatus(Status.NEW);
            }
            taskValidator.validateNewTask(task, "add");
            task.setId(IdGenerator.generateID());
            taskList.put(task.getId(), task);
            updateSortedByStartDateList(task);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }
        return task.getId();
    }

    // Метод обновления Task
    @Override
    public void updateTask(Task task) {
        try {
            LocalDateTime oldStartDate = taskList.get(task.getId()).getStartDate();
            long oldDuration = taskList.get(task.getId()).getDuration();
            taskValidator.validateUpdatedTask(task, oldStartDate, oldDuration, "update");
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
            Task task = taskList.get(taskID);
            dateSortedTaskList.remove(task);
            taskList.remove(taskID);
            historyManager.remove(List.of(taskID));
            taskValidator.removeTaskFromValidationMap(task);
        }
    }

    @Override
    public void deleteAllTasks() {
        historyManager.remove(new ArrayList<>(taskList.keySet()));
        taskList.forEach((id, task) -> dateSortedTaskList.remove(task));
        taskList.clear();
    }

    // Метод добавления Epic
    @Override
    public String addEpic(Epic epic) {
        epic.setId(IdGenerator.generateID());
        epic.setStatus(Status.NEW);
        epicList.put(epic.getId(), epic);
        return epic.getId();
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
            historyManager.remove(ids);
            epicList.remove(id);
        }
    }

    // Метод удаления всех Epic
    @Override
    public void deleteAllEpics() {
        epicList.forEach((id, epic) -> epic.getSubtaskList().forEach(dateSortedTaskList::remove));
        historyManager.remove(new ArrayList<>(subtaskList.keySet()));
        subtaskList.clear();
        historyManager.remove(new ArrayList<>(epicList.keySet()));
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
            historyManager.add(task);
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
            historyManager.add(epic);
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
            historyManager.add(subtask);
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

    @Override
    public <T extends Task> void updateSortedByStartDateList(T task) {
        dateSortedTaskList.add(task);
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
    protected void updateEpicDates(Subtask subtask) {
        Epic epic = epicList.get(subtask.getEpic().getId());
        LocalDateTime minDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime maxDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        long epicDuration = 0;
        for (Subtask subtask1 : epic.getSubtaskList()) {
            if (subtask1.getStartDate() != null) {
                assert minDate != null;
                if (minDate.isBefore(subtask1.getStartDate())) {
                    minDate = subtask1.getStartDate();
                }
                if (maxDate.isBefore(subtask1.getStartDate().plusMinutes(subtask1.getDuration()))) {
                    maxDate = subtask1.getStartDate().plusMinutes(subtask1.getDuration());
                }
                epicDuration = epicDuration + subtask1.getDuration();
            } else {
                minDate = null;
                maxDate = null;
                epicDuration = 0;
            }
        }
        epic.setStartDate(minDate);
        epic.setDuration(epicDuration);
        epic.setEndDate(maxDate);
        epicList.put(epic.getId(), epic);
    }
}
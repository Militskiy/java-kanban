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
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TaskValidator taskValidator = new TaskValidator(this);
    protected final LinkedHashMap<String, Task> taskList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Epic> epicList = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Subtask> subtaskList = new LinkedHashMap<>();
    protected final TreeSet<Task> dateSortedTaskSet = new TreeSet<>(new TaskComparator());
    protected boolean isDataLoaded = false;

    @Override
    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    // Метод добавления Subtask
    @Override
    public String addSubTask(Subtask subtask) {
        try {
            if (epicList.get(subtask.getEpicId()) == null) {
                throw new NoSuchEpicException("No such epic exists.");
            }
            if (subtask.getStatus() == null) {
                subtask.setStatus(Status.NEW);
            }
            taskValidator.validateNewTask(subtask, "add");
            subtask.setId(IdGenerator.generateID());
            subtaskList.put(subtask.getId(), subtask);
            epicList.get(subtask.getEpicId()).addSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            updateEpicDates(subtask.getEpicId());
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
            dateSortedTaskSet.remove(subtaskList.get(subtask.getId()));
            subtaskList.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            updateEpicDates(subtask.getEpicId());
            updateSortedByStartDateList(subtask);
        } catch (ValidationException e) {
            System.out.println(e.getDetailedMessage());
        }

    }

    // Метод удаления Subtask
    @Override
    public void deleteSubTask(String subtaskID) {
        if (subtaskList.containsKey(subtaskID)) {
            epicList.get(subtaskList.get(subtaskID).getEpicId()).getSubtaskIdList().remove(subtaskID);
            updateEpicStatus(subtaskList.get(subtaskID).getEpicId());
            updateEpicDates(subtaskList.get(subtaskID).getEpicId());
            dateSortedTaskSet.remove(subtaskList.get(subtaskID));
            subtaskList.remove(subtaskID);
            historyManager.remove(List.of(subtaskID));
        }
    }

    // Метод удаления всех Subtask
    @Override
    public void deleteAllSubTasks() {
        historyManager.remove(new ArrayList<>(subtaskList.keySet()));
        subtaskList.forEach((id, subtask) -> dateSortedTaskSet.remove(subtask));
        subtaskList.clear();
        epicList.forEach((id, epic) -> {
            epic.setStatus(Status.NEW);
            epic.setStartDate(null);
            epic.setEndDate(null);
            epic.setDuration(0);
            epic.clearSubTaskList();
            epicList.put(id, epic);
        });
    }

    // Метод добавления Task
    @Override
    public String addTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        taskValidator.validateNewTask(task, "add");
        task.setId(IdGenerator.generateID());
        taskList.put(task.getId(), task);
        updateSortedByStartDateList(task);
        return task.getId();
    }

    // Метод обновления Task
    @Override
    public void updateTask(Task task) {
        try {
            LocalDateTime oldStartDate = taskList.get(task.getId()).getStartDate();
            long oldDuration = taskList.get(task.getId()).getDuration();
            taskValidator.validateUpdatedTask(task, oldStartDate, oldDuration, "update");
            dateSortedTaskSet.remove(taskList.get(task.getId()));
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
            dateSortedTaskSet.remove(task);
            taskList.remove(taskID);
            historyManager.remove(List.of(taskID));
            taskValidator.removeTaskFromValidationMap(task);
        }
    }

    @Override
    public void deleteAllTasks() {
        historyManager.remove(new ArrayList<>(taskList.keySet()));
        taskList.forEach((id, task) -> {
            dateSortedTaskSet.remove(task);
            taskValidator.removeTaskFromValidationMap(task);
        });
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
            epicList.get(id).getSubtaskIdList().forEach(subtaskId -> {
                ids.add(subtaskId);
                subtaskList.remove(subtaskId);
            });
            historyManager.remove(ids);
            epicList.remove(id);
        }
    }

    // Метод удаления всех Epic
    @Override
    public void deleteAllEpics() {
        epicList.forEach((id, epic) -> epic.getSubtaskIdList()
                .forEach(subtaskId -> dateSortedTaskSet.remove(subtaskList.get(id))));
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
            epicList.get(epicID).getSubtaskIdList().forEach(id -> list.put(id, subtaskList.get(id)));
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
        return dateSortedTaskSet;
    }

    @Override
    public <T extends Task> void updateSortedByStartDateList(T task) {
        dateSortedTaskSet.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Метод расчета статуса Epic в зависимости от статусов его Subtask
    protected void updateEpicStatus(String epicId) {
        Epic epic = epicList.get(epicId);
        List<Status> epicStatusList = subtaskList
                .values()
                .stream()
                .filter(subtask1 -> subtask1.getEpicId().equals(epicId))
                .map(Task::getStatus)
                .collect(Collectors.toList());
        if (epic.getSubtaskIdList().isEmpty()) {
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
    protected void updateEpicDates(String epicId) {
        Epic epic = epicList.get(epicId);
        LocalDateTime minDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime maxDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        long epicDuration = 0;
        if (epic.getSubtaskIdList().isEmpty()) {
            minDate = null;
            maxDate = null;
        } else {
            for (String id : epic.getSubtaskIdList()) {
                if (subtaskList.get(id).getStartDate() != null) {
                    assert minDate != null;
                    if (minDate.isBefore(subtaskList.get(id).getStartDate())) {
                        minDate = subtaskList.get(id).getStartDate();
                    }
                    if (maxDate.isBefore(subtaskList.get(id).getStartDate()
                            .plusMinutes(subtaskList.get(id).getDuration()))) {
                        maxDate = subtaskList.get(id).getStartDate().plusMinutes(subtaskList.get(id).getDuration());
                    }
                    epicDuration = epicDuration + subtaskList.get(id).getDuration();
                } else {
                    assert minDate != null;
                    if (minDate.equals(LocalDateTime.of(1900, 1, 1, 0, 0))
                            || maxDate.equals(LocalDateTime.of(1900, 1, 1, 0, 0))){
                        minDate = null;
                        maxDate = null;
                        epicDuration = 0;
                    }
                }
            }
        }
        epic.setStartDate(minDate);
        epic.setDuration(epicDuration);
        epic.setEndDate(maxDate);
        epicList.put(epic.getId(), epic);
    }
}
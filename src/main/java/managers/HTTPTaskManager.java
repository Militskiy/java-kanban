package managers;

import clients.KVTaskClient;
import managers.util.Converter;
import tasks.Task;
import tasks.util.TaskType;

import java.util.*;
import java.util.stream.Collectors;


public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;

    public HTTPTaskManager(String url) {
        this.client = new KVTaskClient(url);
    }

    @Override
    public void load(String url) {
        isDataLoaded = true;
        String taskJson = client.load("Tasks");
        String epicJson = client.load("Epics");
        String subtaskJson = client.load("Subtasks");
        String historyJson = client.load("History");
        final HTTPTaskManager taskManager = this;
        Converter.convertJsonToTaskList(taskJson).forEach(task -> {
            taskManager.taskList.put(task.getId(), task);
            taskManager.dateSortedTaskSet.add(task);
        });
        Converter.convertJsonToEpicList(epicJson).forEach(epic -> taskManager.epicList.put(epic.getId(), epic));
        Converter.convertJsonToSubtaskList(subtaskJson).forEach(subtask -> {
            taskManager.subtaskList.put(subtask.getId(), subtask);
            taskManager.dateSortedTaskSet.add(subtask);
        });
        taskManager.epicList.forEach((id, epic) -> {
            taskManager.updateEpicStatus(id);
            taskManager.updateEpicDates(id);
        });
        Converter.convertJsonToHistoryMap(historyJson).forEach((id, type) -> {
            switch (type) {
                case TASK:
                    taskManager.historyManager.addLast(taskManager.taskList.get(id));
                    break;
                case EPIC:
                    taskManager.historyManager.addLast(taskManager.epicList.get(id));
                    break;
                case SUBTASK:
                    taskManager.historyManager.addLast(taskManager.subtaskList.get(id));
            }
        });
    }

    @Override
    protected void save() {
        client.put("Tasks", Converter.convertListToJson(new ArrayList<>(taskList.values())));
        client.put("Epics", Converter.convertListToJson(new ArrayList<>(epicList.values())));
        client.put("Subtasks", Converter.convertListToJson(new ArrayList<>(subtaskList.values())));
        LinkedHashMap<String, TaskType> history = getHistory().stream()
                .collect(Collectors.toMap(Task::getId, Task::getType, (e1, e2) -> e1, LinkedHashMap::new));
        client.put("History", Converter.convertLinkedMapToJson(history));
    }
}

package managers;

import clients.KVTaskClient;
import managers.util.Converter;

import java.util.*;


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
        Converter.convertJsonToTaskList(historyJson).forEach(taskManager.historyManager::addLast);
    }

    @Override
    protected void save() {
        client.put("Tasks", Converter.convertListToJson(new ArrayList<>(taskList.values())));
        client.put("Epics", Converter.convertListToJson(new ArrayList<>(epicList.values())));
        client.put("Subtasks", Converter.convertListToJson(new ArrayList<>(subtaskList.values())));
        client.put("History", Converter.convertListToJson(getHistory()));
    }
}

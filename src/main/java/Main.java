import managers.HTTPTaskManager;
import managers.Managers;
import managers.TaskManager;
import servers.HTTPTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.util.Status;
import tasks.util.TaskType;

import java.io.IOException;
import java.time.LocalDateTime;

import static managers.util.Constants.KV_SERVER_URL;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
        TaskManager taskManager = Managers.getDefault();


        // Тестирования запросов условного "Фронта"
        System.out.println("\n" + "Adding 2 Tasks, 2 Epics, 3 Subtasks");
        Task task1 = new Task(null, TaskType.TASK, "Task-1", "New Task-1", Status.NEW, LocalDateTime.of(2022, 7, 21, 23, 10), 600);
        taskManager.addTask(task1);
        Task task2 = new Task(null, TaskType.TASK, "Task-2", "New Task-2", Status.NEW, LocalDateTime.of(2022, 8, 22, 1, 12), (600));
        taskManager.addTask(task2);
        Epic epic1 = new Epic(null, TaskType.EPIC, "Epic-1", "New Epic-1");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic(TaskType.EPIC, "Epic-2", "New Epic-2");
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask(null, TaskType.SUBTASK, "Subtask-1", "New Subtask-1",
                Status.NEW, epic1.getId(), LocalDateTime.of(2022, 7, 23, 23, 11), 600);
        taskManager.addSubTask(subtask1);
        Subtask subtask2 = new Subtask(null, TaskType.SUBTASK, "Subtask-2", "New Subtask-2",
                Status.NEW, epic1.getId(), LocalDateTime.of(2022, 7, 24, 23, 13), 600);
        taskManager.addSubTask(subtask2);
        Subtask subtask3= new Subtask(null, TaskType.SUBTASK, "Subtask-3", "New Subtask-3",
                Status.NEW, epic1.getId(), null, 0);
        taskManager.addSubTask(subtask3);
        taskManager.listTasks().keySet().forEach(taskManager::getTaskById);
        taskManager.listEpics().keySet().forEach(taskManager::getEpicById);
        taskManager.listAllSubtasks().keySet().forEach(taskManager::getSubtaskById);

        HTTPTaskManager httpTaskManager = new HTTPTaskManager(KV_SERVER_URL);
        httpTaskManager.load(KV_SERVER_URL);
        new HTTPTaskServer(httpTaskManager).start();


        // taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

//        System.out.println("\n" + "Listing All");
//        taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);
//
//        System.out.println("\n" + "Getting all tasks epics and subtasks");
//
//
//        System.out.println("\n" + "Showing history");
//        Managers.getDefaultHistory().getHistory().forEach(System.out::println);
//        System.out.println("\n" + "Sorted List");
//        taskManager.listPrioritizedTasks().forEach(System.out::println);
//


    }
}
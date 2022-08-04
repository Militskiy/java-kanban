import managers.Managers;
import managers.TaskManager;
import tasks.util.TaskType;
import tasks.*;
import tasks.util.Status;

import java.time.LocalDateTime;

public class Main {


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Тестирования запросов условного "Фронта"
        System.out.println("\n" + "Adding 2 Tasks, 2 Epics, 3 Subtasks");
        Task task1 = new Task(null, TaskType.TASK, "Task-1", "New Task-1", Status.NEW,LocalDateTime.of(2022, 7, 21, 23, 10), 600);
        taskManager.addTask(task1);
        Task task2 = new Task(null, TaskType.TASK, "Task-2", "New Task-2", Status.NEW, LocalDateTime.of(2022, 8, 22, 1, 12), (600));
        taskManager.addTask(task2);
        Epic epic1 = new Epic(null, TaskType.EPIC, "Epic-1", "New Epic-1");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic(TaskType.EPIC, "Epic-2", "New Epic-2");
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask(null, TaskType.SUBTASK, "Subtask-1", "New Subtask-1",
                Status.NEW, epic1, LocalDateTime.of(2022, 7, 23, 23, 11), 600);
        taskManager.addSubTask(subtask1);
        Subtask subtask2 = new Subtask(null, TaskType.SUBTASK, "Subtask-2", "New Subtask-2",
                Status.NEW, epic1, LocalDateTime.of(2022, 7, 24, 23, 13), 600);
        taskManager.addSubTask(subtask2);
        Subtask subtask3= new Subtask(null, TaskType.SUBTASK, "Subtask-3", "New Subtask-3",
                Status.NEW, epic1, null, 0);
        taskManager.addSubTask(subtask3);
        // taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

        System.out.println("\n" + "Listing All");
        taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

        System.out.println("\n" + "Getting all tasks epics and subtasks");
        requestAllTasks(taskManager);

        System.out.println("\n" + "Showing history");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);
        System.out.println("\n" + "Sorted List");
        taskManager.listPrioritizedTasks().forEach(System.out::println);
        // TaskValidator.validationMap.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    // Методы для тестирования
    private static void listEpicsAndSubs(TaskManager taskManager) {
        taskManager.listEpics().forEach((epicId, epic) -> {
            System.out.println(epic);
            taskManager.listEpicSubtasks(epicId).forEach((subId, subtask) -> System.out.println(subtask));
        });
    }

    private static void listTasks(TaskManager taskManager) {
        taskManager.listTasks().forEach((id, task) -> System.out.println(task));
    }

    private static void listAllSubtasks(TaskManager taskManager) {
        taskManager.listAllSubtasks().forEach((id, subtask) -> System.out.println(subtask));
    }

    private static void requestAllTasks(TaskManager taskManager) {
        taskManager.listEveryTaskAndEpicAndSubtask().forEach(v -> {
            switch (v.getType()) {
                case TASK:
                    taskManager.getTaskById(v.getId());
                    break;
                case EPIC:
                    taskManager.getEpicById(v.getId());
                    break;
                case SUBTASK:
                    taskManager.getSubtaskById(v.getId());
                    break;
            }
        });
    }
}
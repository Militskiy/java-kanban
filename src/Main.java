import managers.Managers;
import managers.TaskManager;
import managers.util.TaskType;
import tasks.*;
import managers.util.Status;

public class Main {


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Тестирования запросов условного "Фронта"
        System.out.println("\n" + "Adding 2 Tasks, 2 Epics, 3 Subtasks");
        Task task1 = new Task(null, TaskType.TASK, "Task-1", "description", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task(null, TaskType.TASK, "Task-2", "description", Status.NEW);
        taskManager.addTask(task2);
        Epic epic1 = new Epic(null, TaskType.EPIC, "Epic-1", "description");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic(null, TaskType.EPIC, "Epic-2", "description");
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask(null, TaskType.SUBTASK, "Subtask-1", "description",
                Status.NEW, epic1);
        taskManager.addSubTask(subtask1);
        Subtask subtask2 = new Subtask(null, TaskType.SUBTASK, "Subtask-2", "description",
                Status.NEW, epic1);
        taskManager.addSubTask(subtask2);
        Subtask subtask3= new Subtask(null, TaskType.SUBTASK, "Subtask-3", "description",
                Status.NEW, epic1);
        taskManager.addSubTask(subtask3);
        // taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

        System.out.println("\n" + "Listing All");
        taskManager.listEveryTaskAndEpicAndSubtask().forEach(System.out::println);

        System.out.println("\n" + "Getting all tasks epics and subtasks");
        taskManager.getTaskById("TASK-10");
        taskManager.getTaskById("TASK-1");
        taskManager.getTaskById("TASK-1");
        taskManager.getTaskById("TASK-2");
        taskManager.getTaskById("TASK-2");
        taskManager.getEpicById("EPIC-10");
        taskManager.getEpicById("EPIC-1");
        taskManager.getEpicById("EPIC-1");
        taskManager.getEpicById("EPIC-2");
        taskManager.getEpicById("EPIC-2");
        taskManager.getSubtaskById("SUBTASK-10");
        taskManager.getSubtaskById("SUBTASK-1");
        taskManager.getSubtaskById("SUBTASK-1");
        taskManager.getSubtaskById("SUBTASK-2");
        taskManager.getSubtaskById("SUBTASK-2");
        taskManager.getSubtaskById("SUBTASK-3");
        taskManager.getSubtaskById("SUBTASK-3");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);
    }

    // Методы вывода на экран списков задач (для тестирования)
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
}
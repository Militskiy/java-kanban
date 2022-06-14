import managers.Managers;
import managers.TaskManager;
import tasks.*;
import managers.util.Status;

public class Main {


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Тестирования запросов условного "Фронта"
        System.out.println("\n" + "Adding 2 Tasks, 2 Epics, 3 Subtasks" + "\n");
        Task task = new Task(null, "Task-1", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-2", "description", Status.NEW);
        taskManager.addTask(task);
        Epic epic = new Epic(null, "Epic-1", "description");
        taskManager.addEpic(epic);
        epic = new Epic(null, "Epic-2", "description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(null, "Subtask-1", "description", Status.NEW, "Epic-1");
        taskManager.addSubTask(subtask);
        subtask = new Subtask(null, "Subtask-2", "description", Status.NEW, "Epic-1");
        taskManager.addSubTask(subtask);
        subtask = new Subtask(null, "Subtask-3", "description", Status.NEW, "Epic-2");
        taskManager.addSubTask(subtask);
        listTasks(taskManager);
        listEpicsAndSubs(taskManager);
        System.out.println(taskManager.getTaskById("Task-1"));

        System.out.println("\n" + "Subtask-1 to DONE" + "\n");

        subtask = taskManager.getSubtaskById("Subtask-1");
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Subtask-2 to DONE" + "\n");

        subtask = taskManager.getSubtaskById("Subtask-2");
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Changing Epic-1 name and description" + "\n");

        epic = taskManager.getEpicById("Epic-1");
        epic.setName("This Epic is DONE");
        epic.setDescription("Testing attribute changes");
        taskManager.updateEpic(epic);
        subtask = taskManager.getSubtaskById("Subtask-2");
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Deleting Subtask-1 & 2" + "\n");

        taskManager.deleteSubTask("Subtask-1");
        taskManager.deleteSubTask("Subtask-2");
        epic = taskManager.getEpicById("Epic-1");
        epic.setName("This Epic is NEW");
        epic.setDescription("No subtasks");
        taskManager.updateEpic(epic);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Listing all subtasks" + "\n");

        listAllSubtasks(taskManager);

        task = new Task(null, "Task-3", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-4", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-5", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-6", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-7", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-8", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-9", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-10", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-11", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-12", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-13", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Task-14", "description", Status.NEW);
        taskManager.addTask(task);

        taskManager.getSubtaskById("Subtask-3");
        taskManager.getEpicById("Epic-2");
        taskManager.getTaskById("Task-1");
        taskManager.getTaskById("Task-2");
        taskManager.getTaskById("Task-3");
        taskManager.getTaskById("Task-4");
        taskManager.getTaskById("Task-5");
        taskManager.getTaskById("Task-6");
        taskManager.getTaskById("Task-7");
        taskManager.getTaskById("Task-8");
        taskManager.getTaskById("Task-9");
        taskManager.getTaskById("Task-10");
        taskManager.getTaskById("Task-11");
        taskManager.getTaskById("Task-12");
        taskManager.getTaskById("Task-13");
        taskManager.getTaskById("Task-14");

        System.out.println("\n" + "Showing history" + "\n");

        for (Task task1 : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task1);
        }

    }

    // Методы вывода на экран списков задач (для тестирования)
    private static void listEpicsAndSubs(TaskManager taskManager) {
        for (String id : taskManager.listEpics().keySet()) {
            System.out.println(taskManager.listEpics().get(id));
            for (String subtaskID : taskManager.listEpicSubtasks(id).keySet()) {
                System.out.println(taskManager.listEpicSubtasks(id).get(subtaskID));
            }
        }
    }

    private static void listTasks(TaskManager taskManager) {
        for (String id : taskManager.listTasks().keySet()) {
            System.out.println(taskManager.listTasks().get(id));
        }
    }

    private static void listAllSubtasks(TaskManager taskManager) {
        for (String id : taskManager.listAllSubtasks().keySet()) {
            System.out.println(taskManager.listAllSubtasks().get(id));
        }
    }
}
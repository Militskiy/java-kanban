public class Main {


    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Тестирования запросов условного "Фронта"
        System.out.println("\n" + "Adding 2 Tasks, 2 Epics, 3 Subtasks" + "\n");
        Task task = new Task(null, "Test Task-1", "description", Status.NEW);
        taskManager.addTask(task);
        task = new Task(null, "Test Task-2", "description", Status.NEW);
        taskManager.addTask(task);
        Epic epic = new Epic(null, "Test Epic-1", "description");
        taskManager.addEpic(epic);
        epic = new Epic(null, "Test Epic-2", "description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(null, "Test Subtask-1", "description", Status.NEW, "Epic-1");
        taskManager.addSubTask(subtask);
        subtask = new Subtask(null, "Test Subtask-2", "description", Status.NEW, "Epic-1");
        taskManager.addSubTask(subtask);
        subtask = new Subtask(null, "Test Subtask-3", "description", Status.NEW, "Epic-2");
        taskManager.addSubTask(subtask);
        listTasks(taskManager);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Print all" + "\n");
        for (Object obj : taskManager.listEveryTaskAndEpicAndSubtask()) {
            System.out.println(obj);
        }

        System.out.println("\n" + "Changing Task-1 & 2 statuses" + "\n");
        task = new Task("Task-1", "Test Task-1", "description", Status.IN_PROGRESS);
        taskManager.updateTask(task);
        task = new Task("Task-2", "Test Task-2", "description", Status.DONE);
        taskManager.updateTask(task);
        listTasks(taskManager);

        System.out.println("\n" + "Changing Subtask-2 status to IN_PROGRESS" + "\n");
        subtask = new Subtask("Subtask-2", "Test Subtask-2", "description", Status.IN_PROGRESS, "Epic-1");
        taskManager.updateSubtask(subtask);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Changing Subtask-1 & 2 status to DONE" + "\n");
        subtask = new Subtask("Subtask-1", "Test Subtask-1", "description", Status.DONE, "Epic-1");
        taskManager.updateSubtask(subtask);
        subtask = new Subtask("Subtask-2", "Test Subtask-2", "description", Status.DONE, "Epic-1");
        taskManager.updateSubtask(subtask);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Deleting Subtask-1 & 2" + "\n");
        taskManager.deleteSubTask("Subtask-1");
        taskManager.deleteSubTask("Subtask-2");
        epic = new Epic("Epic-1", "Test Epic-1", "Removed all subtasks");
        taskManager.updateEpic(epic);
        listEpicsAndSubs(taskManager);

        System.out.println("\n" + "Deleting Task-1" + "\n");
        taskManager.deleteTask("Task-1");
        System.out.println(taskManager.listTasks());

        System.out.println("\n" + "Showing Task-2" + "\n");
        System.out.println(taskManager.giveTask("Task-2"));

        System.out.println("\n" + "Deleting Epic-1" + "\n");
        taskManager.deleteEpic("Epic-1");
        listAllSubtasks(taskManager);

        for (Object obj : taskManager.listEveryTaskAndEpicAndSubtask()) {
            System.out.println(obj);
        }
    }

    // Методы вывода на экран списков задач (для тестирования)
    public static void listEpicsAndSubs(TaskManager taskManager) {
        for (String id : taskManager.listEpics().keySet()) {
            System.out.println(taskManager.listEpics().get(id));
            for (String subtaskID : taskManager.listEpicSubtasks(id).keySet()) {
                System.out.println(taskManager.listEpicSubtasks(id).get(subtaskID));
            }
        }
    }

    public static void listTasks(TaskManager taskManager) {
        for (String id : taskManager.listTasks().keySet()) {
            System.out.println(taskManager.listTasks().get(id));
        }
    }

    public static void listAllSubtasks(TaskManager taskManager) {
        for (String id : taskManager.listAllSubtasks().keySet()) {
            System.out.println(taskManager.listAllSubtasks().get(id));
        }
    }
}

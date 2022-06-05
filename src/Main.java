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
public class Main {


    public static void main(String[] args) {
        // Тестирования запросов условного "Фронта"
        System.out.println("\n" + "Adding 2 Tasks, 2 Epics, 3 Subtasks" + "\n");
        Task task = new Task(null, "Test Task-1", "description", Status.NEW);
        Managers.getDefault().addTask(task);
        task = new Task(null, "Test Task-2", "description", Status.NEW);
        Managers.getDefault().addTask(task);
        Epic epic = new Epic(null, "Test Epic-1", "description");
        Managers.getDefault().addEpic(epic);
        epic = new Epic(null, "Test Epic-2", "description");
        Managers.getDefault().addEpic(epic);
        Subtask subtask = new Subtask(null, "Test Subtask-1", "description", Status.NEW, "Epic-1");
        Managers.getDefault().addSubTask(subtask);
        subtask = new Subtask(null, "Test Subtask-2", "description", Status.NEW, "Epic-1");
        Managers.getDefault().addSubTask(subtask);
        subtask = new Subtask(null, "Test Subtask-3", "description", Status.NEW, "Epic-2");
        Managers.getDefault().addSubTask(subtask);
        listTasks(Managers.getDefault());
        listEpicsAndSubs(Managers.getDefault());

        System.out.println("\n" + "Subtask-1 to DONE" + "\n");

        subtask = Managers.getDefault().getSubtaskById("Subtask-1");
        subtask.setStatus(Status.DONE);
        Managers.getDefault().updateSubtask(subtask);
        listEpicsAndSubs(Managers.getDefault());

        System.out.println("\n" + "Subtask-2 to DONE" + "\n");

        subtask = Managers.getDefault().getSubtaskById("Subtask-2");
        subtask.setStatus(Status.DONE);
        Managers.getDefault().updateSubtask(subtask);
        listEpicsAndSubs(Managers.getDefault());

        System.out.println("\n" + "Changing Epic-1 name and description" + "\n");

        epic = Managers.getDefault().getEpicById("Epic-1");
        epic.setName("This Epic is DONE");
        epic.setDescription("Testing attribute changes");
        Managers.getDefault().updateEpic(epic);
        subtask = Managers.getDefault().getSubtaskById("Subtask-2");
        subtask.setStatus(Status.IN_PROGRESS);
        Managers.getDefault().updateSubtask(subtask);
        listEpicsAndSubs(Managers.getDefault());

        System.out.println("\n" + "Deleting Subtask-1 & 2" + "\n");

        Managers.getDefault().deleteSubTask("Subtask-1");
        Managers.getDefault().deleteSubTask("Subtask-2");
        epic = Managers.getDefault().getEpicById("Epic-1");
        epic.setName("This Epic is NEW");
        epic.setDescription("No subtasks");
        Managers.getDefault().updateEpic(epic);
        listEpicsAndSubs(Managers.getDefault());

        System.out.println("\n" + "Listing all subtasks" + "\n");

        listAllSubtasks(Managers.getDefault());

        System.out.println("\n" + "Showing history" + "\n");
        System.out.println(Managers.getDefault().getTaskById("Task-1"));
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-1");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-1");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-1");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-1");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getTaskById("Task-2");
        Managers.getDefault().getSubtaskById("Subtask-3");
        System.out.println(Managers.getDefault().getEpicById("Epic-2"));
        for (Task task1 : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task1);
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
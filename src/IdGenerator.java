public final class IdGenerator {
    private IdGenerator() {
    }

    private static int taskIdNum = 0;
    private static int epicIdNum = 0;
    private static int subtaskIdNum = 0;
    private static int id;

    // Метод генерирования ИД в зависимости от типов задач
    public static String generateID(String taskType) {
        switch (taskType) {
            case "Task":
                id = ++taskIdNum;
                break;
            case "Epic":
                id = ++epicIdNum;
                break;
            case "Subtask":
                id = ++subtaskIdNum;
                break;
        }
        return (taskType + "-" + id);
    }
}
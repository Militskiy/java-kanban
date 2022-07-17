package managers.util;

public final class IdGenerator {
    private IdGenerator() {
    }

    private static int taskIdNum = 0;
    private static int epicIdNum = 0;
    private static int subtaskIdNum = 0;
    private static int id;

    // Метод генерирования ИД в зависимости от типов задач
    public static String generateID(TaskType type) {
        switch (type) {
            case TASK:
                id = ++taskIdNum;
                break;
            case EPIC:
                id = ++epicIdNum;
                break;
            case SUBTASK:
                id = ++subtaskIdNum;
                break;
        }
        return (type + "-" + id);
    }
}
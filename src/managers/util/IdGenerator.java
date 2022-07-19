package managers.util;

public final class IdGenerator {
    private IdGenerator() {
    }

    private static int taskIdNum = 0;
    private static int epicIdNum = 0;
    private static int subtaskIdNum = 0;
    private static int id;

    // Сеттеры и геттеры для корректной загрузки состояния из файла
    public static void setTaskIdNum(int taskIdNum) {
        IdGenerator.taskIdNum = taskIdNum;
    }

    public static void setEpicIdNum(int epicIdNum) {
        IdGenerator.epicIdNum = epicIdNum;
    }

    public static void setSubtaskIdNum(int subtaskIdNum) {
        IdGenerator.subtaskIdNum = subtaskIdNum;
    }

    public static int getTaskIdNum() {
        return taskIdNum;
    }

    public static int getEpicIdNum() {
        return epicIdNum;
    }

    public static int getSubtaskIdNum() {
        return subtaskIdNum;
    }

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
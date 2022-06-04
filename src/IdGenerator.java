public class IdGenerator {
    private int taskIdNum = 0;
    private int epicIdNum = 0;
    private int subtaskIdNum = 0;
    private int id;

    // Метод генерирования ИД в зависимости от типов задач
    public String generateID(String taskType) {
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

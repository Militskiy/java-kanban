package managers.util;

import tasks.Subtask;
import tasks.Task;

import static managers.util.Constants.DELIMITER;

public final class TaskToCSVString {
    private TaskToCSVString() {
    }
    public static String taskToCSVString(Task task) {
        if (task.getClass().equals(Subtask.class)) {
            return task.getId() + DELIMITER +
                    task.getType() + DELIMITER +
                    task.getName() + DELIMITER +
                    task.getStatus() + DELIMITER +
                    task.getDescription() + DELIMITER +
                    ((Subtask) task).getEpic().getId();
        }
        return task.getId() + DELIMITER +
                task.getType() + DELIMITER +
                task.getName() + DELIMITER +
                task.getStatus() + DELIMITER +
                task.getDescription();
    }
}

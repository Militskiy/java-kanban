package managers.util;

import tasks.Subtask;
import tasks.Task;

import static managers.util.Constants.DELIMITER;

public final class Converter {
    private Converter() {
    }

    public static String convertTaskToCSV(Task task) {
        if (task.getClass().equals(Subtask.class)) {
            if (task.getStartDate() == null) {
                return task.getId() + DELIMITER +
                        task.getType() + DELIMITER +
                        task.getName() + DELIMITER +
                        task.getStatus() + DELIMITER +
                        task.getDescription() + DELIMITER +
                        ((Subtask) task).getEpic().getId() + DELIMITER +
                        task.getStartDate() + DELIMITER +
                        task.getDuration();
            }
            return task.getId() + DELIMITER +
                    task.getType() + DELIMITER +
                    task.getName() + DELIMITER +
                    task.getStatus() + DELIMITER +
                    task.getDescription() + DELIMITER +
                    ((Subtask) task).getEpic().getId() + DELIMITER +
                    task.getStartDate().toString() + DELIMITER +
                    task.getDuration();
        }
        if (task.getStartDate() == null) {
            return task.getId() + DELIMITER +
                    task.getType() + DELIMITER +
                    task.getName() + DELIMITER +
                    task.getStatus() + DELIMITER +
                    task.getDescription() + DELIMITER +
                    "" + DELIMITER +
                    task.getStartDate() + DELIMITER +
                    task.getDuration();
        }
        return task.getId() + DELIMITER +
                task.getType() + DELIMITER +
                task.getName() + DELIMITER +
                task.getStatus() + DELIMITER +
                task.getDescription() + DELIMITER +
                "" + DELIMITER +
                task.getStartDate().toString() + DELIMITER +
                task.getDuration();
    }
}

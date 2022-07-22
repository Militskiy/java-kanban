package tasks.util;

import managers.exceptions.ValidationException;
import tasks.Task;

import java.util.Set;

public final class TaskValidator {
    private TaskValidator() {
    }

    public static void validateTask(Task task, Set<Task> sortedTaskMap, String cause) throws ValidationException {
        if (task.getStartDate() == null) {
            return;
        }
        for (Task v : sortedTaskMap) {
            if (v.getStartDate() == null) {
                return;
            } else if (!(task.getStartDate().plusMinutes(task.getDuration()).isBefore(v.getStartDate()) ||
                    task.getStartDate().isAfter(v.getStartDate().plusMinutes(v.getDuration())))) {
                throw new ValidationException(task, v, cause);
            }
        }
    }
}

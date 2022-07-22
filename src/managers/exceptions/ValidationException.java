package managers.exceptions;

import tasks.Task;

public class ValidationException extends Exception {
    private final Task task;
    private final Task taskFromSet;
    private final String cause;

    public ValidationException(Task task, Task taskFromSet, String cause) {
        this.task = task;
        this.taskFromSet = taskFromSet;
        this.cause = cause;
    }

    public String getDetailedMessage() {
        return "Cannot " + cause + " " + task.getType().toString().charAt(0) +
                task.getType().toString().toLowerCase().substring(1) + ": " +
                task.getType().toString().charAt(0) + task.getType().toString().toLowerCase().substring(1) +
                " " + task.getName() + " intersects with " +
                task.getType().toString().charAt(0) + task.getType().toString().toLowerCase().substring(1) +
                " " + taskFromSet.getName();
    }
}

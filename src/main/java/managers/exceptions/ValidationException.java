package managers.exceptions;

import tasks.Task;

public class ValidationException extends Exception {
    private final Task task;
    private final String cause;

    public ValidationException(Task task, String cause) {
        this.task = task;
        this.cause = cause;
    }

    public String getDetailedMessage() {
        return "Cannot " + cause + " " + task.getType().toString().charAt(0) +
                task.getType().toString().toLowerCase().substring(1) + ": " +
                task.getType().toString().charAt(0) + task.getType().toString().toLowerCase().substring(1) +
                " " + task.getName() + " intersects with another Task";
    }
}

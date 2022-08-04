package tasks;

import tasks.util.Status;
import tasks.util.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public Subtask(String id, TaskType type, String name, String description, Status status, Epic epic, LocalDateTime startDate, long duration) {
        super(id, type, name, description, status, startDate, duration);
        this.epic = epic;
    }

    public Subtask(TaskType type, String name, String description, Status status, LocalDateTime startDate, long duration, Epic epic) {
        super(type, name, description, status, startDate, duration);
        this.epic = epic;
    }

    public Subtask(TaskType type, String name, String description, Epic epic) {
        super(type, name, description);
        this.epic = epic;
    }

    @Override
    public String toString() {
        String stringStartDate;
        String stringEndDate;
        if (startDate == null) {
            stringStartDate = null;
            stringEndDate = null;
        } else {
            stringStartDate = startDate.toString();
            stringEndDate = startDate.plusMinutes(duration).toString();
        }
        return "ID: " + id + " {" +
                "Type: " + type + " | " +
                "Name: " + name + " | " +
                "Status: " + status + " | " +
                "Description: " + description + " | " +
                "Epic ID: " + epic.getId() + " | " +
                "Start Date: " + stringStartDate + " | " +
                "Duration: " + duration + " | " +
                "End date: " + stringEndDate +"}";
    }
}
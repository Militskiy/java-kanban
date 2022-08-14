package tasks;

import tasks.util.Status;
import tasks.util.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private final String epicId;

    public String getEpicId() {
        return epicId;
    }

    public Subtask(String id, TaskType type, String name, String description, Status status, String epicId, LocalDateTime startDate, long duration) {
        super(id, type, name, description, status, startDate, duration);
        this.epicId = epicId;
    }

    public Subtask(TaskType type, String name, String description, Status status, LocalDateTime startDate, long duration, String epicId) {
        super(type, name, description, status, startDate, duration);
        this.epicId = epicId;
    }

    public Subtask(TaskType type, String name, String description, String epicId) {
        super(type, name, description);
        this.epicId = epicId;
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
                "Epic ID: " + epicId + " | " +
                "Start Date: " + stringStartDate + " | " +
                "Duration: " + duration + " | " +
                "End date: " + stringEndDate +"}";
    }
}
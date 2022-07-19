package tasks;

import tasks.util.Status;
import tasks.util.TaskType;

public class Subtask extends Task {

    private final Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public Subtask(String id, TaskType type, String name, String description, Status status, Epic epic) {
        super(id, type, name, description, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "ID: " + id + " {" +
                "Type: " + type + " | " +
                "Name: " + name + " | " +
                "Status: " + status + " | " +
                "Description: " + description + " | " +
                "Epic ID: " + epic.getId() + "}";
    }
}
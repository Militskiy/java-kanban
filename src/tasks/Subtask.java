package tasks;

import managers.util.Status;

public class Subtask extends Task {

    private final String epicId;

    public String getEpicId() {
        return epicId;
    }

    public Subtask(String id, String name, String description, Status status, String epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId='" + epicId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
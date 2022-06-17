package tasks;

import managers.util.Status;

public class Subtask extends Task {

    private final Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public Subtask(String id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId='" + epic.getId() + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
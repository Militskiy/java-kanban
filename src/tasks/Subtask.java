package tasks;

import managers.util.Status;
import managers.util.TaskType;

import static managers.util.Constants.DELIMITER;

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
        return id + DELIMITER +
                type + DELIMITER +
                name + DELIMITER +
                status + DELIMITER +
                description + DELIMITER +
                epic.getId();
    }
}
package tasks;

import managers.util.Constants;
import managers.util.Status;
import managers.util.TaskType;

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
        return id + Constants.DELIMITER +
                type + Constants.DELIMITER +
                name + Constants.DELIMITER +
                status + Constants.DELIMITER +
                description + Constants.DELIMITER +
                epic.getId();
    }
}
package tasks;

import managers.util.Constants;
import managers.util.Status;
import managers.util.TaskType;

public class Task {

    protected String id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String id, TaskType type, String name, String description, Status status) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String id, TaskType type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + Constants.DELIMITER +
                type + Constants.DELIMITER +
                name + Constants.DELIMITER +
                status + Constants.DELIMITER +
                description;
    }
}

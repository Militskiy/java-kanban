package tasks;

import managers.util.Constants;
import managers.util.Status;
import managers.util.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtaskList = new ArrayList<>();

    public Epic(String id, TaskType type, String name, String description) {
        super(id, type, name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public Epic(String id, TaskType type, String name, String description, Status status) {
        super(id, type, name, description, status);
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
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
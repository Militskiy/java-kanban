package tasks;

import tasks.util.Status;
import tasks.util.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<String> subtaskList = new ArrayList<>();
    private LocalDateTime endDate = null;

    public Epic(String id, TaskType type, String name, String description) {
        super(id, type, name, description);
    }

    public void addSubtask(String id) {
        subtaskList.add(id);
    }


    public Epic(String id, TaskType type, String name, String description, Status status, LocalDateTime startDate,
                long duration, LocalDateTime endDate) {
        super(id, type, name, description, status, startDate, duration);
        this.endDate = endDate;
    }


    public Epic(TaskType type, String name, String description) {
        super(type, name, description);
    }

    @Override
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void clearSubTaskList() {
        subtaskList.clear();
    }

    public List<String> getSubtaskIdList() {
        return subtaskList;
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
                "Start Date: " + stringStartDate + " | " +
                "Duration: " + duration + " | " +
                "End date: " + stringEndDate + "}";
    }
}
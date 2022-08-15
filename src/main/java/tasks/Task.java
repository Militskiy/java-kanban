package tasks;

import com.google.common.base.Objects;
import tasks.util.Status;
import tasks.util.TaskType;

import java.time.LocalDateTime;

public class Task {

    protected String id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected Status status;
    protected LocalDateTime startDate = null;
    protected long duration;

    public Task(String id, TaskType type, String name, String description, Status status, LocalDateTime startDate, long duration) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.duration = duration;
    }

    public Task(TaskType type, String name, String description, Status status, LocalDateTime startDate, long duration) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.duration = duration;
    }

    public Task(String id, TaskType type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Task(TaskType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return startDate.plusMinutes(duration);
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getDuration() == task.getDuration() &&
                Objects.equal(getId(), task.getId()) &&
                getType() == task.getType() &&
                Objects.equal(getName(), task.getName()) &&
                Objects.equal(getDescription(), task.getDescription()) &&
                getStatus() == task.getStatus() &&
                Objects.equal(getStartDate(), task.getStartDate());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(),
                getType(),
                getName(),
                getDescription(),
                getStatus(),
                getStartDate(),
                getDuration());
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

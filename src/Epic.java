import java.util.LinkedHashMap;

public class Epic extends Task {

    private final LinkedHashMap<String, Subtask > subtaskList = new LinkedHashMap<>();

    public Epic(String id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtask (Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
    }

    public LinkedHashMap<String, Subtask> getSubtaskList() {
        return subtaskList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
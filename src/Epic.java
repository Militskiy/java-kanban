import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtaskList = new LinkedList<>();

    public Epic(String id, String name, String description) {
        super(id, name, description);
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

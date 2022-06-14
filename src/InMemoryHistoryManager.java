import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyList = new ArrayList<>();

    // Реализация метода вывода истории последних 10 задач
    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    // Реализация метода добавления задачи в историю
    @Override
    public void add(Task task) {
        if (historyList.size() < 10) {
            historyList.add(task);
        } else {
            historyList.remove(0);
            historyList.add(task);
        }
    }
}

import java.util.List;

public interface HistoryManager {
    // Метод вывода истории последних 10 задач
    List<Task> getHistory();
}

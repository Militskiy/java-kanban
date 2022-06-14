package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    // Метод вывода истории последних 10 задач
    List<Task> getHistory();
    // Метод добавления задачи в историю
    void add(Task task);
}

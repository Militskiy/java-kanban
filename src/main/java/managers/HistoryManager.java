package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    // Метод вывода истории задач
    List<Task> getHistory();

    // Метод добавления задачи в историю
    void add(Task task);
    void addLast(Task task);

    // Метод удаления задачи из истории
    void remove(List<String> idList);
}

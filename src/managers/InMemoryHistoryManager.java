package managers;

import managers.util.CustomLinkedList;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyList = new CustomLinkedList<>();

    // Реализация метода вывода истории задач
    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList.getTasks());
    }

    // Реализация метода добавления задачи в историю
    @Override
    public void add(Task task) {
        historyList.linkFirst(task);
    }

    // Реализация метода удаления задач из истории (список для ускорения методов удаления всех задач)
    @Override
    public void remove(List<String> idList) {
        idList.forEach(historyList::removeTask);
    }
}

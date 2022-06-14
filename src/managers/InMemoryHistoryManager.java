package managers;

import tasks.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyList = new LinkedList<>();

    // Реализация метода вывода истории последних 10 задач
    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }

    // Реализация метода добавления задачи в историю, первый в списке последний добавленный элемент
    @Override
    public void add(Task task) {
        if (historyList.size() < 10) {
            historyList.addFirst(task);
        } else {
            historyList.removeLast();
            historyList.addFirst(task);
        }
    }
}

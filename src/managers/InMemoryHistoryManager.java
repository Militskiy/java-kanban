package managers;

import managers.util.CustomLinkedList;
import managers.util.Node;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyList = new CustomLinkedList<>();
    private final Map<String, Node<Task>> nodeMap = new HashMap<>();


    // Реализация метода вывода истории задач
    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList.getTasks());
    }

    // Реализация метода добавления задачи в историю
    @Override
    public void add(Task task) {
        if (nodeMap.get(task.getId()) == null) {
            nodeMap.put(task.getId(), historyList.linkFirst(task));
        } else {
            historyList.removeNode(nodeMap.get(task.getId()));
            nodeMap.put(task.getId(), historyList.linkFirst(task));
        }
    }

    // Реализация метода удаления задач из истории (список для ускорения методов удаления всех задач)
    @Override
    public void remove(List<String> idList) {
        idList.forEach(id -> {
            historyList.removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        });
    }
}

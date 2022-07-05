package managers;

import managers.util.Node;
import tasks.Task;

import java.util.*;

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
            nodeMap.put(task.getId(), historyList.linkLast(task));
        } else {
            historyList.removeNode(nodeMap.get(task.getId()));
            nodeMap.put(task.getId(), historyList.linkLast(task));
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

    // Внутренний класс аналог LinkedList
    private static class CustomLinkedList<T> {


        private Node<T> first;
        private Node<T> last;

        // Внутренний метод добавления элемента в конец списка
        private Node<T> linkLast(T element) {
            final Node<T> oldLast = last;
            final Node<T> newNode = new Node<>(oldLast, element, null);
            last = newNode;
            if (oldLast == null) {
                first = newNode;
            } else {
                oldLast.next = newNode;
            }
            return newNode;
        }
        // Внутренний метод получения списка задач в истории
        private ArrayList<Task> getTasks() {
            ArrayList<Task> result = new ArrayList<>();
            for (Node<T> x = first; x != null; x = x.next) {
                result.add((Task) x.data);
            }
            return result;
        }

        // Внутренний метод удаления ноды из списка
        private void removeNode(Node<T> node) {
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;
            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.data = null;
        }

    }
}

package managers.util;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomLinkedList<T extends Task> {

    private final Map<String, Node<T>> nodeMap = new HashMap<>();
    private Node<T> first;
    private Node<T> last;

    // Метод добавления элемента в начало списка
    public void linkFirst(T element) {
        final Node<T> oldHead = first;
        final Node<T> newNode = new Node<>(null, element, oldHead);
        first = newNode;
        if (oldHead == null) {
            last = newNode;
        } else {
            oldHead.prev = newNode;
        }
        if (nodeMap.get(element.getId()) == null) {
            nodeMap.put(element.getId(), newNode);
        } else {
            removeTask(element.getId());
            nodeMap.put(element.getId(), newNode);
        }
    }

    // Метод добавления элемента в конец списка
    public void linkLast(T element) {
        final Node<T> oldLast = last;
        final Node<T> newNode = new Node<>(oldLast, element, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        if (nodeMap.get(element.getId()) == null) {
            nodeMap.put(element.getId(), newNode);
        } else {
            removeTask(element.getId());
            nodeMap.put(element.getId(), newNode);
        }
    }

    // Метод получения списка задач в истории
    public ArrayList<T> getTasks() {
        ArrayList<T> result = new ArrayList<>();
        for (Node<T> x = first; x != null; x = x.next) {
            result.add(x.data);
        }
        return result;
    }

    // Метод удаления ноды из списка
    public void removeTask(String id) {
        final Node<T> node = nodeMap.get(id);
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
    private static class Node<E extends Task> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}

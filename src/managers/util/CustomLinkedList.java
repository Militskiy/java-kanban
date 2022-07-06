package managers.util;

import tasks.Task;

import java.util.ArrayList;

public class CustomLinkedList<T> {


    private Node<T> first;
    private Node<T> last;

    // Метод добавления элемента в начало списка
    public Node<T> linkFirst(T element) {
        final Node<T> oldHead = first;
        final Node<T> newNode = new Node<>(null, element, oldHead);
        first = newNode;
        if (oldHead == null) {
            last = newNode;
        } else {
            oldHead.prev = newNode;
        }
        return newNode;
    }

    // Метод добавления элемента в конец списка
    public Node<T> linkLast(T element) {
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

    // Метод получения списка задач в истории
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Node<T> x = first; x != null; x = x.next) {
            result.add((Task) x.data);
        }
        return result;
    }

    // Метод удаления ноды из списка
    public void removeNode(Node<T> node) {
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
